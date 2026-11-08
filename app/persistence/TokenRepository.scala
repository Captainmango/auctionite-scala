package persistence

import anorm.*
import anorm.Macro.ColumnNaming
import com.google.inject.Inject
import core.entities.Token
import play.api.db.*

trait TokenRepositoryInterface {
  def persistToken(t: Token): Token
  def findByValue(v: String): Token
}

val parser: RowParser[Token] = Macro.namedParser[Token](ColumnNaming.SnakeCase)

class TokenRepository @Inject() (db: Database) extends TokenRepositoryInterface {
  def persistToken(t: Token): Token =
    db.withTransaction { implicit conn =>
      SQL("""
      INSERT INTO tokens (value, user_id)
      VALUES ({value}, {user_id})
      RETURNING value, user_id
      """)
        .on(
          "value" -> t.value, "user_id" -> t.userId
        )
        .as(parser.single)
    }

  def findByValue(v: String): Token = {
    db.withConnection { implicit conn =>
      SQL("""
      SELECT * FROM tokens where value = {value}
      """).on("value" -> v).as(parser.single)
    }
  }
}
