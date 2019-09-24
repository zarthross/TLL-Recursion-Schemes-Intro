Lets talk about `Fixed Point`

=========================

> In mathematics, a fixed point of a function is an element of the function's domain that is mapped to itself by the function. That is to say, c is a fixed point of the function f(x) if f(c) = c.

-- Wikipedia

=========================

```tut:book
// type Fix[F[_]] = F[Fix[F]]
case class Fix[F[_]](unfix: F[Fix[F]])
```

=========================

```tut:book:silent
trait ListF[+A, +B]

case object NilF extends ListF[Nothing, Nothing]
def nilF[A, B]: ListF[A, B] = NilF

case class ConsF[A, B](head: A, tail: B) extends ListF[A, B]
def consF[A, B](head: A, tail: B): ListF[A, B] =
  ConsF(head, tail)
```

Note:
This is our list.

note the smart constructors

=========================

```tut:book
type List[A] = Fix[ListF[A, *]]
def nil[A] = Fix[ListF[A, *]](NilF)
def cons[A](a: A, tail: List[A]): List[A] = Fix(ConsF(a, tail))
```

=========================

```tut:book
val data = ConsF(9, ConsF(10, ConsF(7, NilF)))

val dataFixed = cons(9, cons(10, cons(7, nil)))
```

Note:
So why do this?
Because now the height of the list is known at compile time.
We can control the tail type.

=========================

```tut:book:silent
import cats._, cats.implicits._
implicit def traverseListF[T]: Traverse[ListF[T, *]] =
new Traverse[ListF[T, *]] {
def foldLeft[A, B](fa: ListF[T, A], b: B)(f: (B, A) => B): B =
  fa match {
    case NilF => b
    case ConsF(_, tail) => f(b, tail)
  }

def foldRight[A, B](fa: ListF[T, A], lb: Eval[B])(
    f: (A, Eval[B]) => Eval[B]): Eval[B] =
  fa match {
    case NilF => lb
    case ConsF(_, tail) => f(tail, lb)
  }

def traverse[G[_]: Applicative, A, B](fa: ListF[T, A])
  (f: (A) => G[B]): G[ListF[T, B]] =
    fa match {
      case NilF => NilF.pure[G].widen[ListF[T, B]]
      case ConsF(head, tail) => f(tail).map(ConsF(head, _))
    }
}
```
<!-- .element: class="stretch" -->

Note:
This code allows us to easily (generically) modify
the tail of a list.  a traverse is a kind of `Functor`

Hence `ListF`

===========================

```tut:book

consF(1, 3).map(_ * 2)
```

===========================

Putting it all together

```tut:book:silent
type Algebra[F[_], A] =  F[A] => A
type Coalgebra[F[_], A] = A => F[A]

def idCoalgebra[F[_]]: Coalgebra[F, Fix[F]] = _.unfix
def idAlgebra[F[_]]: Algebra[F, Fix[F]] = Fix.apply

def hylo[F[_]: Functor, A, B](
      algebra: Algebra[F, B],
      coalgebra: Coalgebra[F, A]
  ): A => B =
    new (A => B) {
      def apply(a: A): B = algebra(coalgebra(a).map(this))
    }
```
<!-- .element: class="stretch" -->


===========================

```tut:book
val sumAlgebra: Algebra[ListF[Int, *], Int] =  {
  case ConsF(head, tailSum) => head + tailSum
  case NilF                 => 0
}
val dataFixed = cons(9, cons(10, cons(7, nil)))
val sumOf = hylo(sumAlgebra, idCoalgebra[ListF[Int, *]])
sumOf(dataFixed)
```
<!-- .element: class="stretch" -->

===========================

```tut:book
val rangeCoalgebra: Coalgebra[ListF[Int, *], Int] = { n =>
  if(n == 0) NilF
  else ConsF(n-1, n - 1)
}
val rangeOf = hylo(idAlgebra[ListF[Int, *]], rangeCoalgebra)
rangeOf(6)
```
<!-- .element: class="stretch" -->
