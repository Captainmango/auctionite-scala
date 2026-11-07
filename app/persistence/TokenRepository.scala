package persistence

import core.entities.Token
import anorm.*
import anorm.Macro.ColumnNaming
import com.google.inject.Inject
import play.api.db.*

trait TokenRepositoryInterface {
  def persistToken(t: Token): Boolean
  def findByValue(v: String): Token
}

val parser: RowParser[Token] = Macro.namedParser[Token](ColumnNaming.SnakeCase)

class TokenRepository @Inject() (db: Database) extends TokenRepositoryInterface {
  def persistToken(t: Token): Boolean =
    db.withTransaction { conn =>
      SQL("""
      INSERT INTO tokens (value, user_id)
      VALUES ({value}, {user_id})
      RETURNING 1
      """)
        .on(
          "value" -> t.value, "user_id" -> t.userId
        )
        .executeInsert()(using conn)
    } match
      case Some(rowCount) =>
        rowCount == 1L
      case None =>
        false

  def findByValue(v: String): core.entities.Token = {
    db.withConnection { conn =>
      SQL("""
      SELECT * FROM tokens where value = {value}
      """).on("value" -> v).as(parser.single)(using conn)
    }
  }
}
