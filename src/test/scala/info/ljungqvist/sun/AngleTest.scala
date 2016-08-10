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
            "wer" mustEqual "wer"
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
    }

}
