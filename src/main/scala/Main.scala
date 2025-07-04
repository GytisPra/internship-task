import upickle.default.*
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import os.write
import ujson.Arr
import ujson.Value
import ujson.Obj
import scala.compiletime.ops.double

case class Location(val name: String, val coordinates: (Double, Double)) derives Reader

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
    case other     =>
      throw new Error(
        s"error while trying to read regions exptected name and List(List(List())) got: $other"
      )
  }

case class Polygon(val points: List[(Double, Double)])

implicit val polygonsListReader: Reader[List[Polygon]] =
  reader[Value].map[List[Polygon]] {
    // There must be a better way to do this
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
    case other            =>
      throw new Error(
        s"error while trying to read polygons exptected Arr(value) got: $other"
      )
  }

case class Result(val region: String, val matchedLocations: List[String])
    derives Writer

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

  val results = for
    region   <- regions
    location <- locations
    if locationInPolygonsRaycasting(location, region.polygons)
  yield (region.name, location.name)

  // locationInPolygonsWithRaycasting(locations.head, regions.head.polygons)

  // This for-yield outputs List((region1, location1), (region1, location2), ...)
  // I need a way to group the results by the region name so that I get List((region1, List(...matchedLocations...)))
  // and also for example region3 has no matched locations so I would like to have List(..., (region3, List()))

  val groupedAndTransformedResults = results
    .groupBy(r => r._1) // Group by the region name
    .transform((key, value) => // transfrom
      value.map { case (regionName, locationName) =>
        locationName
      }
    )
    .map { // pattern match map the values to the class Result
      case (regionName, matchedLocations) =>
        Result(regionName, matchedLocations)
    }
    .toList

  if os.exists(resPath) then os.remove(resPath)

  val fullResults = regions.map(region =>
    groupedAndTransformedResults.find(_.region == region.name) match {
      case None        => Result(region.name, List())
      case Some(value) => value
    }
  )

  os.write(
    resPath,
    upickle.default
      .write[List[Result]](fullResults)
  )

/** Checks if a location is inside a given list of polygons
  *
  * I am using the Java geom.Path2D and geom.Point2D libraries. First we create
  * the polygon, then create a test point using the provided location and call
  * the method ```contains``` to check if the location is inside.
  *
  * Got this from:
  * https://www.geeksforgeeks.org/dsa/how-to-check-if-a-given-point-lies-inside-a-polygon/
  *
  * @param location
  *   The location to check
  * @param polygons
  *   A list of polygons
  * @return
  *   True if inside; False otherwise
  */
def locationInPolygons(location: Location, polygons: List[Polygon]): Boolean =
  polygons
    .map(polygon =>
      val path: Path2D = new Path2D.Double
      path.moveTo(polygon.points.head._1, polygon.points.head._2)

      polygon.points.drop(1).map(point => path.lineTo(point._1, point._2))

      path.closePath()

      val testPoint: Point2D =
        new Point2D.Double(location.coordinates._1, location.coordinates._2)

      path.contains(testPoint)
    )
    .find(_ == true) != None

/** Checks if a given location is inside any provided polygon
  *
  * This is implement using the ray casting algorithm. We take our location and
  * cast a ray to the right side of it to infinty. Now we can imangine what
  * happens when a point is outside of a polygon it will first enter the polygon
  * and then exit so it intersects an even number of times, however if the point
  * is inside the polygon then it will only exit intersecting, an odd number of
  * times.
  *
  * @param location
  *   The location to check
  * @param polygons
  *   A list of polygons
  * @return
  *   True if inside; False otherwise
  */
def locationInPolygonsRaycasting(
    location: Location,
    polygons: List[Polygon]
): Boolean =
  polygons
    .map(polygon =>
      // Right now a polygon stores its points in a linked list like so: p1 -> p2 -> ...
      // an edge is made up of two points, so to form an edge i want to take the following point
      // and combine with with the previous point so that I would get this llist: (p1, p2) -> (p2, p3) -> ... -> (plast -> p1)

      // a zip operation takes to lists an zips their values togehter
      // for example:
      //  list1 = List(1,2,3)
      //  list2 = List(1,2,3)
      //  zippedList1 = list1 zip list2      = list((1,1), (2,2), (3,3))
      //  zippedList2 = list1 zip list2.tail = list((1,2), (2,3))
      // so now for our points we have points = List((p1_long, p1_lat), (p2_long, p1_lat))

      // so in order to form edges we need to zip our points list with a points list where the head of the list is moved to the front
      // so zip our points = List((p1_long, p1_lat), (p2_long, p1_lat),...,(plast_long, plast_lat))
      // with newPoints = List((p2_long, p1_lat),...,(plast_long, plast_lat), (p1_long, p1_lat))
      // so now edges = points zip newPoints = List(((p1_long, p1_lat), (p2_long, p2_lat)),...,((plast_long, plast_lat), (p1_long, p1_lat)))
      // we can form a new list where the head is at the front by appending head to the tail

      val pointsHeadAtTail = polygon.points.tail :+ polygon.points.head
      val edges            =
        polygon.points zip pointsHeadAtTail map { case ((x1, y1), (x2, y2)) =>
          (
            x1,
            y1,
            x2,
            y2
          ) // clean up with pattern matching so that the edge coordinates are in a single tuple
        }

      val (xl, yl) = location.coordinates
      // now we can loop over all edges and check if a given location when casting a ray to the right intersects that edge
      val cnt      = edges
        .map((x1, y1, x2, y2) => // count will be equal to how many edges we intersected
          // First condition: if the point is higher or lower than the edge we know that its ray will not intersect it
          // Second condtion:
          if (yl < y1) != (yl < y2) && xl < x1 + ((yl - y1) / (y2 - y1)) * (x2 - x1)
          then 1
          else 0
        )
        .foldLeft(0)(_ + _)

      // now we can check if the count is odd or even
      // the count will be odd only if the point is inside and even when outside
      cnt % 2 == 1
    )
    .find(_ == true) != None
