```tut:invisible
import scala.annotation.tailrec
import higherkindness.droste._
import higherkindness.droste.data._
import higherkindness.droste.data.list._
import cats._
import cats.implicits._
import util.DefaultTraverse
```
Lets do something fancy

=========================

```tut:book
trait QuadTreeF[+T, +N]
final case class QuadF[T, N](value: T,
  topLeft: N,    topRight: N,
  bottomLeft: N, bottomRight: N) extends QuadTreeF[T, N]
final case object NilLeafF extends QuadTreeF[Nothing, Nothing]
```

=========================

```tut:book:silent
implicit def drosteTraverseForQuadTreeF[A]: Traverse[QuadTreeF[A, *]] =
   new DefaultTraverse[QuadTreeF[A, ?]] {
     def traverse[F[_]: Applicative, B, C]
       (fb: QuadTreeF[A, B])
       (f: B => F[C]): F[QuadTreeF[A, C]] =
       fb match {
         case QuadF(value, tl, tr, bl, br) =>
           (f(tl), f(tr), f(bl), f(br))
            .mapN(QuadF(value, _, _, _, _))
         case NilLeafF                     =>
           (NilLeafF: QuadTreeF[A, C]).pure[F]
       }
   }
```

=========================

```tut:book
val heightCoalgebra: Coalgebra[QuadTreeF[Int, *], Int] = Coalgebra {
  case 0 => NilLeafF
  case n =>
    val less = n - 1
    QuadF(n, less, less, less, less)
}
```

=========================

```tut:book:silent
val showAlgebra: Algebra[QuadTreeF[Int, *], List[List[Int]]] = Algebra {
  case NilLeafF => List.empty[List[Int]]
  case QuadF(v, Nil, Nil, Nil, Nil) => List(List(v))
  case QuadF(_, tl, tr, bl, br) =>
    def merge(left: List[List[Int]],
              right: List[List[Int]]) =
      left.zip(right).map(t => t._1 |+| t._2)
    merge(tl, tr) |+| merge(bl, br)
}
```

=========================

```tut:book
implicit def showLoL[T: Show]: Show[List[List[T]]] = { (a: List[List[T]]) =>
  val top = List.range(0, a.length * 2 + 1).as("-").mkString
  a.map(_.mkString("|",",","|"))
   .mkString(top + "\n" , "\n", "\n" + top)
}
```

=========================

```tut:book
val treeIt: Int => List[List[Int]] = scheme.ghylo(
  showAlgebra.gather(Gather.cata),
  heightCoalgebra.scatter(Scatter.ana)
)

treeIt(3).show
```
<!-- .element: class="stretch" -->

=========================

```tut:book
val noise = GCoAlgebraM[QuadTreeF[Int, *], Bounds] = CVAlgebra {
case class Bounds(top: Int, bottom: Int, left: Int, right: Int)
  case NilLeafF => 0
  case QuadF(v, Nil, Nil, Nil, Nil) => List(List(v))
}
```
<!-- .element: class="stretch" -->

=========================

```tut:book
val waveIt: Int => List[List[Int]] = scheme.ghylo(
  showAlgebra.gather(Gather.cata),
  heightCoalgebra.scatter(Scatter.ana)
)

waveIt(Bounds(255, 255, 126, 0)).show
```
<!-- .element: class="stretch" -->

=========================

```tut:invisible
def outputCanvas(width: Int, height:Int, data: List[Int]): Unit = {
  println(s"""
  <canvas id="canvas-tree" width="300" height="300" data-tree='
  {"width":$width,"height":$height, "data":[${data.mkString(",")}]}
   '></canvas>
   """)
}
```
```tut:passthrough
outputCanvas(100, 100, List.range(0,100 * 100).map(_ => 255))
 ```
