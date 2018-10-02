@file:Suppress("FunctionName")

package info.ljungqvist.sun

data class Vector(private val v: List<Double>) {

    init {
        if (v.size != 3) throw IllegalArgumentException("v mus have three dimensions")
    }

    val x: Double
        get() = v[0]
    val y: Double
        get() = v[1]
    val z: Double
        get() = v[2]

    operator fun get(i: Int) = v[i]

    operator fun times(other: Vector) = v.zip(other.v).map { (a, b) -> a * b }.sum()

    fun rot(axis: Int, angle: Angle): Vector {
        val a: Int = (axis + 1) % 3
        val b: Int = (axis + 2) % 3
        val vector = DoubleArray(3)
        vector[axis] = v[axis]
        vector[a] = angle.cos * v[a] - angle.sin * v[b]
        vector[b] = angle.sin * v[a] + angle.cos * v[b]
        return Vector(vector.toList())
    }

}

fun Vector(x0: Double, x1: Double, x2: Double) = Vector(listOf(x0, x1, x2))
fun Vector() = Vector(0.0, 0.0, 0.0)
