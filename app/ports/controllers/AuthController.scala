package ports.controllers

import com.google.inject.Inject
import core.entities.userWriter
import core.services.AuthServiceInterface
import play.api._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{BaseController, ControllerComponents, _}
import ports.requests.{RegisterUserRequest, registerReader}

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
