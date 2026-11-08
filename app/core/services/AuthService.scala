package core.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.inject.Inject
import core.entities.{AuthUser, Token, User}
import persistence.UserRepositoryInterface
import play.api.Configuration

trait AuthServiceInterface {
  def registerUser(email: String, password: String): Option[User]
  def login(email: String, password: String): Option[Token]
}

class AuthService @Inject() (
    tokenService: TokenServiceInterface,
    userRepo: UserRepositoryInterface,
    config: Configuration
) extends AuthServiceInterface {

  def registerUser(email: String, password: String): Option[User] = {
    val hashedPassword = BCrypt
      .withDefaults()
      .hashToChar(config.get[Int]("auth.bcrypt-rounds"), password.toCharArray)
      .mkString("")

    userRepo.persist(AuthUser(email, hashedPassword))
  }

  def login(email: String, password: String): Option[Token] = {
    for {
      aU <- userRepo.findByEmailWithPassword(email)
      u <- this.verifyPassword(password, aU)
      t <- Some(tokenService.generateTokenFor(u))
    } yield t
  }

  private def verifyPassword(password: String, au: AuthUser): Option[User] = {
    if (BCrypt.verifyer().verify(password.toCharArray, au.passwordHash.toCharArray).verified) {
      userRepo.findByEmail(au.email)
    } else {
      None
    }
  }
}
