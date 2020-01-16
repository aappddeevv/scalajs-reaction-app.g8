package app

import scala.scalajs.js
import js.annotation._
import js.Dynamic.literal

import react._

object Message {

  @js.native
  @JSImport("JS/Message", JSImport.Namespace)
  object JS extends ReactJsComponent

  def apply(message: String)(children: ReactNode*) =
    createElement(JS, literal("message"-> message))(children:_*)

  // we often define a Props trait and use that instead of a String directly
  /*
  trait Props extends js.Object {
    val message: String
  }
  */
}
