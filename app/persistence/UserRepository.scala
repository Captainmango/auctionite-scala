package persistence

import com.google.inject.Inject
import play.api.db.Database
import anorm.*
import anorm.Macro.ColumnNaming
import core.entities.{AuthUser, User}

import java.sql.Connection


trait UserRepositoryInterface {
  def persist(u: AuthUser): Option[User]
  def findByEmailWithPassword(email: String): Option[AuthUser]
  def findByEmail(email: String): Option[User]
}

val userParser = Macro.namedParser[User](ColumnNaming.SnakeCase)
val authUserParser = Macro.namedParser[AuthUser](ColumnNaming.SnakeCase)

class UserRepository @Inject() (
    db: Database
) extends UserRepositoryInterface {
  def persist(u: AuthUser): Option[User] = {
    db.withTransaction { implicit conn =>
      SQL("""
      INSERT INTO users (email, password_hash)
      VALUES ({email}, {passwordHash})
      ON CONFLICT (email) DO NOTHING
      RETURNING id, email
      """)
        .on("email" -> u.email, "passwordHash" -> u.passwordHash)
        .as(userParser.singleOpt)
    }
  }
  
  def findByEmailWithPassword(email: String): Option[AuthUser] = {
    db.withConnection { implicit conn =>
      SQL("""
      SELECT email, password_hash FROM users
      WHERE email = {email}
      """)
        .on("email" -> email)
        .as(authUserParser.singleOpt)
    }
  }

  def findByEmail(email: String): Option[User] = {
    db.withConnection { implicit conn =>
      SQL("""
      SELECT id, email FROM users
      WHERE email = {email}
      """)
        .on("email" -> email)
        .as(userParser.singleOpt)
    }
  }
}