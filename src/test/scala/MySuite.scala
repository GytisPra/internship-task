import collection.mutable.Stack
import upickle.default._
import org.scalatest.funsuite.AnyFunSuite

import com.internshiptask.Models.{Polygon, Location, Point, Region}
import com.internshiptask.Utils.GeoUtils

class CustomPicklersTest extends AnyFunSuite {
  test("Point custom reader") {
    val json     = "[1, 2]"
    val point    = read[Point](json)
    val expectedPoint = Point(1, 2)
    assert(point == expectedPoint)
  }

  test("Polygon custom reader") {
    val json     = """[ [[1, 2], [2, 4]], [[1, 2], [2, 4]] ]"""
    val polygons = read[List[Polygon]](json)
    val expectedPolygons =
      List(Polygon(List(Point(1, 2), Point(2, 4))), Polygon(List(Point(1, 2), Point(2, 4))))
    assert(polygons == expectedPolygons)
  }

  test("Region custom reader") {
    val json = """{ "name": "region1", "coordinates": [ [[1, 2], [2, 4]], [[1, 2], [2, 4]] ]}"""
    val region = read[Region](json)
    val expectedPolygons =
      List(Polygon(List(Point(1, 2), Point(2, 4))), Polygon(List(Point(1, 2), Point(2, 4))))
    val expectedRegion = Region(name = "region1", polygons = expectedPolygons)

    assert(region == expectedRegion)
  }

  test("Location custom reader") {
    val json = """{"name": "location1", "coordinates":[1,2]}"""
    val location = read[Location](json)
    val expectedLocation = Location("location1", Point(1,2))
    assert(location == expectedLocation) 
  }
}

class GetEdgesTest extends AnyFunSuite {

  test("correctly forms edges") {
    val (point1, point2, point3) = (Point(1, 2), Point(3, 5), Point(3, 2))
    val polygon                  = Polygon(points = List(point1, point2, point3))
    val expectedEdges            = List((point1, point2), (point2, point3), (point3, point1))
    val edges                    = polygon.getEdges()

    assert(edges.length == polygon.points.length)
    assert(edges == expectedEdges)
  }
}

class LocationInPolygonTest extends AnyFunSuite {
  val (point1, point2, point3) = (Point(1, 2), Point(3, 5), Point(3, 2))
  val testPolygon              = Polygon(points = List(point1, point2, point3))

  test("correctly determines if a location is inside a polygon") {
    val locationInside    = Location(name = "inside", coordinates = Point(1.5, 2))
    val locationNotInside = Location(name = "outside", coordinates = Point(10, 10))

    val isInPolygon    = GeoUtils.locationInPolygon(locationInside, testPolygon)
    val isNotInPolygon = GeoUtils.locationInPolygon(locationNotInside, testPolygon)

    assert(isInPolygon == true)
    assert(isNotInPolygon == false)
  }

  test("edge case where location coords are the same as one of polygon points") {
    for point <- testPolygon.points do
      val location    = Location(name = "test", coordinates = point)
      val isInPolygon = GeoUtils.locationInPolygon(location, testPolygon)
      assert(isInPolygon == true)
  }

  test("edge case where a location is on an edge of a polygon") {
    val polygonEdges = testPolygon.getEdges()
    for (p1, p2) <- polygonEdges do
      val midPoint    = Point(((p1.x + p2.x) / 2), ((p1.y + p2.y) / 2))
      val location    = Location(name = "test", coordinates = midPoint)
      val isInPolygon = GeoUtils.locationInPolygon(location, testPolygon)
      assert(isInPolygon == true)
  }

  test("correctly determines if a location is inside any polygon") {
    val points1  = List(Point(10, 2), Point(12, 5), Point(12, 2))
    val points2  = List(Point(1, 2), Point(3, 5), Point(3, 2))
    val polygon1 = Polygon(points1)
    val polygon2 = Polygon(points2)
    val polygons = List(polygon1, polygon2)

    val locationInside    = Location(name = "inside", coordinates = Point(11.5, 3))
    val locationNotInside = Location(name = "outside", coordinates = Point(100, 100))

    val isInPolygon    = GeoUtils.locationInPolygons(locationInside, polygons)
    val isNotInPolygon = GeoUtils.locationInPolygons(locationNotInside, polygons)

    assert(isInPolygon == true)
    assert(isNotInPolygon == false)
  }
}
