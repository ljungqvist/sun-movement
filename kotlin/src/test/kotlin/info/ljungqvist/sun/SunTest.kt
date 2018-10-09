package info.ljungqvist.sun

import org.junit.Assert
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.text.SimpleDateFormat
import java.util.*

@RunWith(JUnitPlatform::class)
class SunTest : Spek({


    fun date(dateString: String): JulianDate = run {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        df.timeZone = TimeZone.getTimeZone("GMT")
        df.parse(dateString).toJulianDate()
    }

    val date20171221_1628 = date("2017-12-21 16:28")
    val date20170621_0424 = date("2017-06-21 04:24")

    describe("Ecliptic longitude") {

        it("should be calculated from Winter Solstice") {
            Assert.assertEquals(Sun.eclipticLongitude(date("2000-03-22 04:51")).inDeg, 90.0, 2.0)
            Assert.assertEquals(Sun.eclipticLongitude(date20171221_1628).inDeg, 0.0, .5)
            Assert.assertEquals(Sun.eclipticLongitude(date20170621_0424).inDeg, 180.0, .5)
        }

    }

    describe("Solar angle over the horizon") {

        it("over the equator") {

            val sun = Sun.fromDeg(0.0, 0.0)
            Assert.assertEquals(sun.sinTheta(date("2000-03-22 12:00")).asin.inDeg, 90.0, 1.0)
            Assert.assertEquals(sun.sinTheta(date("2000-03-22 12:00")).asin.inDeg, 90.0, 1.0)
            Assert.assertEquals(sun.sinTheta(date("2017-12-21 12:00")).asin.inDeg, (90.0 - 23.4), .1)
            Assert.assertEquals(sun.sinTheta(date("2017-06-21 00:00")).asin.inDeg, (-90.0 + 23.4), .1)
            Assert.assertEquals(sun.sinTheta(date("2017-06-21 06:00")).asin.inDeg, 0.0, .1)
            Assert.assertEquals(sun.sinTheta(date("2017-06-21 12:00")).asin.inDeg, (90.0 - 23.4), .1)
            Assert.assertEquals(sun.sinTheta(date("2017-06-21 18:00")).asin.inDeg, 0.0, .1)

            val sun2 = Sun.fromDeg(0.0, 90.0)
            Assert.assertEquals(sun2.sinTheta(date("2000-03-22 06:00")).asin.inDeg, 90.0, 1.0)
            Assert.assertEquals(sun2.sinTheta(date("2017-12-21 06:00")).asin.inDeg, (90.0 - 23.4), .1)
            Assert.assertEquals(sun2.sinTheta(date("2017-06-20 18:00")).asin.inDeg, (-90.0 + 23.4), .1)
            Assert.assertEquals(sun2.sinTheta(date("2017-06-21 00:00")).asin.inDeg, 0.0, .1)
            Assert.assertEquals(sun2.sinTheta(date("2017-06-21 06:00")).asin.inDeg, (90.0 - 23.4), .1)
            Assert.assertEquals(sun2.sinTheta(date("2017-06-21 12:00")).asin.inDeg, 0.0, .1)
        }

        it(" over the north pole") {

            val sun = Sun.fromDeg(90.0, 0.0)
            Assert.assertEquals(sun.sinTheta(date("2000-03-22 04:53")).asin.inDeg, 0.0, 1.0)
            Assert.assertEquals(sun.sinTheta(date20171221_1628).asin.inDeg, -23.4, .1)
            Assert.assertEquals(sun.sinTheta(date20170621_0424).asin.inDeg, 23.4, .1)

        }

        it("over Nürnberg") {

            val sun = Sun.fromDeg(49.49500, 11.07300)
            Assert.assertEquals(sun.sinTheta(date("2017-05-23 18:58")).asin.inDeg, 0.0, 1.0)
            Assert.assertEquals(sun.sinTheta(date("2017-05-01 12:00")).asin.inDeg, 54.0, 1.0)
            Assert.assertEquals(sun.sinTheta(date("2017-06-13 17:26")).asin.inDeg, 15.97, 1.0)
        }

    }

    describe("The next passings") {
        val minute = 4.0 / 24.0 / 60.0

        it("over the equator") {

            val sun = Sun.fromDeg(0.0, 0.0)

            val spring = date("2000-03-22 00:51")
            Assert.assertEquals(sun.nextPassing(rad(0.0), true, spring).let { it as Sun.Passing.Passes }.julianDate.dayNumber, date("2000-03-22 06:00").dayNumber, minute)
            Assert.assertEquals(sun.nextPassing(rad(0.0), false, spring).let { it as Sun.Passing.Passes }.julianDate.dayNumber, date("2000-03-22 18:00").dayNumber, minute)
            Assert.assertEquals(sun.nextPassing(deg(89.0), false, spring).let { it as Sun.Passing.Passes }.julianDate.dayNumber, date("2000-03-22 12:00").dayNumber, minute)
            Assert.assertEquals(Sun.Passing.Above, sun.nextPassing(deg(-90.0), false, spring))
            Assert.assertEquals(Sun.Passing.Below, sun.nextPassing(deg(90.0), false, spring))
        }

        it("in Nürnberg") {
            val nurnberg = Sun.fromDeg(49.49500, 11.07300)
            Assert.assertEquals(nurnberg.nextPassing(deg(15.97), false, date("2017-06-12 20:00")).let { it as Sun.Passing.Passes }.julianDate.dayNumber, date("2017-06-13 17:26").dayNumber, minute)
        }

        it("at the North Pole") {
            val northPole = Sun.fromDeg(90.0, 0.0)
            Assert.assertEquals(Sun.Passing.Above, northPole.nextPassing(deg(0.0), false, date("2017-06-13 12:00")))
            Assert.assertEquals(Sun.Passing.Below, northPole.nextPassing(deg(0.0), false, date("2017-01-13 12:00")))
            Assert.assertEquals(Sun.Passing.Below, northPole.nextPassing(deg(0.0), false, date("2017-03-19 12:00")))
            Assert.assertEquals(Sun.Passing.Above, northPole.nextPassing(deg(0.0), false, date("2017-03-24 12:00")))
        }

    }

    describe("The sun should") {

        it("be between at the equator") {

            val sun = Sun.fromDeg(0.0, 0.0)
            Assert.assertEquals(true, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 12:00")))
            Assert.assertEquals(true, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 06:01")))
            Assert.assertEquals(true, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 17:59")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 00:00")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 05:59")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 18:01")))
            Assert.assertEquals(true, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 00:00")))
            Assert.assertEquals(true, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 05:59")))
            Assert.assertEquals(true, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 18:01")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 12:00")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 06:01")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 17:59")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 06:00")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 03:01")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 08:59")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 18:00")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 02:59")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 09:01")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 18:00")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 15:01")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 20:59")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 06:00")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 14:59")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 21:01")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 09:01")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 12:00")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 18:00")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 00:00")))
            Assert.assertEquals(true, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 02:59")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 03:01")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 06:00")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 08:59")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 21:01")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 00:00")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 06:00")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 12:00")))
            Assert.assertEquals(true, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 14:59")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 15:01")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 18:00")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 20:59")))
        }

        it("not be between at the equator") {
            val sun = Sun.fromDeg(0.0, 0.0)
            Assert.assertEquals(false, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 12:00")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 06:10")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), false, 0, deg(0.0), true, 0, date("2017-03-22 17:50")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 00:00")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 05:50")))
            Assert.assertEquals(false, sun.isBetween(deg(0.0), true, 0, deg(0.0), false, 0, date("2017-03-22 18:10")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 06:00")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 03:10")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), true, 0, deg(-45.0), true, 0, date("2017-03-22 08:50")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 18:00")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 15:10")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), false, 0, deg(45.0), false, 0, date("2017-03-22 20:50")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 09:10")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 12:00")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 18:00")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 00:00")))
            Assert.assertEquals(false, sun.isBetween(deg(-45.0), true, 0, deg(45.0), true, 0, date("2017-03-22 02:50")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 21:10")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 00:00")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 06:00")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 12:00")))
            Assert.assertEquals(false, sun.isBetween(deg(45.0), false, 0, deg(-45.0), false, 0, date("2017-03-22 14:50")))


        }
    }

})