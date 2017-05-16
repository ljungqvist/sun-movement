package info.ljungqvist.sun

import org.scalatest.{MustMatchers, WordSpec}

/**
  * @author Petter Ljungqvist (petter.ljungqvist@houston-inc.com) on 16/05/2017.
  */
class NewtonTest extends WordSpec with MustMatchers {

    val d = 0.00001
    val d2: Double = d / 2d

    "The root of 3x + 2 = 5" should {
        "be 1" in {
            Newton(x => 3 * x + 2, 0, 5, d) mustEqual 1d +- d2
        }
    }

    "The root of 3x + 2 = 122" should {
        "be 40" in {
            Newton(x => 3 * x + 2, 0, 122, d) mustEqual 40d +- d2
        }
    }

    "The root of x^2 + 2x = 3.41" should {
        "be 1.1 when starting from 0" in {
            Newton(x => x * x + 2 * x, 0, 3.41, d) mustEqual 1.1 +- d2
        }
        "be -3.1 when starting from -10" in {
            Newton(x => x * x + 2 * x, -10, 3.41, d) mustEqual -3.1 +- d2
        }
    }

    "The root of x^3 + 2x^2 - 4x = 4.5" should {
        "be -2.84954 when starting from -4" in {
            Newton(x => x * x * x + 2 * x * x - 4 * x, -4, 4.5, d) mustEqual -2.84954 +- d2
        }
        "be -0.901741 when starting from 0" in {
            Newton(x => x * x * x + 2 * x * x - 4 * x, 0, 4.5, d) mustEqual -0.901741 +- d2
        }
        "be 1.75128 when starting from 1" in {
            Newton(x => x * x * x + 2 * x * x - 4 * x, 1, 4.5, d) mustEqual 1.75128 +- d2
        }
    }

    "The root of d(x^2 + 2x) = 0" should {
        "be -1 when starting from 0" in {
            Newton.diff(x => x * x + 2 * x, 0, 6, d) mustEqual 2d +- d2
        }
    }

}
