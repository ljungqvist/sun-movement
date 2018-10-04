package info.ljungqvist.sun

import org.junit.Assert
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@RunWith(JUnitPlatform::class)
object NewtonTest : Spek({

    val d = 0.00001
    val d2: Double = d / 2.0


    describe("Newtons method") {

        context("3x + 2 = 5") {
            val f = { x: Double -> 3 * x + 2 }
            it("yields x = 1") {
                Assert.assertEquals(1.0, solveWithNewton(5.0, 0.0, d, f), d2)
            }
        }

        context("3x + 2 = 122") {
            val f = { x: Double -> 3 * x + 2 }
            it("yields x = 40") {
                Assert.assertEquals(40.0, solveWithNewton(122.0, 0.0, d, f), d2)
            }
        }

        context("x^2 + 2x = 3.41") {
            val f = { x: Double -> x * x + 2 * x }
            it("yields x = 1.1 when starting from 0") {
                Assert.assertEquals(1.1, solveWithNewton(3.41, 0.0, d, f), d2)
            }
            it("yields x = -3.1 when starting from -10") {
                Assert.assertEquals(-3.1, solveWithNewton(3.41, -10.0, d, f), d2)
            }
        }

        context("x^3 + 2x^2 - 4x = 4.5") {
            val f = { x: Double -> x * x * x + 2 * x * x - 4 * x }
            it("yields x = -2.84954 when starting from -4") {
                Assert.assertEquals(-2.84954, solveWithNewton(4.5, -4.0, d, f), d2)
            }
            it("yields x = -0.901741 when starting from 0") {
                Assert.assertEquals(-0.901741, solveWithNewton(4.5, 0.0, d, f), d2)
            }
            it("yields x = 1.75128 when starting from 1") {
                Assert.assertEquals(1.75128, solveWithNewton(4.5, 1.0, d, f), d2)
            }
        }

    }

    describe("Newtons method for differentials") {

        context("d(x^2 + 2x)/dx = 6") {
            val f = { x: Double -> x * x + 2 * x }
            it("yields x = 2 when starting from 0") {
                Assert.assertEquals(2.0, solveDiffWithNewton(6.0, 0.0, d, f), d2)
            }
        }

        context("d(x^2 + 2x)/dx = 0") {
            val f = { x: Double -> x * x + 2 * x }
            it("yields x = -1 when starting from 0") {
                Assert.assertEquals(-1.0, solveDiffWithNewton(0.0, 0.0, d, f), d2)
            }
        }

    }

})