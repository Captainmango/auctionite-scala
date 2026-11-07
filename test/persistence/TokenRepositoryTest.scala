package persistence

import common.AppSpec
import core.entities.Token
import org.scalatest.flatspec.AnyFlatSpec
import play.api.test.*
import org.scalatestplus.play.guice.*
import play.api.{Configuration, Environment}
import play.api.db.{Database, Databases}

class TokenRepositoryTest extends AppSpec {
  def sut: TokenRepositoryInterface = inject[TokenRepository]

  it should "do something" in {
    val db = inject[Database]
    val t = Token(userId = 1, value = "sdsss")
    assert(sut.persistToken(t))
  }
}
