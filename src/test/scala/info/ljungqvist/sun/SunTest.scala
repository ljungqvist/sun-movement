package info.ljungqvist.sun

import java.util.Date

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Tests for [[Sun]]
  *
  * @author Petter Ljungqvist (petter.ljungqvist@houston-inc.com) on 08/02/16.
  */
class SunTest extends WordSpec with MustMatchers {

    import Angle._
    import JulianDate._

    "The ecliptic longitude" should {
        "sdf" in {

            val sun = Sun(49.49500, 11.07300)

            sun.sinTheta(JD(new Date(1495541938000L))).asin.inDeg mustEqual -0.01 +- .000001 // 2017-05-23 18:58
            sun.sinTheta(JD(new Date(1493640000000L))).asin.inDeg mustEqual 54.36 +- .000001 // 2017-05-01 12:00

        }
    }

}
