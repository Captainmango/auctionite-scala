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

  it should "checks if email is already in user" in {
    val user = sut.registerUser("test@test.com", "password123")
    val userTwo = sut.registerUser("test@test.com", "password123")
    userTwo mustBe None
  }

  it should "be able to log a user in" in {
    val user = sut.registerUser("test@test.com", "password123")

    val tok = sut.login("test@test.com", "password123")
    tok.value.userId mustBe user.value.id
  }

  it should "fail login if user gives wrong password" in {
    val user = sut.registerUser("test@test.com", "password123")

    val tok = sut.login("test@test.com", "password1234")
    tok mustBe None
  }
}
