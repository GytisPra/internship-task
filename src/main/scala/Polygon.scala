package Models

case class Polygon(val points: List[(Double, Double)]):
  def getEdges(): List[(Double, Double, Double, Double)] =
    points zip (points.tail :+ points.head) map { case ((x1, y1), (x2, y2)) => (x1, y1, x2, y2) }

object Polygon {
  import upickle.default.{reader, macroR, Reader}
  import ujson.{Value, Arr}

  implicit val polygonReader: Reader[Polygon] = macroR

  implicit val polygonsListReader: Reader[List[Polygon]] =
    reader[Value].map[List[Polygon]] {
      case Arr(polygonsArr) =>
        polygonsArr.toList
          .map(_.arr.toList.map(_.arr.toList match {
            case List(a, b) => (a.num, b.num)
            case other      => throw new Error(s"Expected List(a, b) got: $other")
          }))
          .map(Polygon(_))
      case other            => throw new Error(s"exptected Arr(value) got: $other")
    }
}
