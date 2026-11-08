import com.google.inject.AbstractModule
import core.services.{AuthService, AuthServiceInterface, TokenService, TokenServiceInterface}
import persistence.{TokenRepository, TokenRepositoryInterface, UserRepository, UserRepositoryInterface}

class Module extends AbstractModule {
  override def configure(): Unit = {
    super.configure()
    bind(classOf[UserRepositoryInterface]).to(classOf[UserRepository])
    bind(classOf[TokenRepositoryInterface]).to(classOf[TokenRepository])
    bind(classOf[TokenServiceInterface]).to(classOf[TokenService])
    bind(classOf[AuthServiceInterface]).to(classOf[AuthService])
  }
}
