import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import com.internshiptask.Models.{Point, Polygon, Location, Precision}
import com.internshiptask.Utils.GeoUtils.{locationInPolygon, locationInPolygons}

class GeoUtilsTest extends AnyFunSuite with Matchers:
  val points                 = List(
    Point.unsafeApply(2.5, 1.5),
    Point.unsafeApply(2, 1),
    Point.unsafeApply(2.5, 0.5),
    Point.unsafeApply(3, 1)
  )
  val testPolygon            = Polygon(points)
  given precision: Precision = Precision(1e-5)

  test("should correctly determine if a location is inside a polygon") {
    val locationInside    = Location(name = "inside", coordinates = Point.unsafeApply(2, 1))
    val locationNotInside = Location(name = "outside", coordinates = Point.unsafeApply(10, 10))

    val isInPolygon    = locationInPolygon(locationInside, testPolygon)
    val isNotInPolygon = locationInPolygon(locationNotInside, testPolygon)

    withClue(s"failed for location ${locationInside.coordinates}, when polygon was $testPolygon") {
      isInPolygon shouldBe true
    }
    withClue(
      s"failed for location ${locationNotInside.coordinates}, when polygon was $testPolygon"
    ) {
      isNotInPolygon shouldBe false
    }
  }

  test("location on polygon point should be considered inside") {
    for point <- testPolygon.points do
      val location    = Location(name = "test", coordinates = point)
      val isInPolygon = locationInPolygon(location, testPolygon)
      withClue(
        s"failed at point ${location.coordinates} when polygon was ${testPolygon}: "
      ) {
        isInPolygon shouldBe true
      }
  }

  test("location on polygon edge should be considered inside") {
    val polygonEdges = testPolygon.getEdges()
    for (p1, p2) <- polygonEdges do
      val midPoint = Point.unsafeApply(((p1.x + p2.x).coord / 2), ((p1.y + p2.y).coord / 2))
      val location = Location(name = "test", coordinates = midPoint)
      val isOnEdge = locationInPolygon(location, testPolygon)
      withClue(s"failed at point ${location.coordinates} when edge was ${(p1, p2)}: ") {
        isOnEdge shouldBe true
      }
  }

  test("should correctly determine if a location is inside any polygon") {
    val points1 = List(Point.unsafeApply(10, 2), Point.unsafeApply(12, 5), Point.unsafeApply(12, 2))
    val points2 = List(Point.unsafeApply(1, 2), Point.unsafeApply(3, 5), Point.unsafeApply(3, 2))

    val polygon1 = Polygon(points1)
    val polygon2 = Polygon(points2)

    val polygons = List(polygon1, polygon2)

    val locationInside    = Location(name = "inside", coordinates = Point.unsafeApply(11.5, 3))
    val locationNotInside = Location(name = "outside", coordinates = Point.unsafeApply(80, 80))

    val isInPolygon    = locationInPolygons(locationInside, polygons)
    val isNotInPolygon = locationInPolygons(locationNotInside, polygons)

    isInPolygon shouldBe true
    isNotInPolygon shouldBe false
  }
