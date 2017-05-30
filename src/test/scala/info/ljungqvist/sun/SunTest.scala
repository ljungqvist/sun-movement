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

    val date20171221_1628 = new Date(1513873680000L)
    val date20170621_0424 = new Date(1498019040000L)

    "Ecliptic longitude" should {
        "be calculated from Winter Solstice" in {
            Sun.eclipticLongitude(JD(new Date(953700660000L))).inDeg mustEqual 90d +- 2d
            Sun.eclipticLongitude(JD(date20171221_1628)).inDeg mustEqual 0d +- 0.5
            Sun.eclipticLongitude(JD(date20170621_0424)).inDeg mustEqual 180d +- 0.5
        }
    }

    "Solar angle over the horizon" should {
        "Nurnberg" in {

            val sun = Sun(49.49500, 11.07300)

            sun.sinTheta(JD(new Date(1495541938000L))).asin.inDeg mustEqual -0.01 +- .000001 // 2017-05-23 18:58
            sun.sinTheta(JD(new Date(1493640000000L))).asin.inDeg mustEqual 54.36 +- .000001 // 2017-05-01 12:00

        }
        "north pole" in {

            val sun = Sun(90, 0)

            sun.sinTheta(JD(new Date(953700660000L))).asin.inDeg mustEqual 0d +- 1d // 2000-03-22 04:53
            sun.sinTheta(JD(date20171221_1628)).asin.inDeg mustEqual -23.4 +- .1// 2017-05-23 18:58
            sun.sinTheta(JD(date20170621_0424)).asin.inDeg mustEqual 23.4 +- .1 // 2017-05-01 12:00

        }
    }

}
