package persistence

import common.{AppSpec, TransactionalTests}
import core.entities.Token

class TokenRepositoryTest extends AppSpec with TransactionalTests {
  def sut: TokenRepositoryInterface = inject[TokenRepository]

  it should "do something" in {
    val t = Token(userId = 1, value = "test-token-value")
    val actualToken = sut.persistToken(t)
    actualToken.value mustBe t.value
  }
}
