package core.services

import com.google.inject.Inject
import core.entities.{Token, User}
import persistence.TokenRepositoryInterface

import java.util.UUID

trait TokenServiceInterface {
  def generateTokenFor(user: User): Token
}

class TokenService @Inject() (
    tokenRepo: TokenRepositoryInterface
) extends TokenServiceInterface {

  def generateTokenFor(user: User): Token =
    tokenRepo.persistToken(
      Token(
        value = UUID.randomUUID().toString,
        userId = user.id
      )
    )
}
