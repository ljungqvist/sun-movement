package info.ljungqvist.sun

import info.ljungqvist.sun.Angle._
import org.scalatest.{MustMatchers, WordSpec}

/**
  * Created on 03/09/15.
  *
  * @author Petter Ljungqvist (petter.ljungqvist@houston-inc.com)
  */
class AngleTest extends WordSpec with MustMatchers {

    "The radians" should {
        "be between 0 an 2Pi" in {
            Rad(2d) mustEqual Rad(2d)
            Rad(1d + RAD_MAX) mustEqual Rad(1d)
            Rad(1d + 2d * RAD_MAX) mustEqual Rad(1d)
            Rad(1d - RAD_MAX) mustEqual Rad(1d)
        }


        "be added to degrees" in {
            Rad(1d) + Deg(180d) mustEqual Rad(1 + RAD_MAX / 2d)
        }

        "equal degrees" in {
            Rad(0d) mustEqual Deg(0d)
            Rad(RAD_MAX / 2d) mustEqual Deg(180d)
        }

        "be addable" in {
            Rad(RAD_MAX / 4d) + Rad(RAD_MAX / 4d) mustEqual Rad(RAD_MAX / 2d)
            Rad(RAD_MAX / 4d) + Rad(RAD_MAX / 4d) + Deg(90d) mustEqual Rad(3d * RAD_MAX / 4d)
        }

        "work with multiplication" in {
            Rad(1) * 1.3 mustEqual Rad(1.3)
        }

    }

    "Trigonometric functions" should {

        val d = 0.0000000001

        s"evaluate correctly for " in {
            var a = 0d
            val step = 0.00001
            while (a < 2 * Math.PI) {
                val ang1 = Rad(a)
                val tan = Math.tan(a)
                val dTan = Math.abs(tan * Math.sqrt(d) + d)
                ang1.sin mustEqual Math.sin(a) +- d
                ang1.cos mustEqual Math.cos(a) +- d
                ang1.tan mustEqual tan +- dTan
                val ang2 = Rad(a)
                ang2.cos mustEqual Math.cos(a) +- d
                ang2.sin mustEqual Math.sin(a) +- d
                ang2.tan mustEqual tan +- dTan
                a += step
            }
        }

    }

}
