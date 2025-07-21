import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import com.internshiptask.Models.{Point, Polygon}

class GetEdgesTest extends AnyFunSuite with Matchers {

  test("should correctly form edges") {
    val (point1, point2, point3) =
      (Point.unsafeApply(1, 2), Point.unsafeApply(3, 5), Point.unsafeApply(3, 2))
    val polygon                  = Polygon(points = List(point1, point2, point3))
    val expectedEdges            = List((point1, point2), (point2, point3), (point3, point1))
    val edges                    = polygon.getEdges()

    edges shouldBe expectedEdges
  }
}
