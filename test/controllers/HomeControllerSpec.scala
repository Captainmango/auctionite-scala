package controllers

import common.DotenvOneAppPerTest
import org.scalatestplus.play._
import play.api.cache.SyncCacheApi
import play.api.test.Helpers._
import play.api.test._
import ports.controllers.HomeController

/** Add your spec here. You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class HomeControllerSpec extends PlaySpec with DotenvOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      val e = inject[SyncCacheApi]

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      true mustBe true
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      true mustBe true
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      true mustBe true
    }
  }
}
