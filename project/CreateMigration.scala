import sbt.*
import sbt.Keys.*

object CreateMigration {
  val createMigration = inputKey[File]("Create an empty Flyway migration: newMigration <description>")

  val settings: Seq[Setting[?]] = Seq(
    createMigration := {
      val args = Def.spaceDelimited("<description>").parsed
      if (args.isEmpty) sys.error("usage: newMigration <description>")

      val dir = (Compile / resourceDirectory).value / "db" / "migration"

      val slug = args.mkString("_").toLowerCase.replaceAll("[^a-z0-9]+", "_").stripPrefix("_").stripSuffix("_")
      val existing = Option(dir.listFiles()).toSeq.flatten
        .flatMap(f => """^V(\d+)__""".r.findFirstMatchIn(f.getName).map(_.group(1).toLong))

      val version = (if (existing.isEmpty) 0L else existing.max) + 1

      val file = dir / s"V${version}__$slug.sql"
      IO.touch(file)
      streams.value.log.info(s"Created ${file.relativeTo(baseDirectory.value).getOrElse(file)}")
      file
    }
  )
}