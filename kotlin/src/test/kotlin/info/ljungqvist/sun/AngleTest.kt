package info.ljungqvist.sun

import org.junit.Assert
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@RunWith(JUnitPlatform::class)
object AngleTest : Spek({

    describe("radians") {

        it("in interval [-PI, PI)") {

            Assert.assertEquals(rad(2.0), rad(2.0))
            Assert.assertEquals(rad(1.0 + Angle.RAD_MAX), rad(1.0))
            Assert.assertEquals(rad(1.0 + 2.0 * Angle.RAD_MAX), rad(1.0))
            Assert.assertEquals(rad(1.0 - Angle.RAD_MAX), rad(1.0))
            Assert.assertEquals(rad(1.0).angle, 1.0, 0.0)
            Assert.assertEquals(rad(-1.0).angle, -1.0, 0.0)
            Assert.assertEquals(rad(4.0).angle, 4.0 - Angle.RAD_MAX, 0.0)
            Assert.assertEquals(rad(-4.0).angle, -4.0 + Angle.RAD_MAX, 0.0)

        }

        it("add to degrees") {
            Assert.assertEquals(rad(1.0) + deg(180.0), rad(1 + Angle.RAD_MAX / 2.0))
        }

        it("equallity with degrees") {
            Assert.assertEquals(rad(0.0), deg(0.0))
            Assert.assertEquals(rad(Math.PI / 2.0), deg(90.0))
        }

        it("addition" ) {
            Assert.assertEquals(rad(Math.PI / 2.0) + rad(Math.PI / 2.0), rad(Math.PI))
            Assert.assertEquals(rad(Math.PI / 2.0) + rad(Math.PI / 2.0) + deg(90.0), deg(-90.0))
        }

        it("multiplication") {
            Assert.assertEquals(rad(1.0) * 1.7, rad(1.7))
        }

        val d = 0.0000000001
        it("trigonometry") {
            var a = 0.0
            val step = 0.001
            while (a < 2 * Math.PI) {
                val ang1 = rad(a)
                val tan = Math.tan(a)
                val dTan = Math.abs(tan * Math.sqrt(d) + d)
                Assert.assertEquals("a is $a", ang1.sin, Math.sin(a), d)
                Assert.assertEquals("a is $a", ang1.cos, Math.cos(a), d)
                Assert.assertEquals("a is $a", ang1.tan, tan, dTan)
                val ang2 = rad(a)
                Assert.assertEquals("a is $a", ang2.sin, Math.sin(a), d)
                Assert.assertEquals("a is $a", ang2.cos, Math.cos(a), d)
                Assert.assertEquals("a is $a", ang2.tan, tan, dTan)
                a += step
            }
        }

    }

})