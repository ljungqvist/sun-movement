package info.ljungqvist.sun

import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable.ArrayBuffer

/**
  * Created on 24/08/15.
  *
  * @author Petter Ljungqvist (petter.ljungqvist@terdelle.com)
  */
class Sun(val position: Position) extends LazyLogging {

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

    private def sunPoleAngle(jd: JulianDate): Angle = (axialTilt(jd).sin * (-eclipticLongitude(jd).cos)).acos

    private def d(a: Double, b: Double): Double = (b - a) / JD_D

    private def z_pi(v: Angle): Angle = if (v.isPositive) v else -v

    /**
      * Calculates when the sun the next time after "date", passes the angle in either rising or setting direction.
      *
      * @param angle  the angle to pass
      * @param rising true for rising, false for setting direction
      * @param date   the date after which the passing should take place
      * @return the next Julian Date a passing will occur
      */
    def nextPassing(angle: Angle, rising: Boolean, date: JulianDate): Passing = {
        var jdTmp = date
        val spAngle: Angle = sunPoleAngle(jdTmp + .25)
        val angleFromNorthPole: Angle = Rad(Math.PI / 2d) - this.position.lat
        val sunMaxAngle: Angle = Rad(Math.PI / 2d) - z_pi(spAngle - angleFromNorthPole)
        val sunMinAngle: Angle = Rad(Math.PI / 2d) - z_pi(spAngle + angleFromNorthPole)

        if (sunMaxAngle.inRad < angle.inRad) return Below
        if (sunMinAngle.inRad > angle.inRad) return Above

        var p: Double = sinTheta(jdTmp)
        if (if (rising) p > angle.sin else p < angle.sin) {
            while (if (rising) p < sinTheta(jdTmp + JD_D) else p > sinTheta(jdTmp + JD_D)) {
                jdTmp += JD_STEP
                p = sinTheta(jdTmp)
            }
            while (if (rising) p > sinTheta(jdTmp - JD_STEP) else p < sinTheta(jdTmp - JD_STEP))
                jdTmp += JD_STEP
        }

        val max_ : Double = sunMinAngle.sin + 0.6 * (sunMaxAngle.sin - sunMinAngle.sin)
        val min_ : Double = sunMinAngle.sin + 0.4 * (sunMaxAngle.sin - sunMinAngle.sin)
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
        var last: Passing = NotSetPassing
        for (i <- 0 until 6) {
            if (!arr(i).isClose(last)) x += arr(i)
            last = arr(i)
        }
        x.toArray
    }

    private def getAngleAndDirection(date: JulianDate): AngleAndDirection = {
        val sinT = sinTheta(date)
        AngleAndDirection(sinT.asin, d(sinT, sinTheta(date + JD_D)) > 0)
    }

    private def minutesToDays(minutes: Int): Double = minutes.toDouble / 60d / 24d

    /**
      * Determine if
      *
      * @param date        is between
      * @param fromMinutes minutes after the sun passing the
      * @param fromAngle   in
      * @param fromRising  direction
      *                    and
      * @param toMinutes   after the sun passing
      * @param toAngle     in
      * @param toRising    direction
      * @return
      */
    def isBetween(
        fromAngle: Angle,
        fromRising: Boolean,
        fromMinutes: Int,
        toAngle: Angle,
        toRising: Boolean,
        toMinutes: Int,
        date: JulianDate
    ): Boolean = {
        val fromAad = getAngleAndDirection(date - minutesToDays(fromMinutes))
        val toAad = getAngleAndDirection(date - minutesToDays(toMinutes))

        (fromRising, toRising) match {
            case (true, true)   =>
                if (fromAngle <= toAngle) {
                    fromAad.rising && fromAad.angle >= fromAngle &&
                        toAad.rising && toAad.angle <= toAngle
                } else {
                    !(fromAad.rising && fromAad.angle <= fromAngle &&
                        toAad.rising && toAad.angle >= toAngle)
                }
            case (true, false)  =>
                (fromAad.rising && fromAad.angle >= fromAngle) ||
                    (!toAad.rising && toAad.angle >= toAngle)
            case (false, true)  =>
                (!fromAad.rising && fromAad.angle <= fromAngle) ||
                    (toAad.rising && toAad.angle <= toAngle)
            case (false, false) =>
                if (fromAngle >= toAngle) {
                    !fromAad.rising && fromAad.angle <= fromAngle &&
                        !toAad.rising && toAad.angle >= toAngle
                } else {
                    !(!fromAad.rising && fromAad.angle >= fromAngle &&
                        !toAad.rising && toAad.angle <= toAngle)
                }
        }
    }

    //    def isBetween(
    //        fromAngle: Angle,
    //        fromRising: Boolean,
    //        fromMinutes: Int,
    //        toAngle: Angle,
    //        toRising: Boolean,
    //        toMinutes: Int,
    //        date: JulianDate
    //    ): Boolean = {
    //        var jdFrom = new Array[Passing](6)
    //        var jdTo = new Array[Passing](6)
    //
    //        for (i <- 0 until 6) {
    //            jdFrom(i) = nextPassing(fromAngle, fromRising, date + 0.5 * i.toDouble - 1.5)
    //            jdTo(i) = nextPassing(toAngle, toRising, date + 0.5 * i.toDouble - 1.5)
    //        }
    //        jdFrom = cleanArr(jdFrom)
    //        jdTo = cleanArr(jdTo)
    //        if (jdFrom.length == 1 && jdTo.length == 1) {
    //            (jdFrom(0), jdTo(0)) match {
    //                case (Above, Above) => return fromRising && !toRising
    //                case (Above, Below) => return fromRising && toRising
    //                case (Below, Above) => return !fromRising && !toRising
    //                case (Below, Below) => return !fromRising && toRising
    //                case _              => _
    //            }
    //        }
    //
    //        if (jdFrom.length == 1 && jdFrom(0) < JD0) {
    //            jdFrom = new Array[JulianDate](2)
    //            val day: Boolean = fromAngle.inRad > toAngle.inRad
    //            jdFrom(0) = getM(false, day, date)
    //            jdFrom(1) = getM(true, day, date)
    //        }
    //        if (jdTo.length == 1 && jdTo(0) < JD0) {
    //            jdTo = new Array[JulianDate](2)
    //            val day: Boolean = fromAngle.inRad < toAngle.inRad
    //            jdTo(0) = getM(false, day, date)
    //            jdTo(1) = getM(true, day, date)
    //        }
    //        if (fromAngle == toAngle && fromRising == toRising) {
    //            for (i <- 0 until jdFrom.length - 1)
    //                if (if (fromMinutes < toMinutes) jdFrom(i) + DAYS_IN_MINUTE * fromMinutes <= date && jdFrom(i) + DAYS_IN_MINUTE * toMinutes > date
    //                else jdFrom(i) + DAYS_IN_MINUTE * fromMinutes <= date && jdFrom(i + 1) + DAYS_IN_MINUTE * toMinutes > date) return true
    //
    //            return false
    //        }
    //        var fi: Int = 0
    //        var ti: Int = 0
    //        var from: Array[JulianDate] = new Array[JulianDate](0)
    //        var to: Array[JulianDate] = new Array[JulianDate](0)
    //        while (fi < jdFrom.length && ti < jdTo.length) {
    //            if (jdFrom(fi) < jdTo(ti)) {
    //                from +:= jdFrom(fi)
    //                to +:= jdTo(ti)
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
    private val JD_D: Double = 0.0001
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

    sealed abstract class Passing() {
        def isClose(other: Passing): Boolean
        def isSet = false
    }

    case object Above extends Passing {
        override def isClose(other: Passing): Boolean = other match {
            case Above => true
            case _     => false
        }
    }

    case object Below extends Passing {
        override def isClose(other: Passing): Boolean = other match {
            case Below => true
            case _     => false
        }
    }

    case class Passes(julianDate: JulianDate) extends Passing {
        override def isClose(other: Passing): Boolean = other match {
            case Passes(jd) => Math.abs(jd - julianDate) < 0.1
            case _          => false
        }
        override val isSet: Boolean = true
    }

    case object NotSetPassing extends Passing {
        override def isClose(other: Passing): Boolean = false
    }

    case class AngleAndDirection(angle: Angle, rising: Boolean)

}
