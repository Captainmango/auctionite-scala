package common

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting

abstract class AppSpec extends AnyFlatSpec with GuiceOneAppPerTest with Matchers with OptionValues with Injecting {

}
