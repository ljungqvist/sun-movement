package info.ljungqvist.sun

import scala.collection.mutable.ArrayBuffer

/**
  * Created on 24/08/15.
  *
  * @author Petter Ljungqvist (petter.ljungqvist@terdelle.com)
  */
class Sun(val position: Position) {

    import Angle._
    import JulianDate._
    import Sun._

    private val v0: Vector = Vector(1d, 0d, 0d).rot(1, -position.lat).rot(2, position.lng)

    def sinTheta(julianDate: JulianDate): Double = {
        val l = eclipticLongitude(julianDate)
        Vector(1d, 0d, 0d) *
            v0
                .rot(2, Rad(julianDate.hourPart * Angle.RAD_MAX) + l)
                .rot(1, -axialTilt(julianDate))
                .rot(2, -l)
    }

    //    def sinTheta(julianDate: JulianDate): Double = {
    //        val l = eclipticLongitude(julianDate)
    //        Vector(l.cos, l.sin, 0d) * v0.rot(2, meanLongitudeOfTheSun(julianDate)).rot(1, -axialTilt(julianDate))
    //    }

    private def sinThetaNewton(julianDayNumber: Double): Double = sinTheta(JD(julianDayNumber))

    private def sunPoleAngle(jd: JulianDate): Angle = (axialTilt(jd).sin * eclipticLongitude(jd).cos).acos

    private def d(a: Double, b: Double): Double = (b - a) / JD_D

    private def z_pi(v: Angle): Angle = if (v.isPositive) v else -v

    /**
      * Calculates when the sun the next time after "date", passes the angle in either rising or setting direction.
      *
      * @param angle  the angle to pass
      * @param rising true for rising, false for setting direction
      * @param date   the date after which the passing should take place
      * @return the next Julian Date a passing will accure
      */
    def nextPassing(angle: Angle, rising: Boolean, date: JulianDate): Passing = {
        var jdTmp = date
        val d_ang: Angle = sunPoleAngle(jdTmp + .25)
        val angleFromNorthPole: Angle = Rad(Math.PI / 2d) - this.position.lat
        val max: Angle = Rad(Math.PI / 2d) - z_pi(d_ang - angleFromNorthPole)
        val min: Angle = Rad(Math.PI / 2d) - z_pi(d_ang + angleFromNorthPole)
        if (max.inRad < angle.inRad) return Below
        if (min.inRad > angle.inRad) return Above

        val max_ : Double = min.sin + 0.6 * (max.sin - min.sin)
        val min_ : Double = min.sin + 0.4 * (max.sin - min.sin)
        var p: Double = sinTheta(jdTmp)
        var pn: Double = sinTheta(jdTmp + JD_D)
        var dp: Double = d(p, pn)
        if (if (rising) p > angle.sin else p < angle.sin) {
            while (if (rising) dp > 0 else dp < 0) {
                jdTmp += JD_STEP
                p = sinTheta(jdTmp)
                pn = sinTheta(jdTmp + JD_D)
                dp = d(p, pn)
            }
            while (if (rising) p > sinTheta(jdTmp - JD_STEP) else p < sinTheta(jdTmp - JD_STEP)) jdTmp += JD_STEP
        }
        var p_ : Double = sinTheta(jdTmp - JD_STEP)
        while (!(if (rising) p > p_ && p > min_ else p < p_ && p < max_)) {
            jdTmp += JD_STEP
            p_ = p
            p = sinTheta(jdTmp)
        }
        Passes(JulianDate(Newton.solve(sinThetaNewton, angle.sin, jdTmp.dayNumber, JD_D)))
    }

    private def getM(fwd: Boolean, day: Boolean, jd: JulianDate): JulianDate = {
        var jdTmp = jd
        val dir: Double = if (fwd) 1d else -1d
        var p: Double = sinTheta(jdTmp)
        var pn: Double = sinTheta(jdTmp + JD_D)
        var dp: Double = d(p, pn)
        while (if (fwd) if (day) dp <= 0d else dp >= 0d else if (day) dp >= 0d else dp <= 0d) {
            jdTmp += dir * JD_STEP
            p = sinTheta(jdTmp)
            pn = sinTheta(jdTmp + JD_D)
            dp = d(p, pn)
        }
        while (if (fwd) if (day) dp > 0d else dp < 0d else if (day) dp < 0d else dp > 0d) {
            jdTmp += dir * JD_STEP
            p = sinTheta(jdTmp)
            pn = sinTheta(jdTmp + JD_D)
            dp = d(p, pn)
        }
        JulianDate(Newton.diff(sinThetaNewton, 0d, jdTmp.dayNumber, JD_D))
    }

    private def cleanArr(arr: Array[Passing]): Array[Passing] = {
        var x: ArrayBuffer[Passing] = ArrayBuffer[Passing]()
        var last = JulianDate(-3d)
        //        for (i <- 0 until 6) {
        //            if (Math.abs(arr(i) - last) > 0.1) x += arr(i)
        //            last = arr(i)
        //        }
        x.toArray
    }

    //    def isBetween(fromAngle: Angle, fromRising: Boolean, fromMinutes: Int,
    //        toAngle: Angle, toRising: Boolean, toMinutes: Int,
    //        date: JulianDate): Boolean = {
    //        var jd_from = new Array[Passing](6)
    //        var jd_to = new Array[Passing](6)
    //
    //        for (i <- 0 until 6) {
    //            jd_from(i) = nextPassing(fromAngle, fromRising, date + 0.5 * i.toDouble - 1.5)
    //            jd_to(i) = nextPassing(toAngle, toRising, date + 0.5 * i.toDouble - 1.5)
    //        }
    //        jd_from = cleanArr(jd_from)
    //        jd_to = cleanArr(jd_to)
    //        if (jd_from.length == 1 && jd_from(0) < JD0 && jd_to.length == 1 && jd_to(0) < JD0) {
    //            if (jd_from(0) < JulianDate(1.5)) return fromRising && !toRising
    //            else return !fromRising && toRising
    //        }
    //        if (jd_from.length == 1 && jd_from(0) < JD0) {
    //            jd_from = new Array[JulianDate](2)
    //            val day: Boolean = fromAngle.inRad > toAngle.inRad
    //            jd_from(0) = getM(false, day, date)
    //            jd_from(1) = getM(true, day, date)
    //        }
    //        if (jd_to.length == 1 && jd_to(0) < JD0) {
    //            jd_to = new Array[JulianDate](2)
    //            val day: Boolean = fromAngle.inRad < toAngle.inRad
    //            jd_to(0) = getM(false, day, date)
    //            jd_to(1) = getM(true, day, date)
    //        }
    //        if (fromAngle == toAngle && fromRising == toRising) {
    //            for (i <- 0 until jd_from.length - 1)
    //                if (if (fromMinutes < toMinutes) jd_from(i) + DAYS_IN_MINUTE * fromMinutes <= date && jd_from(i) + DAYS_IN_MINUTE * toMinutes > date
    //                else jd_from(i) + DAYS_IN_MINUTE * fromMinutes <= date && jd_from(i + 1) + DAYS_IN_MINUTE * toMinutes > date) return true
    //
    //            return false
    //        }
    //        var fi: Int = 0
    //        var ti: Int = 0
    //        var from: Array[JulianDate] = new Array[JulianDate](0)
    //        var to: Array[JulianDate] = new Array[JulianDate](0)
    //        while (fi < jd_from.length && ti < jd_to.length) {
    //            if (jd_from(fi) < jd_to(ti)) {
    //                from +:= jd_from(fi)
    //                to +:= jd_to(ti)
    //                fi += 1
    //                ti += 1
    //            }
    //            else {
    //                ti += 1
    //                ti
    //            }
    //        }
    //        for (i <- from.indices)
    //            if (from(i) + DAYS_IN_MINUTE * fromMinutes <= date && to(i) + DAYS_IN_MINUTE * toMinutes > date)
    //                return true
    //        false
    //    }

}

object Sun {

    import Angle._

    def apply(lat: Double, lng: Double) = new Sun(Position(Deg(lat), Deg(lng)))

    private def axialTilt(jd: JulianDate): Angle = Deg(23.439d) - Deg(.0000004) * jd.j2000

    //private val MILLISECONDS_PER_DAY: Double = 24d * 60d * 60d * 1000d
    private val JD_D: Double = 0.00001
    private val JD_STEP: Double = 0.01
    private val DAYS_IN_MINUTE: Double = 1 / 24 / 60

    private val DEG_1_195 = Deg(1.915d)
    private val DEG_0_020 = Deg(0.020d)
    private val VAL_0_02906 = 0.0290572732640d
    private val VAL_0_00274 = 0.00273781191135448d
    private val DEG_357_528 = Deg(357.528d)
    private val DEG_0_986 = Deg(0.9856003d)


    // Ecliptic coordinates

    def eclipticLongitude(julianDate: JulianDate): Angle = {
        val L = meanLongitudeOfTheSun(julianDate)
        val g = meanAnomalyOfTheSun(julianDate)
        L + DEG_1_195 * g.sin + DEG_0_020 * (g * 2d).sin
    }

    private def meanLongitudeOfTheSun(julianDate: JulianDate): Angle =
        Rad(2 * Math.PI * (VAL_0_02906 + VAL_0_00274 * julianDate.j2000))

    private[sun] def meanAnomalyOfTheSun(julianDate: JulianDate): Angle =
        DEG_357_528 + DEG_0_986 * julianDate.j2000


    // Equatorial coordinates

    private def rightAscension(eclipticLongitude: Angle, tilt: Angle): Angle =
        Rad(Math.atan2(tilt.cos * eclipticLongitude.sin, eclipticLongitude.cos))

    private def declination(eclipticLongitude: Angle, tilt: Angle) =
        (tilt.sin * eclipticLongitude.sin).asin

    sealed abstract class Passing()

    case object Above extends Passing

    case object Below extends Passing

    case class Passes(julianDate: JulianDate) extends Passing

}
