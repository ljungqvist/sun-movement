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
            sun.sinTheta(date("2017-06-13 17:26")).asin.inDeg mustEqual 15.97d +- 1d

        }
    }

    "The next passings" should {

        val minute = 4d / 24d / 60d

        "be at the following times over the equator" in {
            val sun = Sun(0, 0)

            val spring = date("2000-03-22 00:51")

            sun.nextPassing(Rad(0), true, spring).asInstanceOf[Sun.Passes].julianDate.dayNumber mustEqual date("2000-03-22 06:00").dayNumber +- minute
            sun.nextPassing(Rad(0), false, spring).asInstanceOf[Sun.Passes].julianDate.dayNumber mustEqual date("2000-03-22 18:00").dayNumber +- minute
            sun.nextPassing(Deg(89), false, spring).asInstanceOf[Sun.Passes].julianDate.dayNumber mustEqual date("2000-03-22 12:00").dayNumber +- minute
            sun.nextPassing(Deg(-90), false, spring) mustEqual Sun.Above
            sun.nextPassing(Deg(90), false, spring) mustEqual Sun.Below
        }

        "be at the following times in Nürnberg" in {
            val nurnberg = Sun(49.49500, 11.07300)

            nurnberg.nextPassing(Deg(15.97), false, date("2017-06-12 20:00")).asInstanceOf[Sun.Passes].julianDate.dayNumber mustEqual
                date("2017-06-13 17:26").dayNumber +- minute
        }

        "be at the following times at the North Pole" in {
            val northPole = Sun(90, 0)

            northPole.nextPassing(Deg(0), false, date("2017-06-13 12:00")) mustEqual Sun.Above
            northPole.nextPassing(Deg(0), false, date("2017-01-13 12:00")) mustEqual Sun.Below
            northPole.nextPassing(Deg(0), false, date("2017-03-19 12:00")) mustEqual Sun.Below
            northPole.nextPassing(Deg(0), false, date("2017-03-24 12:00")) mustEqual Sun.Above
        }

    }

    "The sun" should {
        "be between, at the equator" in {
            val sun = Sun(0, 0)
            sun.isBetween(Deg(0), true, 0, Deg(0), false, 0, date("2017-03-22 12:00")) mustEqual true
            sun.isBetween(Deg(0), true, 0, Deg(0), false, 0, date("2017-03-22 06:10")) mustEqual true
            sun.isBetween(Deg(0), true, 0, Deg(0), false, 0, date("2017-03-22 17:50")) mustEqual true

            sun.isBetween(Deg(0), false, 0, Deg(0), true, 0, date("2017-03-22 00:00")) mustEqual true
            sun.isBetween(Deg(0), false, 0, Deg(0), true, 0, date("2017-03-22 05:50")) mustEqual true
            sun.isBetween(Deg(0), false, 0, Deg(0), true, 0, date("2017-03-22 18:10")) mustEqual true

            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 06:00")) mustEqual true
            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 03:10")) mustEqual true
            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 08:50")) mustEqual true

            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 18:00")) mustEqual true
            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 15:10")) mustEqual true
            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 20:50")) mustEqual true

            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 09:10")) mustEqual true
            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 12:00")) mustEqual true
            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 18:00")) mustEqual true
            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 00:00")) mustEqual true
            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 02:50")) mustEqual true

            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 21:10")) mustEqual true
            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 00:00")) mustEqual true
            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 06:00")) mustEqual true
            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 12:00")) mustEqual true
            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 14:50")) mustEqual true
        }

        "not be between, at the equator" in {
            val sun = Sun(0, 0)
            sun.isBetween(Deg(0), false, 0, Deg(0), true, 0, date("2017-03-22 12:00")) mustEqual false
            sun.isBetween(Deg(0), false, 0, Deg(0), true, 0, date("2017-03-22 07:00")) mustEqual false
            sun.isBetween(Deg(0), false, 0, Deg(0), true, 0, date("2017-03-22 17:00")) mustEqual false

            sun.isBetween(Deg(0), true, 0, Deg(0), false, 0, date("2017-03-22 00:00")) mustEqual false
            sun.isBetween(Deg(0), true, 0, Deg(0), false, 0, date("2017-03-22 05:50")) mustEqual false
            sun.isBetween(Deg(0), true, 0, Deg(0), false, 0, date("2017-03-22 18:10")) mustEqual false

            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 06:00")) mustEqual false
            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 03:10")) mustEqual false
            sun.isBetween(Deg(45), true, 0, Deg(-45), true, 0, date("2017-03-22 08:50")) mustEqual false

            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 18:00")) mustEqual false
            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 15:10")) mustEqual false
            sun.isBetween(Deg(-45), false, 0, Deg(45), false, 0, date("2017-03-22 20:50")) mustEqual false

            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 09:10")) mustEqual false
            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 12:00")) mustEqual false
            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 18:00")) mustEqual false
            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 00:00")) mustEqual false
            sun.isBetween(Deg(-45), true, 0, Deg(45), true, 0, date("2017-03-22 02:50")) mustEqual false

            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 21:10")) mustEqual false
            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 00:00")) mustEqual false
            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 06:00")) mustEqual false
            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 12:00")) mustEqual false
            sun.isBetween(Deg(45), false, 0, Deg(-45), false, 0, date("2017-03-22 14:50")) mustEqual false
        }
    }


}
