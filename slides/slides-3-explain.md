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
Like arithmetic what we mean is some operations on some 'thing'

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

Note:
A transformation from one thing to another thing

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
Classic Fold

========================

`Hylomorphism`

Do a Catamorphism then an Anamorphism <!-- .element: class="fragment" data-fragment-index="1" -->

Build it up, then combine it together  <!-- .element: class="fragment" data-fragment-index="2" -->

========================

![Morphisms](https://raw.githubusercontent.com/slamdata/matryoshka/master/resources/recursion-schemes.png)
<!-- .element: class="stretch" style="background-color:#FFFFFF" -->
