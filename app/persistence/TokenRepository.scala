package persistence

import core.entities.Token
import anorm.*
import com.google.inject.Inject
import play.api.db.*

trait TokenRepositoryInterface {
  def persistToken(t: Token): Boolean
}

class TokenRepository @Inject() (db: Database) extends TokenRepositoryInterface {
  def persistToken(t: Token): Boolean =
    db.withConnection { conn =>
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
        println(rowCount)
        rowCount == 1L
      case None =>
        false
}
