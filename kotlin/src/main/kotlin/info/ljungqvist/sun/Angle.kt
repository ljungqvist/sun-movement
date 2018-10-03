package info.ljungqvist.sun

data class Angle internal constructor(val angle: Double) : Comparable<Angle> {

    val inRad = angle
    val inDeg = angle * DEG_MAX / RAD_MAX

    var internalSin: Double? = null
    var internalCos: Double? = null

    val sin: Double
        get() = internalSin
                ?: run {
                    internalCos?.let { cos ->
                        Math.sqrt((1 + cos) * (1 - cos)) * (if (isPositive) 1 else -1)
                    }
                            ?: Math.sin(angle)
                }.also { internalSin = it }

    val cos: Double
        get() = internalCos
                ?: run {
                    internalSin?.let { sin ->
                        Math.sqrt((1 + sin) * (1 - sin)) * (if (angle > -Math.PI / 2 && angle <= Math.PI / 2) 1 else -1)
                    }
                            ?: Math.sin(angle)
                }.also { internalCos = it }

    val tan: Double by lazy {
        if (0.0 == cos) {
            if (sin < 0.0) Double.NEGATIVE_INFINITY
            else Double.POSITIVE_INFINITY
        } else sin / cos
    }

    val isPositive: Boolean
        get() = angle >= 0.0

    operator fun unaryMinus() = rad(-angle)
    operator fun plus(other: Angle) = rad(angle + other.angle)
    operator fun minus(other: Angle) = rad(angle - other.angle)
    operator fun times(double: Double) = rad(angle * double)
    operator fun div(double: Double) = rad(angle / double)

    override fun compareTo(other: Angle): Int = angle.compareTo(other.angle)

    companion object {
        const val RAD_MAX: Double = Math.PI * 2.0
        const val DEG_MAX: Double = 360.0
    }
}

@Suppress("ReplaceSingleLineLet")
fun rad(angle: Double) = ((angle + Math.PI) % (Math.PI * 2.0))
        .buildOnIf({ it < 0 }) { this + Math.PI * 2.0 }
        .let { it - Math.PI }
        .let(::Angle)

fun deg(angle: Double) = rad(angle / Angle.DEG_MAX * Angle.RAD_MAX)

val Double.asin: Angle
    get() = let(Math::asin).let(::rad)
val Double.acos: Angle
    get() = let(Math::acos).let(::rad)
val Double.atan: Angle
    get() = let(Math::atan).let(::rad)
