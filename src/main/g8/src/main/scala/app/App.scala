package app

import scala.scalajs.js
import js.annotation.*
import org.scalajs.dom
import react.*
import react.syntax.*
import react.conversions.*
import jshelpers.syntax.*
import vdom.*
import fabric.*
import fabric.components.*

/** This shows importing an existing css page that is processed by your bundler.
 * To understand how it is processed, you need to look at the webpack config file.
 * Generally, the standard style bundlers output an object with a list of keys
 * corresponding to the top level "names."
 */
@js.native
@JSImport("App/app.css", JSImport.Namespace)
private object componentStyles extends js.Object

object styles {
  val estyles = componentStyles.asInstanceOf[js.Dynamic]
}

import styles.*

object Pages:

  val todo = PivotItem(new PivotItem.Props {
    headerText = "To Do"
    itemKey = "todo"
    //className = estyles.scrollme.asString
  })(
    Label("Note: The To Do manager's data is reset each time you switch tabs.".toNode),
    ToDos(new ToDos.Props {
      var title = "Your To Do List"
      var todos = fakedata.initialToDos
    })
  )

  val helloWorld = PivotItem(new PivotItem.Props {
    headerText = "Message"
    itemKey = "message"
    //className = estyles.scrollme.asString
  })(
    Message("hello world")
  )

end Pages

object Main:
  dom.console.log("styles", estyles, js.typeOf(estyles), js.typeOf(estyles.default), estyles.default)
  /**
    * This will be exported from the ES module that scala.js outputs.  How you
    *  access it depends on your bundler. webpack can be configured to output a
    *  "library", say named, "Scala" so you would call this function as
    *  `Scala.App()`.
    */
  @JSExportTopLevel("App")
  def App(): Unit = {
    fabric.icons.initializeIcons()
    react_dom.createRoot("container") match
      case Left(e) => dom.console.log("Error rendering react node", e)
      case Right(thunk) => thunk(Fabric(new Fabric.Props {
            //className = estyles.toplevel.asString
          })(
            Pivot(
              Pages.todo,
              Pages.helloWorld
            )
          ))    
  }
end Main