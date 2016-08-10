package info.ljungqvist.sun

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Tests for [[Sun]]
  *
  * @author Petter Ljungqvist (petter.ljungqvist@houston-inc.com) on 08/02/16.
  */
class SunTest extends WordSpec with MustMatchers {

    "The ecliptic longitude" should {
        "sdf" in {
            
            1d / 2d mustEqual .5d

        }
    }

}
