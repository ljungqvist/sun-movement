package info.ljungqvist.sun

import java.util.Date

/**
  * Created on 24/09/15.
  *
  * @author Petter Ljungqvist (petter.ljungqvist@terdelle.com)
  */
class JulianDate(val dayNumber: Double) {

    import JulianDate._

    lazy val toDate = new Date(((dayNumber - JD1970.dayNumber) * MILLISECONDS_PER_DAY).toLong)

    lazy val abs = new JulianDate(Math.abs(dayNumber))

    lazy val j2000: Double = dayNumber - JD2000.dayNumber

    def +(days: Double) = new JulianDate(dayNumber + days)

    def -(days: Double) = new JulianDate(dayNumber - days)

    def -(jd: JulianDate): Double = dayNumber - jd.dayNumber

    def ==(jd: JulianDate): Boolean = equals(jd)

    def >(jd: JulianDate): Boolean = dayNumber > jd.dayNumber

    def <(jd: JulianDate): Boolean = ! >(jd) && ! ==(jd)

    def <=(jd: JulianDate): Boolean = ! >(jd)

    def hourPart: Double = dayNumber - dayNumber.asInstanceOf[Long]

    def canEqual(other: Any): Boolean = other.isInstanceOf[JulianDate]

    override def equals(other: Any): Boolean = other match {
        case that: JulianDate =>
            (that canEqual this) &&
                dayNumber == that.dayNumber
        case _ => false
    }

    override def hashCode(): Int = {
        val state = Seq(dayNumber)
        state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
}

object JulianDate {

    def apply(dayNumber: Double): JulianDate = new JulianDate(dayNumber)

    def apply(date: Date): JulianDate = JD1970 + date.getTime.toDouble / MILLISECONDS_PER_DAY

    def JD(dayNumber: Double): JulianDate = JulianDate(dayNumber)
    def JD(date: Date): JulianDate = JulianDate(date)

    //implicit def cast(double: Double): JulianDate = JulianDate(double)

    //implicit def cast(julianDate: JulianDate): Double = julianDate.dayNumber

    val JD0 = JD(0d)
    val JD1970 = JD(2440587.5d)
    val JD2000 = JD(2451545.0d)
    val MILLISECONDS_PER_DAY: Double = 24d * 60d * 60d * 1000d

}