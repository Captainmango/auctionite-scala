package core.entities

case class Token(value: String, userId: Int) {
  def user(): User = User(1, "dummy")
}