import upickle.default.read
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import com.internshiptask.Models.{Polygon, Location, Point, Region}

class CustomPicklersTest extends AnyFunSuite with Matchers {
  // ---------------------- Point custom pickler tests ---------------
  test("point reader should parse correct JSON with no errors") {
    val validJson     = "[1, 2]"
    val expectedPoint = Point.unsafeApply(1, 2)

    read[Either[String, Point]](validJson) match {
      case Left(error)  => fail(s"Expected point but got: $error")
      case Right(point) => point shouldBe expectedPoint
    }
  }

  test("point reader should return an error when coordinates are incorrect") {
    val invalidJson = "[1, 2200]"

    read[Either[String, Point]](invalidJson) match {
      case Left(error)  => error should startWith("provided latitude")
      case Right(point) => fail(s"Expected error but got: $point")
    }
  }

  // ---------------------- Polygon custom pickler tests ---------------
  test("polygon reader should parse correct JSON with no errors") {
    val validJson       = """[[1, 2], [2, 4], [3, 6]]"""
    val expectedPolygon =
      Polygon(List(Point.unsafeApply(1, 2), Point.unsafeApply(2, 4), Point.unsafeApply(3, 6)))

    read[Either[String, Polygon]](validJson) match {
      case Left(error)    => fail(s"Expected polygon but got: $error")
      case Right(polygon) => polygon shouldBe expectedPolygon
    }
  }

  test("polygon reader should return an error when number of points is incorrect") {
    val invalidJson = """[[1, 2], [2, 4]]"""

    read[Either[String, Polygon]](invalidJson) match {
      case Left(error)    => error should endWith("(has to be > 2)")
      case Right(polygon) => fail(s"Expected error but got: $polygon")
    }
  }

  // ---------------------- Region custom pickler tests ---------------
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
      case Left(error)   => fail(s"Expected region but got: $error")
      case Right(region) => region shouldBe expectedRegion
    }
  }

  test("region reader should return an error when name is blank") {
    val invalidJson =
      """{ "name": "", "coordinates": [ [[1, 2], [2, 4]], [[1, 2], [2, 4]] ]}"""
    read[Either[String, Region]](invalidJson) match {
      case Left(error)   => error shouldBe ("name of a region is blank")
      case Right(region) => fail(s"Expected error but got: $region")
    }
  }

  test("region reader should return an error when there are no polygons") {
    val invalidJson =
      """{ "name": "region1", "coordinates": []}"""
    read[Either[String, Region]](invalidJson) match {
      case Left(error)   => error should endWith("has no polygons")
      case Right(region) => fail(s"Expected error but got: $region")
    }
  }

  // ---------------------- Location custom pickler tests ---------------
  test("location reader should parse correct JSON with no errors") {
    val expectedLocation = Location("location1", Point.unsafeApply(1, 2))

    val validJson = """{"name": "location1", "coordinates":[1,2]}"""
    read[Either[String, Location]](validJson) match {
      case Left(error)     => fail(s"Expected location but got: $error")
      case Right(location) => location shouldBe expectedLocation
    }

  }

  test("location reader should return an error when name is blank") {
    val invalidJson = """{"name": "", "coordinates":[1,2]}"""
    read[Either[String, Location]](invalidJson) match {
      case Left(error)     => error shouldBe ("name of a location is blank")
      case Right(location) => fail(s"Expected error but got: $location")
    }
  }
}
