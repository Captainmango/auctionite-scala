package persistence

import core.entities.Token
import anorm.*
import com.google.inject.Inject
import play.api.db.*

trait TokenRepositoryInterface {
  def persistToken(t: Token): Boolean
}

class TokenRepository @Inject() (db: Database) extends TokenRepositoryInterface {
  def persistToken(t: Token): Boolean = {
    db.withConnection { conn =>
      SQL"""select 1;""".execute()(using conn)
    }
  }
}
