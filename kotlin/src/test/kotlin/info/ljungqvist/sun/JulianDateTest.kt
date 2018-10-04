package info.ljungqvist.sun

import org.junit.Assert
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

@RunWith(JUnitPlatform::class)
object JulianDateTest : Spek({

    describe("Julian Dates") {

        it("should be convertible to dates") {

            // Sat, 06 Feb 2016 21:35:04 GMT
            Assert.assertEquals(Date(1454794504000).toJulianDate().dayNumber, 2457425.39935, .000005)
            // Thu, 01 Jan 1970 00:00:00 GMT
            Assert.assertEquals(Date(0).toJulianDate().dayNumber, 2440587.5, .000005)
            Assert.assertEquals(JulianDate.Y1970.dayNumber, 2440587.5, .000005)
            // Sat, 01 Jan 2000 12:00:00 GMT
            Assert.assertEquals(Date(946728000000).toJulianDate().dayNumber, 2451545.0, .000005)
            Assert.assertEquals(JulianDate.Y2000.dayNumber, 2451545.0, .000005)

        }

        it("hourPart should return the fraction part of the Julian Date") {
            Assert.assertEquals(JulianDate(123.456).hourPart , .456 , .0000000001)
        }

    }

})