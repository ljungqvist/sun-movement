package info.ljungqvist.sun

/**
 * Created on 14/10/15.
 *
 * @author Petter Ljungqvist (petter.ljungqvist@tredelle.com)
 */
class Vector(x0: Double, x1: Double, x2: Double) {

    private val v = Array(x0, x1, x2)

    def apply(i: Int) = v(i)

    def x = v(0)

    def y = v(1)

    def z = v(2)

    def *(other: Vector): Double = {
        var res: Double = 0d
        for (i <- 0 until 3)
            res += this.v(i) * other.v(i)
        res
    }

    def rot(axis: Int, angle: Angle): Vector = {
        val vector = Vector()
        val a: Int = (axis + 1) % 3
        val b: Int = (axis + 2) % 3
        vector.v(axis) = v(axis)
        vector.v(a) = angle.cos * v(a) - angle.sin * v(b)
        vector.v(b) = angle.sin * v(a) + angle.cos * v(b)
        vector
    }

}

object Vector {
    def apply() = new Vector(0d, 0d, 0d)

    def apply(x0: Double, x1: Double, x2: Double) = new Vector(x0, x1, x2)

    def apply(v: Array[Double]) = new Vector(v(0), v(1), v(2))
}
