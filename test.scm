;;; Standard Library Tests

;; Core

(assert (boolean? #f))
(assert (boolean? #t))
(assert (builtin? eval))
(assert (error? (error 'ok)))
(assert (false? #f))
(assert (integer? 123))
(assert (lambda? (lambda () 'ok)))
(assert (list? '()))
(assert (list? '(1 2 3)))
(assert (list? (list 1 2 3)))
(assert (list? (list)))
(assert (null? '()))
(assert (pair? (cons 1 2)))
(assert (quote? 'ok))
(assert (real? 1.2))
(assert (string? "ok"))
(assert (true? #t))
(assert (zero? 0))

(assert-eq '(2 4 6 8) (map double '(1 2 3 4)))
(assert-eq 10 (fold 0 + '(1 2 3 4)))

;; List and Pair functions

(assert-eq '(1 2 3) (list 1 2 3))
(assert-eq '(1 2 3) (list (+ 1) (+ 1 1) (+ 1 1 1)))
