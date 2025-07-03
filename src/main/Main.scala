//> using toolkit 0.7.0

import upickle.default.*
import upickle.implicits.key
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import os.write
import ujson.Arr
import ujson.Value
import ujson.Obj

case class Location(val name: String, val coordinates: (Double, Double))
    derives Reader

case class Region(
    val name: String,
    val polygons: List[Polygon]
)

implicit val regionReader: Reader[Region] =
  reader[Value].map[Region] {
    case Obj(json) =>
      val name     = json("name").str
      val polygons = read[List[Polygon]](json("coordinates"))
      new Region(name, polygons)
    case other           =>
      throw new Error(
        s"error while trying to read regions exptected name and List(List(List())) got: $other"
      )
  }

case class Polygon(val points: List[(Double, Double)])

implicit val polygonsListReader: Reader[List[Polygon]] =
  reader[Value].map[List[Polygon]] {
    case Arr(polygonsArr) =>
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
  // I am guessing this is bad because the user doesn't know in what order they should input stuff
  // so maybe replacing this with args array and then from there finding the paths would be better?
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

  os.write(resPath, upickle.default.write[List[Result]](results))

/**
  * Checks if a location is inside a given list of polygons
  * 
  * I am using the Java geom.Path2D and geom.Point2D libraries.
  * First we create the polygon, then create a test point using the
  * provided location and call the method ```contains``` to check if the
  * location is inside.
  * 
  * Got this from: https://www.geeksforgeeks.org/dsa/how-to-check-if-a-given-point-lies-inside-a-polygon/
  *
  * @param location The location to check
  * @param polygons A list of polygons
  * @return True if inside; False otherwise
  */
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
