package info.ljungqvist.sun

import scala.language.implicitConversions

/**
  * Created on 02/09/15.
  *
  * @author Petter Ljungqvist (petter.ljungqvist@terdelle.com)
  */
class Angle(angle: Double) {

    private val max: Double = Angle.RAD_MAX / 2d
    private val min: Double = max - Angle.RAD_MAX

    private val a = {
        var a = angle
        while (a < min) a += max - min
        while (a >= max) a -= max - min
        a
    }

    lazy val sin: Double = Math.sin(a)
    lazy val cos: Double = Math.cos(a)
    lazy val tan: Double = Math.tan(a)

    def unary_- = new Angle(-a)

    def +(other: Angle) = new Angle(a + other.a)

    def -(other: Angle) = new Angle(a - other.a)

    def *(d: Double) = new Angle(a * d)

    def /(d: Double) = new Angle(a / d)

    def ==(other: Angle): Boolean = equals(other)

    def >(other: Angle): Boolean = a > other.a
    def >=(other: Angle): Boolean = a >= other.a
    def <(other: Angle): Boolean = a < other.a
    def <=(other: Angle): Boolean = a <= other.a

    def isPositive: Boolean = a >= 0d

    def inRad: Double = a

    def inDeg: Double = a * Angle.DEG_MAX / Angle.RAD_MAX

    override def toString: String = "Rad[" + a + "]"

    override def equals(o: scala.Any): Boolean = o match {
        case angle: Angle => a == angle.a
        case _            => false
    }

    override def hashCode: Int = a.hashCode
}

object Rad {
    def apply(angle: Double) = new Angle(angle)
}

object Deg {
    def apply(angle: Double) = new Angle(angle * Angle.RAD_MAX / Angle.DEG_MAX)
}

object Angle {
    val RAD_MAX: Double = Math.PI * 2d
    val DEG_MAX = 360d

    implicit def castDoubleToTrigonometryDouble(d: Double): TrigonometryDouble = new TrigonometryDouble(d)

}

class TrigonometryDouble(val double: Double) {

    lazy val asin: Angle = Rad(Math.asin(double))
    lazy val acos: Angle = Rad(Math.acos(double))
    lazy val atan: Angle = Rad(Math.atan(double))

}
