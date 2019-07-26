package app

import scala.scalajs.js
import js.annotation._

import ttg.react._
import ttg.react.elements._

/** The import here imports the entire namespace but you could just import the
 * one comoponent directly.
 */
@js.native
@JSImport("JS/Message", JSImport.Namespace)
object MessageNS extends js.Object {
  val Message: ReactJsComponent = js.native
}

object Message {

  def apply(message: String)(children: ReactNode*) =
    React.createElement(MessageNS.Message, js.Dynamic.literal("message"-> message))(children:_*)
}
