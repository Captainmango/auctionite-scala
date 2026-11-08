package core.services

import common.{AppSpec, TransactionalTests}
import persistence.UserRepositoryInterface

class AuthServiceTest extends AppSpec with TransactionalTests {
  def sut: AuthServiceInterface = inject[AuthService]

  it should "Can create a user and hashes password" in {
    val user = sut.registerUser("test@test.com", "password123")
    user.value.email mustBe "test@test.com"
    val userRepo = inject[UserRepositoryInterface]
    val userWithPassword = userRepo.findByEmailWithPassword(user.value.email)
    userWithPassword.value.passwordHash must not equal "password123"
  }
}
