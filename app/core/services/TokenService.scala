package core.services

import core.entities.Token
import persistence.TokenRepositoryInterface


trait TokenServiceInterface {
  def save(t: Token): Boolean
}

class TokenService(
    tokenRepo: TokenRepositoryInterface
) extends TokenServiceInterface {
  
  def save(t: Token): Boolean = ???
}