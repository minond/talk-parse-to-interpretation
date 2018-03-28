;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Standard Library
;;
;; 1. Internal functions
;; 2. Core functions
;; 3. List and pars
;; 4. Math
;; 5. Test helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Internal functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define thunk/equal?
  (lambda (a)
    (lambda (b)
      (equal? a b))))

(define thunk/type/equal?
  (lambda (t)
    (lambda (x)
      (equal? (type/name x) t))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Core functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define boolean? (thunk/type/equal? "boolean"))
(define builtin? (thunk/type/equal? "builtin"))
(define error? (thunk/type/equal? "error"))
(define false? (thunk/equal? #f))
(define integer? (thunk/type/equal? "integer"))
(define lambda? (thunk/type/equal? "lambda"))
(define list? (thunk/type/equal? "sexpr"))
(define pair? (thunk/type/equal? "pair"))
(define quote? (thunk/type/equal? "quote"))
(define real? (thunk/type/equal? "real"))
(define string? (thunk/type/equal? "string"))
(define true? (thunk/equal? #t))
(define zero? (thunk/equal? 0))

(define null?
  (lambda (xs)
    (equal? (list) xs)))

(define map
  (lambda (f xs)
    (cond
      ((null? xs) '())
      (#t (cons (f (car xs)) (map f (cdr xs)))))))

(define fold
  (lambda (id f xs)
    (cond
      ((null? xs) id)
      (#t (fold (f id (car xs)) f (cdr xs))))))

(define filter
  (lambda (f xs)
    (cond
      ((null? xs) '())
      ((f (car xs))
       (cons (car xs)
             (filter f (cdr xs))))
      (#t (filter f (cdr xs))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; List and pars
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define list
  (lambda (. xs) xs))

(define first
  (lambda (xs)
    (cond
      ((null? xs) (error "first expects a non-empty list"))
      (#t (car xs)))))

(define second
  (lambda (xs)
    (cond
      ((null? xs) (error "second expects a two-item list"))
      ((null? (cdr xs)) (error "second expects a two-item list"))
      (#t (car (cdr xs))))))

(define third
  (lambda (xs)
    (cond
      ((null? xs) (error "third expects a three-item list"))
      ((null? (cdr xs)) (error "third expects a three-item list"))
      ((null? (cdr (cdr xs))) (error "third expects a three-item list"))
      (#t (car (cdr (cdr xs)))))))

(define length
  (lambda (xs)
    (cond
      ((null? xs) 0)
      (#t (+ 1 (length (cdr xs)))))))

(define nth
  (lambda (i xs)
    (cond
      ((null? xs) '())
      ((zero? i) (car xs))
      (#t (nth (dec i) (cdr xs))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Math
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define double
  (lambda (x)
    (+ x x)))

(define triple
  (lambda (x)
    (+ x x x)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define ok? (thunk/equal? 'ok))

(define assert
  (lambda (ret)
    (cond
      ((true? ret) 'ok)
      (#t (error "Assertion error")))))

(define assert-eq
  (lambda (expected ret)
    (assert (equal? expected ret))))