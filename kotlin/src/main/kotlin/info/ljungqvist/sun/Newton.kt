package info.ljungqvist.sun

/**
 * Finds the solution to f(x) = y0 using Newton's method
 *
 * @param f  function to solve
 * @param x0 starting value
 * @param y0 aim of the function
 * @param d  step to take in calculating the derivative
 * @return the x where f(x) = y0
 */
fun solveWithNewton(f: (Double) -> Double, y0: Double, x0: Double, d: Double): Double {
    var x1: Double
    var x2 = x0
    do {
        x1 = x2
        val y = f(x1)
        val dy = diff(y, f(x1 + d), d)
        x2 = x1 - (y - y0) / dy
    } while (Math.abs(x1 - x2) > d * 2.0)
    return x2
}

/**
 * Finds the solution to df(x)/dx = dy0 using Newton's method
 *
 * @param f   function to solve
 * @param x0  starting value
 * @param dy0 aim of the function
 * @param d   step to take in calculating the derivative
 * @return the x where df(x) = dy0
 */
fun solveDiffWithNewton(f: (Double) -> Double, dy0: Double, x0: Double, d: Double): Double {
    val d2 = d * d
    var x1: Double
    var x2 = x0
    do {
        x1 = x2
        val yp = f(x1 - d)
        val y = f(x1)
        val yn = f(x1 + d)
        val dy = diff(yp, yn, 2 * d)
        val d2y = diff2(yp, y, yn, d2)
        x2 = x1 - (dy - dy0) / d2y
    } while (Math.abs(x1 - x2) > d * 2.0)
    return x2
}

private fun diff(a: Double, b: Double, d: Double): Double = (b - a) / d
private fun diff2(yp: Double, y: Double, yn: Double, d2: Double): Double = (yp - 2 * y + yn) / d2