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
<!-- .element: class="stretch" -->

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
<!-- .element: class="stretch" -->

=========================

```tut:book:silent
def showAlgebra[T]: Algebra[QuadTreeF[T, *], List[List[T]]] = Algebra {
  case NilLeafF => List.empty[List[T]]
  case QuadF(v, Nil, Nil, Nil, Nil) => List(List(v))
  case QuadF(_, tl, tr, bl, br) =>
    def merge(left: List[List[T]],
              right: List[List[T]]) =
      left.zip(right).map(t => t._1 |+| t._2)
    merge(tl, tr) |+| merge(bl, br)
}
```
<!-- .element: class="stretch" -->

=========================

```tut:book
implicit def showLoL[T: Show]: Show[List[List[T]]] = { (a: List[List[T]]) =>
  val top = List.range(0, a.length * 2 + 1).as("-").mkString
  a.map(_.mkString_("|",",","|"))
   .mkString(top + "\n" , "\n", "\n" + top)
}
```
<!-- .element: class="stretch" -->

=========================

```tut:book:silent
case class Color(r: Int, b: Int, g: Int) {
  private def pin(v: Int): Int = Math.min(255, Math.max(0, v))
  def bound: Color = Color(pin(r), pin(b), pin(g))
  def +(that: Color): Color = Color(r + that.r, b+that.b, g+that.g)
  def /(s: Int): Color = Color(r / s, b / s, g / s).bound
  def between(that: Color): Color = ((this + that) / 2).bound

  def hex = f"$r%02X$g%02X$b%02X"
}
implicit val showColor: Show[Color] = _.hex
val black = Color(0,0,0)
val white = Color(255,255,255)
val red = Color(255, 0, 0)
val blue = Color(0, 255, 0)
val green = Color(0, 0, 255)
```
<!-- .element: class="stretch" -->

=========================

```tut:book:silent
type Height = Int
type Bounds = QuadF[Height, Color]
object Bounds {
  def apply(v: Height, topLeft: Color, topRight: Color,
                       bottomLeft: Color, bottomRight: Color) =
    QuadF.apply[Height, Color](v, topLeft, topRight,
                                  bottomLeft, bottomRight)
}
```

=========================

```tut:book:silent
def split(b: Bounds): QuadF[Color, Bounds] = {
  import b._    
  val nextHeight = value - 1
  val halfLeft = topLeft between bottomLeft
  val halfRight = topRight between bottomRight
  val halfBottom = bottomLeft between bottomRight
  val halfTop = topLeft between topRight
  val center = (topLeft + bottomLeft + topRight + bottomRight) / 4
QuadF(
  value = center,
  Bounds(nextHeight, topLeft,  halfTop,   halfLeft,   center),
  Bounds(nextHeight, halfLeft, center,    bottomLeft, halfBottom),
  Bounds(nextHeight, halfTop,  topRight,  center,     halfRight),
  Bounds(nextHeight, center,   halfRight, halfBottom, bottomRight)
)
}
```
<!-- .element: class="stretch" -->

=========================

```tut:book:silent
val noise: Coalgebra[QuadTreeF[Color, *], Bounds] = Coalgebra {
  bound =>
  if(bound.value <= 0) NilLeafF
  else split(bound)
}
```

=========================

```tut:book
val waveIt: Bounds => List[List[Color]] = scheme.ghylo(
  showAlgebra[Color].gather(Gather.cata),
  noise.scatter(Scatter.ana)
)

waveIt(Bounds(3, white, black, white, black)).show
```
<!-- .element: class="stretch" -->

=========================

```tut:invisible
implicit val showColor: Show[Color] = c => s"""{"r":${c.r},"g":${c.g},"b":${c.b}}"""
def outputCanvas(width: Int, height:Int, data: List[Color]): Unit = {
  println(s"""
  <canvas id="canvas-tree" width=500px height=500px data-tree='
  {"width":$width,"height":$height, "data":[${data.mkString_(",")}]}
   '></canvas>
   """)
}
val result = waveIt(Bounds(10, white, green, blue, red))
```
```tut:passthrough
outputCanvas(result.length, result.length, result.flatten)
```
<!-- .element: class="stretch" -->
