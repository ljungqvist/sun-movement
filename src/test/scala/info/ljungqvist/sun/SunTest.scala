package info.ljungqvist.sun

import java.text.SimpleDateFormat
import java.util.TimeZone

import org.scalatest.{MustMatchers, WordSpec}

class SunTest extends WordSpec with MustMatchers {

    import Angle._
    import JulianDate._

    private def date(dateString: String): JulianDate = {
        val df = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        df.setTimeZone(TimeZone.getTimeZone("GMT"))
        JD(df.parse(dateString))
    }


    private val date20171221_1628 = date("2017-12-21 16:28")
    private val date20170621_0424 = date("2017-06-21 04:24")

    "Ecliptic longitude" should {
        "be calculated from Winter Solstice" in {
            Sun.eclipticLongitude(date("2000-03-22 04:51")).inDeg mustEqual 90d +- 2d
            Sun.eclipticLongitude(date20171221_1628).inDeg mustEqual 0d +- 0.5
            Sun.eclipticLongitude(date20170621_0424).inDeg mustEqual 180d +- 0.5
        }
    }

    "Solar angle over the horizon" should {
        "match over the equator" in {
            val sun = Sun(0, 0)

            sun.sinTheta(date("2000-03-22 12:00")).asin.inDeg mustEqual 90d +- 1d
            sun.sinTheta(date("2000-03-22 12:00")).asin.inDeg mustEqual 90d +- 1d
            sun.sinTheta(date("2017-12-21 12:00")).asin.inDeg mustEqual (90d - 23.4) +- .1
            sun.sinTheta(date("2017-06-21 00:00")).asin.inDeg mustEqual (-90d + 23.4) +- .1
            sun.sinTheta(date("2017-06-21 06:00")).asin.inDeg mustEqual 0d +- .1
            sun.sinTheta(date("2017-06-21 12:00")).asin.inDeg mustEqual (90d - 23.4) +- .1
            sun.sinTheta(date("2017-06-21 18:00")).asin.inDeg mustEqual 0d +- .1


            val sun2 = Sun(0, 90)
            sun2.sinTheta(date("2000-03-22 06:00")).asin.inDeg mustEqual 90d +- 1d
            sun2.sinTheta(date("2017-12-21 06:00")).asin.inDeg mustEqual (90d - 23.4) +- .1
            sun2.sinTheta(date("2017-06-20 18:00")).asin.inDeg mustEqual (-90d + 23.4) +- .1
            sun2.sinTheta(date("2017-06-21 00:00")).asin.inDeg mustEqual 0d +- .1
            sun2.sinTheta(date("2017-06-21 06:00")).asin.inDeg mustEqual (90d - 23.4) +- .1
            sun2.sinTheta(date("2017-06-21 12:00")).asin.inDeg mustEqual 0d +- .1
        }
        "match over the north pole" in {

            val sun = Sun(90, 0)

            sun.sinTheta(date("2000-03-22 04:53")).asin.inDeg mustEqual 0d +- 1d
            sun.sinTheta(date20171221_1628).asin.inDeg mustEqual -23.4 +- .1
            sun.sinTheta(date20170621_0424).asin.inDeg mustEqual 23.4 +- .1

        }
        "match over Nürnberg" in {

            val sun = Sun(49.49500, 11.07300)

            sun.sinTheta(date("2017-05-23 18:58")).asin.inDeg mustEqual 0d +- 1d
            sun.sinTheta(date("2017-05-01 12:00")).asin.inDeg mustEqual 54d +- 1d

        }
    }

    "The next passings" should {

        "be the following times over the equator" in {
            val sun = Sun(0, 0)

            val spring = date("2000-03-22 00:51")

            val minute = 1d / 60d

            sun.nextPassing(Rad(0), true, spring).asInstanceOf[Sun.Passes].julianDate.dayNumber mustEqual date("2000-03-22 06:00").dayNumber +- minute
            sun.nextPassing(Rad(0), false, spring).asInstanceOf[Sun.Passes].julianDate.dayNumber mustEqual date("2000-03-22 18:00").dayNumber +- minute
            sun.nextPassing(Deg(89), false, spring).asInstanceOf[Sun.Passes].julianDate.dayNumber mustEqual date("2000-03-22 12:00").dayNumber +- minute
            sun.nextPassing(Deg(-90), false, spring) mustEqual Sun.Above
            sun.nextPassing(Deg(90), false, spring) mustEqual Sun.Below
        }

    }


}
