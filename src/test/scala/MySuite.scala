import collection.mutable.Stack
import upickle.default._
import org.scalatest.funsuite.AnyFunSuite

import Models.Polygon.polygonsListReader
import Models.{Polygon, Location}
import Utils.GeoUtils

class PolygonReaderTest extends AnyFunSuite {

  test("Region polygon coordinates array read correctly") {
    val json     = """[ [[1, 2], [2, 4]], [[1, 2], [2, 4]] ]"""
    val polygons = read[List[Polygon]](json)
    val expected =
      List(Polygon(List((1, 2), (2, 4))), Polygon(List((1, 2), (2, 4))))
    assert(polygons == expected)
  }
}

class GetEdgesTest extends AnyFunSuite {

  test("correctly forms edges") {
    val polygon       = Polygon(points = List((1, 2), (3, 5), (3, 2)))
    val expectedEdges = List((1, 2, 3, 5), (3, 5, 3, 2), (3, 2, 1, 2))
    val edges         = polygon.getEdges()

    assert(edges.length == polygon.points.length)
    assert(edges == expectedEdges)
  }
}

class LocationInPolygonTest extends AnyFunSuite {
  val testPolygon = Polygon(points = List((1, 2), (3, 5), (3, 2)))

  test("correctly determines if a location is inside a polygon") {
    val locationInside    = Location(name = "inside", coordinates = (1.5, 2))
    val locationNotInside = Location(name = "outside", coordinates = (10, 10))

    val isInPolygon    = GeoUtils.locationInPolygon(locationInside, testPolygon)
    val isNotInPolygon =
      GeoUtils.locationInPolygon(locationNotInside, testPolygon)

    assert(isInPolygon == true)
    assert(isNotInPolygon == false)
  }

  test(
    "edge case where location coords are the same as one of polygon points"
  ) {
    for point <- testPolygon.points do
      val location    = Location(name = "test", coordinates = point)
      val isInPolygon = GeoUtils.locationInPolygon(location, testPolygon)
      assert(isInPolygon == true)
  }

  test(
    "edge case where a location is on an edge of a polygon"
  ) {
    val polygonEdges = testPolygon.getEdges()
    for (x1, y1, x2, y2) <- polygonEdges do
      val midPoint = (((x1 + x2) / 2), ((y1 + y2) / 2))
      val location    = Location(name = "test", coordinates = midPoint)
      val isInPolygon = GeoUtils.locationInPolygon(location, testPolygon)
      assert(isInPolygon == true)

  }

  test("correctly determines if a location is inside any polygon") {
    val polygon1 = Polygon(points = List((10, 2), (12, 5), (12, 2)))
    val polygon2 = Polygon(points = List((1, 2), (3, 5), (3, 2)))
    val polygons = List(polygon1, polygon2)

    val locationInside    = Location(name = "inside", coordinates = (11.5, 3))
    val locationNotInside = Location(name = "outside", coordinates = (100, 100))

    val isInPolygon    = GeoUtils.locationInPolygons(locationInside, polygons)
    val isNotInPolygon =
      GeoUtils.locationInPolygons(locationNotInside, polygons)

    assert(isInPolygon == true)
    assert(isNotInPolygon == false)
  }
}
