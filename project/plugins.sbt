addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.11")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.18.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.13.0")
addSbtPlugin("com.github.sbt" % "flyway-sbt" % "12.0.0")
// Needed so that the plugin can resolve psql driver when sbt runs it.
// App level stuff doesn't care about flyway
libraryDependencies += "org.flywaydb" % "flyway-database-postgresql" % "13.12.1"
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.14"
