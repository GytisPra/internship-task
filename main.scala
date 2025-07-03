//> using toolkit 0.7.0

import upickle.default.*
import upickle.implicits.key
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import os.write.append
import ujson.Arr
import ujson.Obj
import scala.collection.mutable.ArrayBuffer
import scala.compiletime.ops.double

case class Location(val name: String, val coordinates: (Double, Double))
    derives Reader

case class Region(
    val name: String,
    val polygons: List[Polygon]
)

implicit val regionReader: Reader[Region] =
  reader[ujson.Value].map[Region] {
    case ujson.Obj(json) =>
      val name     = json("name").str
      val polygons = read[List[Polygon]](json("coordinates"))
      new Region(name = name, polygons)
    case other           =>
      throw new Error(
        s"error while trying to read regions exptected name and List(List(List())) got: $other"
      )
  }

case class Polygon(val points: List[(Double, Double)])
  // def formEdges(): List[(Double, Double, Double, Double)] =
  //   points.map(point => point match {
  //     case ((x1, y1), (x2, y2)) => (x1, y1, x2, y2)
  //   })
  //   }

implicit val polygonsListReader: Reader[List[Polygon]] =
  reader[ujson.Value].map[List[Polygon]] {
    case ujson.Arr(polygonsArr) =>
      polygonsArr.toList
        .map {
          case Arr(value) =>
            value.toList.map {
              case Arr(value) =>
                value.toList match {
                  case List(a, b) => (a.num, b.num)
                  case other      =>
                    throw new Error(s"Expected List(a, b) got: $other")
                }
              case other      => throw new Error(s"Expected Arr(value) got: $other")
            }
          case other      => throw new Error(s"Expected Arr(value) got: $other")
        }
        .map(Polygon(_))
    case other                  =>
      throw new Error(
        s"error while trying to read polygons exptected Arr(value) got: $other"
      )
  }

case class Result(val region: String, var matchedLocations: Seq[String])
    derives Writer:
  def appendLocation(
      location: String
  ): Unit = // Should not be doing this with a mutable list
    this.matchedLocations = this.matchedLocations :+ location

@main
def main(regionsPath: String, locationsPath: String, outputPath: String): Unit =
  val locInputPath =
    os.pwd / "input" / locationsPath.stripPrefix("locations=")
  val regInputPath =
    os.pwd / "input" / regionsPath.stripPrefix("regions=")
  var resPath      =
    os.pwd / "output" / outputPath.stripPrefix("output=")

  val regions: List[Region]     = read[List[Region]](os.read(regInputPath))
  val locations: List[Location] = read[List[Location]](os.read(locInputPath))
  var results: List[Result]     = List()

  val result = for 
    region <- regions  
    location <- locations
    if locationInPolygons(location, region.polygons)
  yield (region.name, location.name)
  // This for-yield outputs List((region1, location1), (region1, location2), ...)
  // I need a way to group the results by the region name so that I get List((region1, List(...matchedLocations...)))
  // and also for example region3 has no matched locations so I would like to have List(..., (region3, List()))

  val test = result.groupBy(result => result._1)
  println(test)
    

  if os.exists(resPath) then os.remove(resPath)

  os.write.append(resPath, write[List[Result]](results))

def locationInPolygons(location: Location, polygons: List[Polygon]): Boolean =
  val path: Path2D    = new Path2D.Double
  var locationMatched = false

  for polygon <- polygons do
    path.moveTo(polygon.points.head._1, polygon.points.head._2)

    polygon.points.drop(1).map(point => path.lineTo(point._1, point._2))

    path.closePath()

    val testPoint: Point2D =
      new Point2D.Double(location.coordinates._1, location.coordinates._2)

    if path.contains(testPoint) then locationMatched = true

  return locationMatched
