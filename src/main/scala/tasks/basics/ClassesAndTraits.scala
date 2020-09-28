package tasks.basics

object ClassesAndTraits extends App {
  def tan(x: Double):Double = java.lang.Math.tan(x)
  def sin(x: Double):Double = java.lang.Math.sin(x)
  val Pi: Double = java.lang.Math.PI
//  def main(args: Array[String]): Unit = {}
  sealed trait Shape extends Located with Bounded with Movable

  sealed trait Located {
    def x: Double
    def y: Double
    //    def z: Double
  }

  sealed trait Bounded {
    def minX: Double
    def maxX: Double
    def minY: Double
    def maxY: Double
  }
  sealed trait Movable {
    def move (dx: Double, dy: Double, dz: Double): Shape
  }
  final case class Polygon(cx: Double, cy: Double, n: Int, s: Double) extends Shape {
    def sides: Int = n
    def side_len: Double = s
    def Rc: Double = cy-s/(2*sin(Pi/n))
    def Ri: Double = cy-s/(2*tan(Pi/n))
    def w: Double = {
      if (n%2%2==0) Ri
      else if (n%2==0 && n%2%2!=0) Rc
      else Rc*sin((n-1)*Pi/(2*n))}
    override def x: Double = cx
    override def y: Double = cy
    override def minX: Double = cx - w
    override def maxX: Double = cx + w
    override def minY: Double = cy + Ri
    override def maxY: Double = cy + {if (n%2==0) Ri else Rc}
    override def move(dx: Double, dy: Double, dz: Double): Polygon = Polygon(cx+dx, cy+dy, sides, side_len)
    def area: Double = s*n*(s/2*tan(180/n))/2

  }


  final case class Point(x: Double, y: Double) extends Shape {
    override def minX: Double = x
    override def maxX: Double = x
    override def minY: Double = y
    override def maxY: Double = y
    override def move(dx: Double, dy: Double, dz: Double): Point = Point(x + dx, y + dy)
  }

  final case class Circle(centerX: Double, centerY: Double, radius: Double) extends Shape {
    override def x: Double = centerX
    override def y: Double = centerY
    override def minX: Double = x - radius
    override def maxX: Double = x + radius
    override def minY: Double = y - radius
    override def maxY: Double = y + radius
    override def move(dx: Double, dy: Double, dz: Double): Point = Point(x + dx, y + dy)
  }
  val ci = Polygon(0, 0, 5, 4.0)
  println(ci.maxY, ci.minY, ci.minX, ci.maxX)

}
