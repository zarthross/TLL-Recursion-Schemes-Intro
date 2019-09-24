```tut:invisible
import scala.annotation.tailrec
import higherkindness.droste._
import higherkindness.droste.data._
import higherkindness.droste.data.list._
import cats.implicits._
```
> Them's alot of fancy words

-- You


Note:
Now  your probably thinking to yourself....

========================

'Coalgebra' ...

========================

'ghylo'

========================

What the function am I talking about?

========================

'Algebra'


Note:
Like arithmetic

What we mean is some operations on some 'thing'

========================

'CoAlgebra'

Example:  <!-- .element: class="fragment" data-fragment-index="1" -->
 - <!-- .element: class="fragment" data-fragment-index="2" --> If a `Monad` has `pure` and `flatMap`
 -
  <!-- .element: class="fragment" data-fragment-index="3" -->  a `CoMonad` has `extract` and `coFlatMap`

Note:
The dual of an algebra.

========================

How I think about it:

Algebras add things together <!-- .element: class="fragment" data-fragment-index="1" -->

CoAlgebras take things apart <!-- .element: class="fragment" data-fragment-index="2" -->


========================

'Morphism'

Ancient Greek: <!-- .element: class="fragment" data-fragment-index="1" -->
morphe 'shape, form' <!-- .element: class="fragment" data-fragment-index="2" -->

Note:
A transformation from one thing to another thing

========================

'Catamorphism'

Ancient Greek: <!-- .element: class="fragment" data-fragment-index="1" -->

kata 'downwards, into' <!-- .element: class="fragment" data-fragment-index="2" -->

like 'catastrophe' <!-- .element: class="fragment" data-fragment-index="3" -->

========================

'Catamorphism'

`F[A] => A`

```tut:book
val sumAlgebra: Algebra[ListF[Int, *], Int] = Algebra {
  case ConsF(head, tailSum) => head + tailSum
  case NilF                 => 0
}
val sumOf = scheme.cata(sumAlgebra)
sumOf(List.range(0,10))
```

Note:
Classic Fold

========================

'Anamorphism'

Ancient Greek: <!-- .element: class="fragment" data-fragment-index="1" -->

ana 'up, upwards, along' <!-- .element: class="fragment" data-fragment-index="2" -->

like <!-- .element: class="fragment" data-fragment-index="3" -->

... I got nothing <!-- .element: class="fragment" data-fragment-index="4" -->

========================

'Anamorphism'

`F[A] => A`

```tut:book
val rangeCoalgebra: Coalgebra[ListF[Int, *], Int] = Coalgebra {
  case 0 => NilF
  case n => ConsF(n, n - 1)
}
val upTo = scheme.ana(rangeCoalgebra)
upTo(10)
```

Note:
an 'unfold'

========================

`Hylomorphism`

Ancient Greek: <!-- .element: class="fragment" data-fragment-index="1" -->

hylo- 'matter, wood' <!-- .element: class="fragment" data-fragment-index="2" -->

like hylophobia <!-- .element: class="fragment" data-fragment-index="3" -->

Note:
hylophobia: Fear of forests or wooded areas

========================

`Hylomorphism`

Do a Catamorphism then an Anamorphism <!-- .element: class="fragment" data-fragment-index="1" -->

Build it up, then combine it together  <!-- .element: class="fragment" data-fragment-index="2" -->

========================

![Morphisms](https://raw.githubusercontent.com/slamdata/matryoshka/master/resources/recursion-schemes.png)
<!-- .element: class="stretch" style="background-color:#FFFFFF" -->

========================

Fibonacci (revisited)

```tut:book:silent
val natCoalgebra: Coalgebra[Option, BigInt] =
  Coalgebra(n => if (n > 0) Some(n - 1) else None)

val fibAlgebra: CVAlgebra[Option, BigInt] = CVAlgebra {
  case Some(r1 :< Some(r2 :< _)) => r1 + r2
  case Some(_ :< None)           => 1
  case None                      => 0
}
```
```tut:book
val fib: BigInt => BigInt = scheme.ghylo(
  fibAlgebra.gather(Gather.histo),
  natCoalgebra.scatter(Scatter.ana))

fib(10)
```

Note:
- `:< is` from `Attr`
- `natCoalgebra` counts down
- `fibAlgebra` adds the last 2 together
