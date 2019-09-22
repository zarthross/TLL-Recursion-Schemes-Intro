### So, what is a 'Recursion Scheme'?

=====================================

`map`/`flatMap`/`filter` are to `goto`

as <!-- .element: class="fragment" data-fragment-index="1" -->

Recursion Schemes are to Recursion <!-- .element: class="fragment" data-fragment-index="2" -->

Note:
That is to say, Recursion schemes formalize patterns of
recursion so we don't have to repeat ourselves.
Also, makes code more readable.

=====================================

Lets get down to brass tax....

Example time! <!-- .element: class="fragment" data-fragment-index="1" -->

=====================================

Fibonacci values

```tut:invisible
import scala.annotation.tailrec
import higherkindness.droste._
import higherkindness.droste.data._
import cats.implicits._
```

```tut:book
def fibR(x: BigInt): BigInt = {
  @tailrec
  def go(x: BigInt, prev: BigInt, next: BigInt): BigInt =
    x match {
      case n if n == BigInt(0) => prev
      case n if n == BigInt(1) => next
      case _ => go(x-1, next, (next + prev))
    }
  go(x, 0, 1)
}

fibR(10)
```

Note:
What happens if I forget to subtract `1` from `x`?

======================================

What are some problems here?

You have mixed concerns: <!-- .element: class="fragment" data-fragment-index="1" -->
- How to loop<!-- .element: class="fragment" data-fragment-index="2" -->
- What each iteration does<!-- .element: class="fragment" data-fragment-index="3" --> 


=======================================

Schemed Fibonacci part 1

Separation of concerns.

```tut:book:silent
val natCoalgebra: Coalgebra[Option, BigInt] =
  Coalgebra(n => if (n > 0) Some(n - 1) else None)

val fibAlgebra: CVAlgebra[Option, BigInt] = CVAlgebra {
  case Some(r1 :< Some(r2 :< _)) => r1 + r2
  case Some(_ :< None)           => 1
  case None                      => 0
}
```

Note:
`:<` is from `Attr`
`natCoalgebra` counts down
`fibAlgebra` adds the last 2 together


=========================

Schemed Fibonacci part 2

Putting it together

```tut:book
val fib: BigInt => BigInt = scheme.ghylo(
  fibAlgebra.gather(Gather.histo),
  natCoalgebra.scatter(Scatter.ana))

fib(10)
```

Note:
Witty "with our powers combined giphy here"
