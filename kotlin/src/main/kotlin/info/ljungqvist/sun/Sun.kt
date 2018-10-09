package info.ljungqvist.sun

data class Sun(val position: Position) {


    private val v0: Vector = Vector(1.0, 0.0, 0.0).rot(1, -position.lat).rot(2, position.lng)

    fun sinTheta(julianDate: JulianDate): Double {
        val l = eclipticLongitude(julianDate)
        return Vector(1.0, 0.0, 0.0) *
                v0
                        .rot(2, rad(julianDate.hourPart * Angle.RAD_MAX) + l)
                        .rot(1, -axialTilt(julianDate))
                        .rot(2, -l)
    }

    private fun sinThetaNewton(julianDayNumber: Double): Double = sinTheta(JulianDate(julianDayNumber))

    private fun sunPoleAngle(jd: JulianDate): Angle = (axialTilt(jd).sin * (-eclipticLongitude(jd).cos)).acos

    private fun d(a: Double, b: Double): Double = (b - a) / JD_D

    private fun zPi(v: Angle): Angle = if (v.isPositive) v else -v

    /**
     * Calculates when the sun the next time after "date", passes the angle in either rising or setting direction.
     *
     * @param angle  the angle to pass
     * @param rising true for rising, false for setting direction
     * @param date   the date after which the passing should take place
     * @return the next Julian Date a passing will occur
     */
    fun nextPassing(angle: Angle, rising: Boolean, date: JulianDate): Passing {
        var jdTmp = date
        val spAngle: Angle = sunPoleAngle(jdTmp + .25)
        val angleFromNorthPole: Angle = rad(Math.PI / 2.0) - this.position.lat
        val sunMaxAngle: Angle = rad(Math.PI / 2.0) - zPi(spAngle - angleFromNorthPole)
        val sunMinAngle: Angle = rad(Math.PI / 2.0) - zPi(spAngle + angleFromNorthPole)

        if (sunMaxAngle.inRad < angle.inRad) return Passing.Below
        if (sunMinAngle.inRad > angle.inRad) return Passing.Above

        var p: Double = sinTheta(jdTmp)
        if (if (rising) p > angle.sin else p < angle.sin) {
            while (if (rising) p < sinTheta(jdTmp + JD_D) else p > sinTheta(jdTmp + JD_D)) {
                jdTmp += JD_STEP
                p = sinTheta(jdTmp)
            }
            while (if (rising) p > sinTheta(jdTmp - JD_STEP) else p < sinTheta(jdTmp - JD_STEP))
                jdTmp += JD_STEP
        }

        val max_: Double = sunMinAngle.sin + 0.6 * (sunMaxAngle.sin - sunMinAngle.sin)
        val min_: Double = sunMinAngle.sin + 0.4 * (sunMaxAngle.sin - sunMinAngle.sin)
        var p_: Double = sinTheta(jdTmp - JD_STEP)
        while (!(if (rising) p > p_ && p > min_ else p < p_ && p < max_)) {
            jdTmp += JD_STEP
            p_ = p
            p = sinTheta(jdTmp)
        }
        return Passing.Passes(JulianDate(solveWithNewton(angle.sin, jdTmp.dayNumber, JD_D, ::sinThetaNewton)))
    }

    private fun getM(fwd: Boolean, day: Boolean, jd: JulianDate): JulianDate {
        var jdTmp = jd
        val dir: Double = if (fwd) 1.0 else -1.0
        var p: Double = sinTheta(jdTmp)
        var pn: Double = sinTheta(jdTmp + JD_D)
        var dp: Double = d(p, pn)
        while (if (fwd) if (day) dp <= 0.0 else dp >= 0.0 else if (day) dp >= 0.0 else dp <= 0.0) {
            jdTmp += dir * JD_STEP
            p = sinTheta(jdTmp)
            pn = sinTheta(jdTmp + JD_D)
            dp = d(p, pn)
        }
        while (if (fwd) if (day) dp > 0.0 else dp < 0.0 else if (day) dp < 0.0 else dp > 0.0) {
            jdTmp += dir * JD_STEP
            p = sinTheta(jdTmp)
            pn = sinTheta(jdTmp + JD_D)
            dp = d(p, pn)
        }
        return JulianDate(solveDiffWithNewton(0.0, jdTmp.dayNumber, JD_D, ::sinThetaNewton))
    }

    private fun cleanArr(arr: Array<Passing>): Array<Passing> {
        var x: List<Passing> = emptyList()
        var last: Passing = Passing.NotSetPassing
        (0 until 6).forEach { i ->
            if (!arr[i].isClose(last)) x += arr[i]
            last = arr[i]
        }
        return x.toTypedArray()
    }

    private fun getAngleAndDirection(date: JulianDate): AngleAndDirection {
        val sinT = sinTheta(date)
        return AngleAndDirection(sinT.asin, sinT < sinTheta(date + JD_D))
    }

    private fun minutesToDays(minutes: Int): Double = minutes.toDouble() / 60.0 / 24.0

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
    fun isBetween(
            fromAngle: Angle,
            fromRising: Boolean,
            fromMinutes: Int,
            toAngle: Angle,
            toRising: Boolean,
            toMinutes: Int,
            date: JulianDate
    ): Boolean {
        val fromAad = getAngleAndDirection(date - minutesToDays(fromMinutes))
        val toAad = getAngleAndDirection(date - minutesToDays(toMinutes))

        return when (fromRising to toRising) {
            true to true ->
                if (fromAngle <= toAngle) {
                    fromAad.rising && fromAad.angle >= fromAngle &&
                            toAad.rising && toAad.angle <= toAngle
                } else {
                    !(fromAad.rising && fromAad.angle <= fromAngle &&
                            toAad.rising && toAad.angle >= toAngle)
                }
            true to false ->
                (fromAad.rising && fromAad.angle >= fromAngle) ||
                        (!toAad.rising && toAad.angle >= toAngle)
            false to true ->
                (!fromAad.rising && fromAad.angle <= fromAngle) ||
                        (toAad.rising && toAad.angle <= toAngle)
            false to false ->
                if (fromAngle >= toAngle) {
                    !fromAad.rising && fromAad.angle <= fromAngle &&
                            !toAad.rising && toAad.angle >= toAngle
                } else {
                    !(!fromAad.rising && fromAad.angle >= fromAngle &&
                            !toAad.rising && toAad.angle <= toAngle)
                }
            else -> false // not possible
        }
    }

    companion object {
        private fun axialTilt(jd: JulianDate): Angle = deg(23.439) - deg(.0000004) * jd.j2000

        private const val JD_D: Double = 0.0001
        private const val JD_STEP: Double = 0.01
        private const val DAYS_IN_MINUTE: Double = 1.0 / 24.0 / 60.0

        private val DEG_1_195 = deg(1.915)
        private val DEG_0_020 = deg(0.020)
        private const val VAL_0_02906 = 0.0290572732640
        private const val VAL_0_00274 = 0.00273781191135448
        private val DEG_357_528 = deg(357.528)
        private val DEG_0_986 = deg(0.9856003)


        // Ecliptic coordinates

        fun eclipticLongitude(julianDate: JulianDate): Angle {
            val L = meanLongitudeOfTheSun(julianDate)
            val g = meanAnomalyOfTheSun(julianDate)
            return L + DEG_1_195 * g.sin + DEG_0_020 * (g * 2.0).sin
        }

        private fun meanLongitudeOfTheSun(julianDate: JulianDate): Angle =
                rad(2 * Math.PI * (VAL_0_02906 + VAL_0_00274 * julianDate.j2000))

        internal fun meanAnomalyOfTheSun(julianDate: JulianDate): Angle =
                DEG_357_528 + DEG_0_986 * julianDate.j2000


        // Equatorial coordinates

        private fun rightAscension(eclipticLongitude: Angle, tilt: Angle): Angle =
                rad(Math.atan2(tilt.cos * eclipticLongitude.sin, eclipticLongitude.cos))

        private fun declination(eclipticLongitude: Angle, tilt: Angle) =
                (tilt.sin * eclipticLongitude.sin).asin

        fun create(lat: Angle, lng: Angle) = Sun(Position(lat, lng))

        fun fromDeg(lat: Double, lng: Double) = create(deg(lat), deg(lng))

        fun fromRad(lat: Double, lng: Double) = create(rad(lat), rad(lng))

    }

    sealed class Passing {
        abstract fun isClose(other: Passing): Boolean
        open val isSet = false

        object Above : Passing() {
            override fun isClose(other: Passing): Boolean = other is Above
        }

        object Below : Passing() {
            override fun isClose(other: Passing): Boolean = other is Below
        }

        data class Passes(val julianDate: JulianDate) : Passing() {
            override fun isClose(other: Passing): Boolean = when (other) {
                is Passes -> Math.abs(other.julianDate - julianDate) < 0.1
                else -> false
            }

            override val isSet: Boolean = true
        }

        object NotSetPassing : Passing() {
            override fun isClose(other: Passing): Boolean = false
        }

    }


    data class AngleAndDirection(val angle: Angle, val rising: Boolean)

}
