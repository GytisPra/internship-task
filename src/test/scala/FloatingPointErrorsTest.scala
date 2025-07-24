import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import com.internshiptask.Models.{Coordinate, Precision}

class FloatingPointErrorsTest extends AnyFunSuite with Matchers:

  test("(0.2 + 0.1 == 0.3) should be true") {
    given precision: Precision = Precision(1e-5)

    // In scala  0.2 + 0.1 is 0.30000000000000004
    // So 0.2 + 0.1 will not equal 0.3 which is incorrect
    // Unless we increase precision to be 1e-17
    (0.2 + 0.1 == 0.3) shouldBe false
    (Coordinate(0.2 + 0.1) ~= Coordinate(0.3)) shouldBe true
  }

  test("(0.2 + 0.1 > 0.3) should be false") {
    given precision: Precision = Precision(1e-5)

    // In scala 0.2 + 0.1 is 0.30000000000000004
    // So 0.2 + 0.1 > 0.3 will return true which is incorrect
    (0.2 + 0.1 > 0.3) shouldBe true
    (Coordinate(0.2 + 0.1) > Coordinate(0.3)) shouldBe false
  }

  test("(0.2 + 0.1 < 0.3) should be false") {
    given precision: Precision = Precision(1e-5)

    // In scala 0.2 + 0.1 is 0.30000000000000004
    // So 0.2 + 0.1 < 0.3 will return false which is correct but for the wrong reasons
    (0.2 + 0.1 < 0.3) shouldBe false
    (Coordinate(0.2 + 0.1) < Coordinate(0.3)) shouldBe false
  }
