name := """auctionite-scala"""
organization := "cloud.eheaver"

version := "1.0-SNAPSHOT"
scalaVersion := "3.9.0"
semanticdbEnabled := true
semanticdbVersion := scalafixSemanticdb.revision
scalacOptions += "-Wunused:imports"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, FlywayPlugin)
  .settings(CreateMigration.settings)

libraryDependencies += guice
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.14"
libraryDependencies += "org.flywaydb" % "flyway-database-postgresql" % "13.12.1"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test

// Database connection helpers — defaults match .env.example / docker-compose.yml;
// real environment variables take precedence.
def dbEnv(key: String, default: String): String =
  sys.env.getOrElse(key, default)

def databaseUrl(name: String = dbEnv("DATABASE_NAME", "auctionite_dev")): String =
  s"jdbc:postgresql://${dbEnv("DATABASE_HOST", "localhost")}:${dbEnv("DATABASE_PORT", "5432")}/$name"

// Flyway
flywayUrl := databaseUrl()
flywayUser := dbEnv("DATABASE_USER", "auctionite")
flywayPassword := dbEnv("DATABASE_PASSWORD", "auctionite")
flywayLocations := Seq("filesystem:conf/db/migration")

Test / flywayUrl := databaseUrl(name=dbEnv("TEST_DATABASE_NAME", "auctionite_test"))
Test / flywayUser := dbEnv("DATABASE_USER", "auctionite")
Test / flywayPassword := dbEnv("DATABASE_PASSWORD", "auctionite")
Test / flywayLocations := Seq("filesystem:conf/db/migration")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "cloud.eheaver.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "cloud.eheaver.binders._"
