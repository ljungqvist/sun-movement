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
        "equator" in {
            val sun = Sun(0, 0)

            sun.sinTheta(JD(new Date(953726400000L))).asin.inDeg mustEqual 90d +- 1d // 2000-03-22 12:00
            sun.sinTheta(JD(new Date(1513857600000L))).asin.inDeg mustEqual (90d-23.4) +- .1 // 2017-12-21 12:00
            sun.sinTheta(JD(new Date(1498003200000L))).asin.inDeg mustEqual (-90d+23.4) +- .1 // 2017-06-21 00:00
            sun.sinTheta(JD(new Date(1498024800000L))).asin.inDeg mustEqual 0d +- .1 // 2017-06-21 06:00
            sun.sinTheta(JD(new Date(1498046400000L))).asin.inDeg mustEqual (90d-23.4) +- .1 // 2017-06-21 12:00
            sun.sinTheta(JD(new Date(1498068000000L))).asin.inDeg mustEqual 0d +- .1 // 2017-06-21 18:00

            //+ 6h
            val sun2 = Sun(0, 90)
            sun2.sinTheta(JD(new Date(953726400000L - 21600000L))).asin.inDeg mustEqual 90d +- 1d // 2000-03-22 12:00
            sun2.sinTheta(JD(new Date(1513857600000L - 21600000L))).asin.inDeg mustEqual (90d-23.4) +- .1 // 2017-12-21 12:00
            sun2.sinTheta(JD(new Date(1498003200000L - 21600000L))).asin.inDeg mustEqual (-90d+23.4) +- .1 // 2017-06-21 00:00
            sun2.sinTheta(JD(new Date(1498024800000L - 21600000L))).asin.inDeg mustEqual 0d +- .1 // 2017-06-21 06:00
            sun2.sinTheta(JD(new Date(1498046400000L - 21600000L))).asin.inDeg mustEqual (90d-23.4) +- .1 // 2017-06-21 12:00
            sun2.sinTheta(JD(new Date(1498068000000L - 21600000L))).asin.inDeg mustEqual 0d +- .1 // 2017-06-21 18:00
        }
        "north pole" in {

            val sun = Sun(90, 0)

            sun.sinTheta(JD(new Date(953700660000L))).asin.inDeg mustEqual 0d +- 1d // 2000-03-22 04:53
            sun.sinTheta(JD(date20171221_1628)).asin.inDeg mustEqual -23.4 +- .1
            sun.sinTheta(JD(date20170621_0424)).asin.inDeg mustEqual 23.4 +- .1

        }
        "Nurnberg" in {

            val sun = Sun(49.49500, 11.07300)

            sun.sinTheta(JD(new Date(1495565880000L))).asin.inDeg mustEqual 0d +- 1d // 2017-05-23 18:58
            sun.sinTheta(JD(new Date(1493640000000L))).asin.inDeg mustEqual 54d +- 1d // 2017-05-01 12:00

        }
    }

}
