package app

import scala.scalajs.js
import js.annotation._
import js.Dynamic.literal

import react._

object Message {

  @js.native
  @JSImport("JS/Message", "Message")
  object JS extends ReactJSComponent

  def apply(message: String) =
    createElement(JS, literal("message"-> message))

  // we often define a Props trait and instead of a String directly
  /*
  trait Props extends js.Object {
    val message: String
  }
  */
}
