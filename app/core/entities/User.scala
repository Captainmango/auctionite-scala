package core.entities

case class User(id: Int, email: String)

case class AuthUser(email: String, passwordHash: String)
