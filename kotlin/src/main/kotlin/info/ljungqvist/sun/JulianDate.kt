package info.ljungqvist.sun

import java.util.*
import kotlin.math.floor

data class JulianDate(val dayNumber: Double) : Comparable<JulianDate> {

    override fun compareTo(other: JulianDate): Int = dayNumber.compareTo(other.dayNumber)

    fun toDate(): Date = Date(((dayNumber - Y1970.dayNumber) * MILLISECONDS_PER_DAY).toLong())

    val hourPart: Double
            get() = dayNumber - floor(dayNumber)

    val j2000: Double
        get() = dayNumber - Y2000.dayNumber

    operator fun plus(days: Double): JulianDate = jd(dayNumber + days)
    operator fun minus(days: Double): JulianDate = jd(dayNumber - days)
    operator fun minus(jd: JulianDate): Double = dayNumber - jd.dayNumber

    companion object {
        val Y0 = jd(0.0)
        val Y1970 = jd(2440587.5)
        val Y2000 = jd(2451545.0)
    }

}

private fun jd(dayNumber: Double) = JulianDate(dayNumber)
private const val MILLISECONDS_PER_DAY: Double = 24.0 * 60 * 60 * 1000

fun Date.toJulianDate() = JulianDate.Y1970 + this.time.toDouble() / MILLISECONDS_PER_DAY