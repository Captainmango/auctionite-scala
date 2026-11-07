package utils

import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.Dotenv
import play.api.Configuration

import scala.jdk.CollectionConverters.*

/** Loads `.env` and weaves it into Typesafe Config resolution so its keys back `${?VAR}` substitutions exactly like
  * real environment variables would.
  *
  * This is the single place `.env` is read in application/test code, used both by [[DotenvApplicationLoader]] (app
  * startup) and by tests (which build the application through that same loader — see the `DotenvOneAppPerTest` trait).
  *
  * Precedence: system properties / real environment variables > .env > config file defaults.
  */
object DotenvConfig:

  private lazy val fromFile: Map[String, String] =
    Dotenv
      .configure()
      .ignoreIfMissing()
      .ignoreIfMalformed()
      .load()
      .entries()
      .asScala
      .map(e => e.getKey -> e.getValue)
      .toMap
      // Keys the real environment already sets stay out of the fallback, so it wins over .env.
      .removedAll(sys.env.keySet)

  def configuration(classLoader: ClassLoader): Configuration =
    Configuration(
      // Same stack as ConfigFactory.load(), with .env slotted in just above
      // the real environment, resolved as a whole so ${?VAR} substitution sees it.
      ConfigFactory
        .systemProperties()
        .withFallback(ConfigFactory.defaultOverrides(classLoader))
        .withFallback(ConfigFactory.defaultApplication(classLoader))
        .withFallback(ConfigFactory.defaultReference(classLoader))
        .withFallback(ConfigFactory.parseMap(fromFile.asJava))
        .withFallback(ConfigFactory.systemEnvironment())
        .resolve()
    )
