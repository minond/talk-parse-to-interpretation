package xyz.minond.talk.pti

import Statement._
import Token.{IDENTIFIER, POUND, INTEGER, REAL, OPEN_PAREN, CLOSE_PAREN, QUOTE}

object Parser {
  object Error {
    val STR_INVALID_INT = "Cannot parse integer number."
    val STR_INVALID_REAL = "Cannot parse real number."
    val STR_INVALID_SEXPR = "Cannot parse s-expression."

    val STR_INVALID_QUOTE = "Cannot parse quoted expression."
    val STR_INVALID_NIL_QUOTE = "Cannot parse empty quote expression."

    val STR_INVALID_IDENTIFIER = "Cannot parse identifier."
    val STR_INVALID_NIL_IDENTIFIER = "Empty identifier value."

    val STR_INVALID_BOOL = "Cannot parse boolean value."
    def STR_INVALID_BOOL_TOK(token: Token) =
      s"Expecting either 'f' or 't' but found ${token} instead."

    val STR_INVALID_TOK = "Cannot parse invalid token."
    def STR_UNEXPECTED_TOK(token: Token) =
      s"Found unexpected token: ${token}"
    def STR_EXPECTING_ONE_OF(ids: Token.Id*) =
      s"Expecting (one of): ${ids.mkString(", ")}."
  }
}

/*
 * Grammar:
 *
 * MAIN     = { stmt } ;
 * stmt     = "'" stmt | sexpr | value ;
 * sexpr    = "(" { value } ")" ;
 * value    = IDENTIFIER | NUMBER | boolean ;
 * boolean  = "#" ( "f" | "t" ) ;
 */
class Parser(source: Tokenizer) extends Iterator[Either[Error, Statement]] {
  val tokens = source.buffered

  var curr =
    if (tokens.hasNext) tokens.head
    else Left(Token.Error("EOF"))

  def hasNext(): Boolean =
    tokens.hasNext

  def next(): Either[Error, Statement] = {
    tokens.head match {
      case Right(Token(POUND, _))      => parseBoolean
      case Right(Token(INTEGER, _))    => parseInteger
      case Right(Token(REAL, _))       => parseReal
      case Right(Token(IDENTIFIER, _)) => parseIdentifier
      case Right(Token(QUOTE, _))      => parseQuote
      case Right(Token(OPEN_PAREN, _)) => parseSExpr

      case Right(Token(CLOSE_PAREN, _)) =>
        skip
        Left(Error(Parser.Error.STR_UNEXPECTED_TOK(Token(CLOSE_PAREN))))

      case Right(token) =>
        skip
        Left(Error(Parser.Error.STR_UNEXPECTED_TOK(token)))

      case Left(Token.Error(msg, _)) =>
        skip
        Left(Error(Parser.Error.STR_INVALID_TOK, Some(Error(msg))))
    }
  }

  def eat() = {
    curr = tokens.head
    skip
    curr
  }

  def skip() =
    if (tokens.hasNext) tokens.next

  def expect(ids: Token.Id*): Either[Error, Token] = {
    eat match {
      case Right(token) if ids contains token.id =>
        Right(token)

      case Left(Token.Error(msg, _)) =>
        Left(Error(Parser.Error.STR_INVALID_TOK, Some(Error(msg))))

      case Right(token) =>
        Left(
          Error(Parser.Error.STR_UNEXPECTED_TOK(token),
                Some(Error(Parser.Error.STR_EXPECTING_ONE_OF(ids: _*)))))
    }
  }

  def parseBoolean() = {
    (expect(POUND), expect(IDENTIFIER)) match {
      case (Left(err), _) => Left(Error(Parser.Error.STR_INVALID_BOOL, Some(err)))
      case (_, Left(err)) => Left(Error(Parser.Error.STR_INVALID_BOOL, Some(err)))

      case (Right(_), Right(Token(_, Some("t")))) => Right(BooleanStmt(true))
      case (Right(_), Right(Token(_, Some("f")))) => Right(BooleanStmt(false))

      case (Right(_), Right(token)) =>
        Left(
          Error(Parser.Error.STR_INVALID_BOOL,
                Some(Error(Parser.Error.STR_INVALID_BOOL_TOK(token)))))
    }
  }

  def parseInteger() = {
    (expect(INTEGER), curr.map { _.lexeme.getOrElse("").toInt }) match {
      case (Left(err), _) =>
        Left(Error(Parser.Error.STR_INVALID_INT, Some(err)))

      case (_, Left(err)) =>
        Left(Error(Parser.Error.STR_INVALID_INT, Some(Error(err.message))))

      case (Right(_), Right(value)) =>
        Right(IntegerNumberStmt(value))
    }
  }

  def parseReal() = {
    (expect(REAL), curr.map { _.lexeme.getOrElse("").toDouble }) match {
      case (Left(err), _) =>
        Left(Error(Parser.Error.STR_INVALID_REAL, Some(err)))

      case (_, Left(err)) =>
        Left(Error(Parser.Error.STR_INVALID_REAL, Some(Error(err.message))))

      case (Right(_), Right(value)) =>
        Right(RealNumberStmt(value))
    }
  }

  def parseIdentifier() = {
    expect(IDENTIFIER) match {
      case Left(err) =>
        Left(Error(Parser.Error.STR_INVALID_IDENTIFIER, Some(err)))

      case Right(Token(_, None)) =>
        Left(
          Error(Parser.Error.STR_INVALID_IDENTIFIER,
                Some(Error(Parser.Error.STR_INVALID_NIL_IDENTIFIER))))

      case Right(Token(_, Some(value))) =>
        Right(IdentifierStmt(value))
    }
  }

  def parseSExpr(): Either[Error, Statement] = {
    expect(OPEN_PAREN) match {
      case Left(err) =>
        Left(Error(Parser.Error.STR_INVALID_SEXPR, Some(err)))

      case Right(_) =>
        var head: Option[Statement] = None
        var tail: List[Statement] = List()

        while (tokens.hasNext && tokens.head != Right(Token(CLOSE_PAREN))) {
          next match {
            case Left(err) =>
              return Left(Error(Parser.Error.STR_INVALID_SEXPR, Some(err)))

            case Right(stmt) =>
              if (head.isEmpty) head = Some(stmt)
              else tail = tail ++ List(stmt)
          }
        }

        expect(CLOSE_PAREN) match {
          case Left(err) => Left(Error(Parser.Error.STR_INVALID_SEXPR, Some(err)))
          case Right(_)  => Right(SExprStmt(head, tail))
        }
    }
  }

  def parseQuote() = {
    (expect(QUOTE), hasNext) match {
      case (Left(err), _) => Left(Error(Parser.Error.STR_INVALID_QUOTE, Some(err)))
      case (_, false)     => Left(Error(Parser.Error.STR_INVALID_NIL_QUOTE))

      case (Right(_), true) =>
        next match {
          case Left(err)   => Left(Error(Parser.Error.STR_INVALID_QUOTE, Some(err)))
          case Right(stmt) => Right(QuoteStmt(stmt))
        }
    }
  }
}
