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
type Height = Int
case class Bounds(top: Int, bottom: Int, left: Int, right: Int)

val noise: Coalgebra[QuadTreeF[Int, *], (Height, Bounds)] = Coalgebra {
  case (height, bound) =>
  if(height <= 0) NilLeafF
  else {
    import bound._
    val halfVert = (top + bottom) / 2
    val halfHorz = (left + right) / 2
    QuadF(
      value = (top + bottom + left + right) / 4,
      topLeft = (height - 1, Bounds(top, halfVert, left, halfHorz)),
      topRight = (height - 1, Bounds(top, halfVert, halfHorz, right)),
      bottomLeft = (height - 1, Bounds(halfVert, bottom, left, halfHorz)),
      bottomRight = (height - 1, Bounds(halfVert, bottom, halfHorz, right))
    )
  }
}
```
<!-- .element: class="stretch" -->

=========================

```tut:book
val waveIt: ((Height, Bounds)) => List[List[Int]] = scheme.ghylo(
  showAlgebra.gather(Gather.cata),
  noise.scatter(Scatter.ana)
)

waveIt(3 -> Bounds(255, 255, 126, 0)).show
```
<!-- .element: class="stretch" -->

=========================

```tut:invisible
def outputCanvas(width: Int, height:Int, data: List[Int]): Unit = {
  println(s"""
  <canvas id="canvas-tree" data-tree='
  {"width":$width,"height":$height, "data":[${data.mkString(",")}]}
   '></canvas>
   """)
}
val grid = waveIt(8 -> Bounds(255, 0, 126, 0))
```
```tut:passthrough
outputCanvas(grid.length, grid.length, grid.flatten)
```
