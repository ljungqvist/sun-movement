package info.ljungqvist.sun

/**
 * Created on 15/10/15.
 * @author Petter Ljungqvist (petter.ljungqvist@houston-inc.com)
 */
object Newton {

    /**
     * Finds the solution to f(x) = y0 using Newton's method
     *
     * @param f function to solve
     * @param x0 starting value
     * @param y0 aim of the function
     * @param d step to take in calculating the derivative
     * @param fractional fractional functions
     * @tparam X the type of x
     * @return the x where f(x) = y0
     */
    def apply[X](f: X => X, x0: X, y0: X, d: X)(implicit fractional: Fractional[X]): X = {
        import fractional._

        var x1 = x0
        var x2 = x0
        do {
            x1 = x2
            val y = f(x1)
            val dy = diff(y, f(x1 + d), d)
            x2 = x1 - (y - y0) / dy
        } while (gt(abs(x1 - x2), d * fromInt(2)))
        x2
    }

    /**
     * Finds the solution to df(x) = dy0 using Newton's method
     *
     * @param f function to solve
     * @param x0 starting value
     * @param dy0 aim of the function
     * @param d step to take in calculating the derivative
     * @param fractional fractional functions
     * @tparam X the type of x
     * @return the x where df(x) = dy0
     */
    def diff[X](f: X => X, x0: X, dy0: X, d: X)(implicit fractional: Fractional[X]): X = {
        import fractional._

        val d2 = d * d
        var x1 = x0
        var x2 = x0
        do {
            x1 = x2
            val yp = f(x1 - d)
            val y = f(x1)
            val yn = f(x1 + d)
            val dy = diff(yp, yn, d)
            val d2y = diff2(yp, y, yn, d2)
            x2 = x1 - (dy - dy0) / d2y
        } while (gt(abs(x1 - x2), d * fromInt(2)))
        x2
    }

    private def diff[X](a: X, b: X, d: X)(implicit fractional: Fractional[X]): X = {
        import fractional._

        (b - a) / d
    }

    private def diff2[X](a: X, b: X, c: X, d2: X)(implicit fractional: Fractional[X]): X = {
        import fractional._

        ((a - fromInt(2)) * (b + c)) / d2
    }

}
