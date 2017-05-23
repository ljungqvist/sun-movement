package info.ljungqvist.sun

import java.util.Date

import org.scalatest.{MustMatchers, WordSpec}

/**
  * @author Petter Ljungqvist (petter.ljungqvist@houston-inc.com) on 06/02/16.
  */
class JulianDateTest extends WordSpec with MustMatchers {

    "Dates" should {
        "be converted correctly to Julian Dates" in {
            // Sat, 06 Feb 2016 21:35:04 GMT
            JulianDate(new Date(1454794504000l)).dayNumber mustEqual (2457425.39935d +- .000005d)
            // Thu, 01 Jan 1970 00:00:00 GMT
            JulianDate(new Date(0l)).dayNumber mustEqual (2440587.5d +- .000005d)
            JulianDate.JD1970.dayNumber mustEqual (2440587.5d +- .000005d)
            // Sat, 01 Jan 2000 12:00:00 GMT
            JulianDate(new Date(946728000000l)).dayNumber mustEqual (2451545d +- .000005d)
            JulianDate.JD2000.dayNumber mustEqual (2451545d +- .000005d)
        }
    }

    "Julian dates" should {
        "return hour part" in {
            JulianDate(123.456).hourPart mustEqual .456 +- .0000000001
        }
    }

}
