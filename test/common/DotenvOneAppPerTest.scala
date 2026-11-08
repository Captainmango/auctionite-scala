package common

import org.scalatest.{TestData, TestSuite}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.{Application, ApplicationLoader, Environment}
import utils.DotenvApplicationLoader

/** Builds the test application through [[DotenvApplicationLoader]] so `.env` is honoured exactly like it is at app
  * startup.
  *
  * A bare GuiceApplicationBuilder ignores `play.application.loader`, and the `${?VAR}` substitutions in
  * application.conf are long resolved by the time builder settings could add anything — which is why sbt used to have
  * to inject `.env` into the test JVM as real environment variables. Going through the loader removes the need for
  * that.
  */
trait DotenvOneAppPerTest extends GuiceOneAppPerTest:
  this: TestSuite =>

  /** The loader used to build each test application. Overridable so mixin traits can adjust the application's bindings
    * — see `TransactionalTests`.
    */
  protected def applicationLoader: ApplicationLoader = new DotenvApplicationLoader()

  override def newAppForTest(testData: TestData): Application =
    applicationLoader.load(ApplicationLoader.Context.create(Environment.simple()))
