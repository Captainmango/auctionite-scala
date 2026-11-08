package ports.requests

import play.api.libs.json.{Json, Reads}

case class RegisterUserRequest(email: String, password: String)

given registerReader: Reads[RegisterUserRequest] = Json.reads[RegisterUserRequest]