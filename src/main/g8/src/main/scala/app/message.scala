package app

import scala.scalajs.js
import js.annotation._

import ttg.react._
import ttg.react.elements._

@js.native
@JSImport("JS/Message", JSImport.Namespace)
object MessageNS extends js.Object {
  val Message: ReactJsComponent = js.native
}

object Message {

  def make(message: String)(children: ReactNode*) =
    wrapJsForScala(MessageNS.Message, js.Dynamic.literal("message"-> message), children:_*)
}
