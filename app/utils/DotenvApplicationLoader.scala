package utils

import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.Dotenv
import play.api.ApplicationLoader.Context
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}
import play.api.Configuration

import scala.jdk.CollectionConverters.*

/** Reads `.env` from the working directory and makes its keys available to application.conf exactly like real
  * environment variables, so `host = ${?REDIS_HOST}` resolves from either source.
  *
  * Precedence: system properties / real environment variables > .env > defaults in the config files.
  */
class DotenvApplicationLoader extends GuiceApplicationLoader:

  override protected def builder(context: Context): GuiceApplicationBuilder =
    super.builder(context.copy(initialConfiguration = DotenvApplicationLoader.configuration(context)))

private object DotenvApplicationLoader:

  private def configuration(context: Context): Configuration =
    val dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load()
    // Drop keys the real environment already sets, so it wins over .env.
    val fromFile = dotenv
      .entries()
      .asScala
      .map(e => e.getKey -> e.getValue)
      .toMap
      .removedAll(sys.env.keySet)

    val classLoader = context.environment.classLoader
    Configuration(
      // Same stack as ConfigFactory.load(), with .env slotted in just above
      // the real environment, mimicking env vars for ${?VAR} substitution.
      ConfigFactory
        .systemProperties()
        .withFallback(ConfigFactory.defaultOverrides(classLoader))
        .withFallback(ConfigFactory.defaultApplication(classLoader))
        .withFallback(ConfigFactory.defaultReference(classLoader))
        .withFallback(ConfigFactory.parseMap(fromFile.asJava))
        .withFallback(ConfigFactory.systemEnvironment())
        .resolve()
    )
