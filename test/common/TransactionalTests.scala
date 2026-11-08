package common

import org.scalatest.{TestData, TestSuite}
import play.api.db.Database
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{ApplicationLifecycle, bind}
import play.api.{Application, ApplicationLoader}
import utils.DotenvApplicationLoader

import scala.concurrent.Future
import scala.util.Try

/** Mixin for specs whose tests write to the database: every test runs inside one transaction that is rolled back when
  * the test ends, so nothing it wrote survives into the next test or the next run.
  *
  * Works by overriding the injected [[Database]] with [[TransactionalDatabase]]: every connection the app under test
  * obtains is the same auto-commit-off connection, so repository code joins the test-wide transaction instead of
  * committing its own. The rollback is registered as an application stop hook; stop hooks run last-registered first, so
  * it executes while the pool owning the connection is still alive.
  *
  * Caveats:
  *   - Only the unqualified default `Database` binding is overridden; code injecting `@NamedDatabase("...") Database`
  *     or using `db.dataSource` directly bypasses the transaction.
  *   - `withTransaction` in code under test joins the same transaction — its commit is discarded along with everything
  *     else.
  *
  * Usage: `class TokenRepositoryTest extends AppSpec with TransactionalTests`.
  */
trait TransactionalTests extends DotenvOneAppPerTest:
  this: TestSuite =>

  override protected def applicationLoader: ApplicationLoader = new DotenvApplicationLoader:
    override protected def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder =
      super
        .builder(context)
        .overrides(bind[Database].to[TransactionalDatabase])

  override def newAppForTest(testData: TestData): Application =
    val built = super.newAppForTest(testData)
    // Resolving the wrapper now opens the test's transaction before the test body runs.
    val db = built.injector.instanceOf[TransactionalDatabase]
    // Runs when the app stops, ahead of DBApi's own stop hook that shuts the pool down.
    built.injector.instanceOf[ApplicationLifecycle].addStopHook { () =>
      Try(db.rollback()).failed
        .foreach(e => println(s"TransactionalTests: rollback failed for '${testData.name}': $e"))
      Future.successful(())
    }
    built
