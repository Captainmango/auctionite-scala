package utils

import play.api.ApplicationLoader.Context
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}

/** Application loader that makes `.env` values available to application.conf exactly like real environment variables at
  * startup. The heavy lifting lives in [[DotenvConfig]].
  *
  * Tests bypass `play.application.loader` when they use a bare GuiceApplicationBuilder, so they load the application
  * through this loader explicitly — see `DotenvOneAppPerTest`.
  */
class DotenvApplicationLoader extends GuiceApplicationLoader:

  override protected def builder(context: Context): GuiceApplicationBuilder =
    super.builder(
      context.copy(initialConfiguration = DotenvConfig.configuration(context.environment.classLoader))
    )
