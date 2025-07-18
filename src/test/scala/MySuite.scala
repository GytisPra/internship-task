import upickle.default.read
import org.scalatest.funsuite.AnyFunSuite

import com.internshiptask.Models.{Polygon, Location, Point, Region}
import com.internshiptask.Utils.GeoUtils
import com.internshiptask.Models.Precision

class CustomPicklersTest extends AnyFunSuite {
  test("point reader should parse correct JSON with no errors") {
    val validJson     = "[1, 2]"
    val expectedPoint = Point.unsafeApply(1, 2)

    read[Either[String, Point]](validJson) match {
      case Left(error)  => fail(s"Something went wrong: $error")
      case Right(point) => assert(point == expectedPoint)
    }
  }

  test("point reader should return an error when JSON incorrect") {
    val invalidJson = "[1, 2200]"

    read[Either[String, Point]](invalidJson) match {
      case Left(error)  => assert(error.startsWith("provided"))
      case Right(point) => fail(s"Point should not have been read")
    }
  }

  test("polygon reader should parse correct JSON with no errors") {
    val validJson       = """[[1, 2], [2, 4], [3, 6]]"""
    val expectedPolygon =
      Polygon(List(Point.unsafeApply(1, 2), Point.unsafeApply(2, 4), Point.unsafeApply(3, 6)))

    read[Either[String, Polygon]](validJson) match {
      case Left(error)    => fail(s"Something went wrong: $error")
      case Right(polygon) => assert(polygon == expectedPolygon)
    }
  }

  test("polygon reader should return an error when JSON incorrect") {
    val invalidJson = """[[10000, 2000], [2, 4], [3, 6]]"""

    read[Either[String, Polygon]](invalidJson) match {
      case Left(error)    => assert(error.startsWith("error"))
      case Right(polygon) => fail(s"Polygon should not have been read")
    }
  }

  test("region reader should parse correct JSON with no errors") {
    val expectedPolygons =
      List(
        Polygon(List(Point.unsafeApply(1, 2), Point.unsafeApply(2, 4), Point.unsafeApply(3, 5))),
        Polygon(List(Point.unsafeApply(1, 2), Point.unsafeApply(2, 4), Point.unsafeApply(3, 5)))
      )
    val expectedRegion   = Region(name = "region1", polygons = expectedPolygons)

    val validJson =
      """{ "name": "region1", "coordinates": [ [[1, 2], [2, 4], [3, 5]], [[1, 2], [2, 4], [3, 5]] ]}"""
    read[Either[String, Region]](validJson) match {
      case Left(error)   => fail(s"Something went wrong: $error")
      case Right(region) => assert(region == expectedRegion)
    }
  }

  test("region reader should return an error when JSON incorrect") {
    val invalidJson =
      """{ "name": "region1", "coordinates": [ [[1000, 20000], [2, 4]], [[1, 2], [2, 4]] ]}"""
    read[Either[String, Region]](invalidJson) match {
      case Left(error)   => assert(error.startsWith("error"))
      case Right(region) => fail("region should not have been read")
    }
  }

  test("location reader should parse correct JSON with no errors") {
    val expectedLocation = Location("location1", Point.unsafeApply(1, 2))

    val validJson = """{"name": "location1", "coordinates":[1,2]}"""
    read[Either[String, Location]](validJson) match {
      case Left(error)     => fail(s"Something went wrong: $error")
      case Right(location) => assert(location == expectedLocation)
    }

  }

  test("location reader should return an error when JSON incorrect") {
    val invalidJson = """{"name": "location1", "coordinates":[10000,20000]}"""
    read[Either[String, Location]](invalidJson) match {
      case Left(error)     => assert(error.startsWith("error"))
      case Right(location) => fail("location should not have been read")
    }
  }
}

class GetEdgesTest extends AnyFunSuite {

  test("correctly forms edges") {
    val (point1, point2, point3) =
      (Point.unsafeApply(1, 2), Point.unsafeApply(3, 5), Point.unsafeApply(3, 2))
    val polygon                  = Polygon(points = List(point1, point2, point3))
    val expectedEdges            = List((point1, point2), (point2, point3), (point3, point1))
    val edges                    = polygon.getEdges()

    assert(edges == expectedEdges)
  }
}

class LocationInPolygonTest extends AnyFunSuite {
  val (point1, point2, point3) =
    (Point.unsafeApply(1, 2), Point.unsafeApply(3, 5), Point.unsafeApply(3, 2))
  val testPolygon              = Polygon(points = List(point1, point2, point3))
  given precision: Precision = Precision(1e-5)

  test("correctly determines if a location is inside a polygon") {
    val locationInside    = Location(name = "inside", coordinates = Point.unsafeApply(1.5, 2))
    val locationNotInside = Location(name = "outside", coordinates = Point.unsafeApply(10, 10))

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
      val midPoint    = Point.unsafeApply(((p1.x + p2.x).coord / 2), ((p1.y + p2.y).coord / 2))
      val location    = Location(name = "test", coordinates = midPoint)
      val isInPolygon = GeoUtils.locationInPolygon(location, testPolygon)
      assert(isInPolygon == true)
  }

  test("should not fail if polygon has diagonal edges") {
    val points = List(
      Point.unsafeApply(2.5, 1.5), 
      Point.unsafeApply(2,1), 
      Point.unsafeApply(2.5, 0.5), 
      Point.unsafeApply(3, 1)
    )

    val polygon = Polygon(points)
    val polygonEdges = polygon.getEdges()

    for (p1, p2) <- polygonEdges do
      val midPoint    = Point.unsafeApply(((p1.x + p2.x).coord / 2), ((p1.y + p2.y).coord / 2))
      val location    = Location(name = "test", coordinates = midPoint)
      val isInPolygon = GeoUtils.locationInPolygon(location, polygon)
      assert(isInPolygon == true)
  }

  test("correctly determines if a location is inside any polygon") {
    val points1 = List(Point.unsafeApply(10, 2), Point.unsafeApply(12, 5), Point.unsafeApply(12, 2))
    val points2 = List(Point.unsafeApply(1, 2), Point.unsafeApply(3, 5), Point.unsafeApply(3, 2))

    val polygon1 = Polygon(points1)
    val polygon2 = Polygon(points2)

    val polygons = List(polygon1, polygon2)

    val locationInside    = Location(name = "inside", coordinates = Point.unsafeApply(11.5, 3))
    val locationNotInside = Location(name = "outside", coordinates = Point.unsafeApply(80, 80))

    val isInPolygon    = GeoUtils.locationInPolygons(locationInside, polygons)
    val isNotInPolygon = GeoUtils.locationInPolygons(locationNotInside, polygons)

    assert(isInPolygon == true)
    assert(isNotInPolygon == false)
  }
}
