package tasks.basics

object ClassesAndTraits extends App {

  def tan(x: Double): Double = java.lang.Math.tan(x)
  def sin(x: Double): Double = java.lang.Math.sin(x)
  val Pi: Double = java.lang.Math.PI

  sealed trait Located {
    def x: Double
    def y: Double
  }

  sealed trait Bounded {
    def minX: Double
    def maxX: Double
    def minY: Double
    def maxY: Double
  }

  sealed trait Shape extends Located with Bounded
  sealed trait Shape1D[A <: Shape1D[A]] extends Shape { def move(dx: Double, dy: Double): A }
  sealed trait Shape2D[A <: Shape2D[A]] extends Shape1D[A] { def area: Double }

  sealed trait Shape3D[A <: Shape3D[A]] extends Shape {
    def z: Double
    def minZ: Double
    def maxZ: Double
    def surface_area: Double
    def volume: Double
    def move(dx: Double, dy: Double, dz: Double): A
  }

  sealed trait Sided {
    def side_len: Double
    def sides: Int
  }

  final case class Point(x: Double, y: Double) extends Shape1D[Point] {
    override def minX: Double = x
    override def maxX: Double = x
    override def minY: Double = y
    override def maxY: Double = y
    override def move(dx: Double, dy: Double): Point = Point(x + dx, y + dy)
  }

  final case class Circle(centerX: Double, centerY: Double, radius: Double)
      extends Shape2D[Circle] {
    override def x: Double = centerX
    override def y: Double = centerY
    override def minX: Double = x - radius
    override def maxX: Double = x + radius
    override def minY: Double = y - radius
    override def maxY: Double = y + radius
    override def move(dx: Double, dy: Double): Circle = Circle(x + dx, y + dy, radius)
    override def area: Double = Pi * radius * radius
  }

  final case class Rectangle(cx: Double, cy: Double, side1: Double, side2: Double)
      extends Shape2D[Rectangle] {
    override def x: Double = cx
    override def y: Double = cy
    override def minX: Double = x - 0.5 * side1
    override def maxX: Double = x + 0.5 * side1
    override def minY: Double = y - 0.5 * side2
    override def maxY: Double = y + 0.5 * side2
    override def move(dx: Double, dy: Double): Rectangle = Rectangle(x + dx, y + dy, side1, side2)
    override def area: Double = side1 * side2
  }

  final case class Triangle(cx: Double, cy: Double, side1: Double, side2: Double, side3: Double)
      extends Shape2D[Triangle] {
    override def x: Double = cx
    override def y: Double = cy
    override def minX: Double = ???
    override def maxX: Double = ???
    override def minY: Double = ???
    override def maxY: Double = ???

    override def move(dx: Double, dy: Double): Triangle =
      Triangle(cx + dx, cy + dy, side1, side2, side3)

    override def area: Double = {
      val S = (side1 + side2 + side3) / 2
      math.sqrt(S * (S - side1) * (S - side2) * (S - side3))
    }
  }

  final case class Polygon(cx: Double, cy: Double, n: Int, s: Double)
      extends Shape2D[Polygon]
      with Sided {
    def sides: Int = n
    override def side_len: Double = s
    def Rc: Double = cy - s / (2 * sin(Pi / n))
    def Ri: Double = cy - s / (2 * tan(Pi / n))

    def w: Double = {
      if (n % 2 % 2 == 0) Ri
      else if (n % 2 == 0 && n % 2 % 2 != 0) Rc
      else Rc * sin((n - 1) * Pi / (2 * n))
    }
    override def x: Double = cx
    override def y: Double = cy
    override def minX: Double = cx - w
    override def maxX: Double = cx + w
    override def minY: Double = cy + Ri
    override def maxY: Double = cy + { if (n % 2 == 0) Ri else Rc }
    override def move(dx: Double, dy: Double): Polygon = Polygon(cx + dx, cy + dy, sides, side_len)
    override def area: Double = s * n * (s / 2 * tan(180 / n)) / 2

  }

  final case class Pyramid(cx: Double, cy: Double, cz: Double, n: Int, s: Double, h: Double)
      extends Shape3D[Pyramid]
      with Sided {
    def sides: Int = n
    override def side_len: Double = s
    def height: Double = h
    def base: Polygon = Polygon(x, y, sides, side_len)
    override def x: Double = cx
    override def y: Double = cy
    override def z: Double = cz
    override def minX: Double = base.minX
    override def maxX: Double = base.maxX
    override def minY: Double = base.minY
    override def maxY: Double = base.maxY
    override def minZ: Double = z
    override def maxZ: Double = z + height

    override def move(dx: Double, dy: Double, dz: Double): Pyramid =
      Pyramid(cx + dx, cy + dy, cz + dz, sides, side_len, height)
    override def surface_area: Double = sides * side_len * height / 2 + base.area
    override def volume: Double = 1 / 3 * base.area * height

  }

  final case class Sphere(centerX: Double, centerY: Double, centerZ: Double, radius: Double)
      extends Shape3D[Sphere] {
    override def x: Double = centerX
    override def y: Double = centerY
    override def z: Double = centerZ
    override def minX: Double = x - radius
    override def maxX: Double = x + radius
    override def minY: Double = y - radius
    override def maxY: Double = y + radius
    override def minZ: Double = z - radius
    override def maxZ: Double = z + radius

    override def move(dx: Double, dy: Double, dz: Double): Sphere =
      Sphere(x + dx, y + dy, z + dz, radius)
    override def surface_area: Double = 4 * Pi * radius * radius
    override def volume: Double = 4 / 3 * Pi * radius * radius * radius

  }

//https://www.artima.com/pins1ed/type-parameterization.html
}
