package ports.controllers

import play.api.*
import play.api.mvc.*
import com.google.inject.Inject
import core.services.AuthServiceInterface
import play.api.mvc.{BaseController, ControllerComponents}
import ports.requests.{RegisterUserRequest, registerReader}
import core.entities.userWriter
import play.api.libs.json.{JsError, JsValue, Json}

class AuthController @Inject() (
    val controllerComponents: ControllerComponents,
    authService: AuthServiceInterface
) extends BaseController {
  def registerUser(): Action[JsValue] = Action(parse.json) { req =>
    val registerRequest = req.body.validate[RegisterUserRequest]
    registerRequest.fold(
      errors => {
        UnprocessableEntity(Json.obj("message" -> JsError.toJson(errors)))
      },
      reg => {
        authService.registerUser(reg.email, reg.password) match {
          case Some(u) =>
            Ok(Json.toJson(u))
          case None =>
            UnprocessableEntity(Json.obj("message" -> "Email already in use"))
        }
      }
    )
  }
}
