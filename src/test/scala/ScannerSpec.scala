package xyz.minond.talk.pti

import Token._
import org.scalatest._

class ExampleSpec extends FlatSpec with Matchers {
  def scan(src: String) =
    new Scanner(src) toList

  "The Scanner" should "handle empty input" in {
    scan("") should be(List())
    scan(" ") should be(List())
    scan("          ") should be(List())
  }

  it should "tokenize identifiers" in {
    scan("name") should be(List(Token(IDENTIFIER, Some("name"))))
    scan("dot.dot") should be(List(Token(IDENTIFIER, Some("dot.dot"))))
    scan("dash-dash") should be(List(Token(IDENTIFIER, Some("dash-dash"))))
    scan("obj->obj") should be(List(Token(IDENTIFIER, Some("obj->obj"))))
  }

  it should "tokenize strings" in {
    val twostrs = List(
      Token(STRING, Some("1 2 3")),
      Token(STRING, Some("4 5 6"))
    )

    scan(""""1 2 3"""") should be(List(Token(STRING, Some("1 2 3"))))
    scan(""""1 2 3""4 5 6"""") should be(twostrs)
    scan(""""1 2 3"   "4 5 6"""") should be(twostrs)
  }

  it should "tokenize strings with escaped quotes" in {
    scan(""""\"1 \"2 \"3"""") should be(
      List(Token(STRING, Some("""\"1 \"2 \"3"""))))
  }

  it should "tokenize invalid strings that do not end with quotes" in {
    scan(""""""") should be(List(Token(INVALID, Some(""))))
    scan(""""123""") should be(List(Token(INVALID, Some("123"))))
  }

  it should "tokenize integers" in {
    scan("1") should be(List(Token(INTEGER, Some("1"))))
    scan("123") should be(List(Token(INTEGER, Some("123"))))
    scan("0123456789") should be(List(Token(INTEGER, Some("0123456789"))))
    scan("9876543210") should be(List(Token(INTEGER, Some("9876543210"))))
  }

  it should "tokenize real numbers" in {
    scan("0.0001") should be(List(Token(REAL, Some("0.0001"))))
    scan("1.0") should be(List(Token(REAL, Some("1.0"))))
    scan("9.999") should be(List(Token(REAL, Some("9.999"))))
  }

  it should "tokenize invalid numbers as errors" in {
    scan("0.000.1") should be(List(Token(INVALID, Some("0.000.1"))))
  }

  it should "tokenize parentheses" in {
    scan("()") should be(List(Token(OPEN_PAREN), Token(CLOSE_PAREN)))
    scan("((()))") should be(
      List(Token(OPEN_PAREN),
           Token(OPEN_PAREN),
           Token(OPEN_PAREN),
           Token(CLOSE_PAREN),
           Token(CLOSE_PAREN),
           Token(CLOSE_PAREN)))
  }

  it should "tokenize content inside of parentheses" in {
    scan("(123)") should be(
      List(Token(OPEN_PAREN), Token(INTEGER, Some("123")), Token(CLOSE_PAREN)))

    scan("(((123)))") should be(
      List(Token(OPEN_PAREN),
           Token(OPEN_PAREN),
           Token(OPEN_PAREN),
           Token(INTEGER, Some("123")),
           Token(CLOSE_PAREN),
           Token(CLOSE_PAREN),
           Token(CLOSE_PAREN)))
  }

  it should "tokenize content separated by spaces that is inside of parentheses" in {
    scan("(+ 1 2)") should be(
      List(Token(OPEN_PAREN),
           Token(IDENTIFIER, Some("+")),
           Token(INTEGER, Some("1")),
           Token(INTEGER, Some("2")),
           Token(CLOSE_PAREN)))

    scan("(((+ 1 2)))") should be(
      List(
        Token(OPEN_PAREN),
        Token(OPEN_PAREN),
        Token(OPEN_PAREN),
        Token(IDENTIFIER, Some("+")),
        Token(INTEGER, Some("1")),
        Token(INTEGER, Some("2")),
        Token(CLOSE_PAREN),
        Token(CLOSE_PAREN),
        Token(CLOSE_PAREN)
      ))

    scan("(((+ 1 (* 2 3))))") should be(
      List(
        Token(OPEN_PAREN),
        Token(OPEN_PAREN),
        Token(OPEN_PAREN),
        Token(IDENTIFIER, Some("+")),
        Token(INTEGER, Some("1")),
        Token(OPEN_PAREN),
        Token(IDENTIFIER, Some("*")),
        Token(INTEGER, Some("2")),
        Token(INTEGER, Some("3")),
        Token(CLOSE_PAREN),
        Token(CLOSE_PAREN),
        Token(CLOSE_PAREN),
        Token(CLOSE_PAREN)
      ))
  }
}
