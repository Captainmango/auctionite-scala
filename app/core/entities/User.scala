package core.entities

import play.api.libs.json.{Json, Writes}

case class User(id: Int, email: String)

case class AuthUser(email: String, passwordHash: String)

given userWriter: Writes[User] = Json.writes[User]