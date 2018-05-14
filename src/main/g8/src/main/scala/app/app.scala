package app

import scala.scalajs.js
import js.annotation._
import js.JSConverters._
import js.Dynamic.{literal => lit}

import org.scalajs.dom
import ttg.react._
import ttg.react.elements._
import ttg.react.implicits._
import vdom._
import ttg.react.fabric
import fabric._
import fabric.components._
import fabric.styling._
import Styling._

/** This shows importing an existing css page that is processed by your bundler
 * if you want. It's better to use a css-in-js or even a scala-in-js solution.
 */
@js.native
@JSImport("App/app.css", JSImport.Namespace)
private object componentStyles extends js.Object

object styles {
  val estyles = componentStyles.asInstanceOf[js.Dynamic]
}

import styles._

object Pages {

  val todo = PivotItem(new IPivotItemProps {
    linkText = "To Do"
    itemKey = "todo"
    className = estyles.scrollme.asString
  })(
    Label()("Note: The To Do manager's data is reset each time you switch tabs."),
    ToDos.make(Some("Your To Do List"), fakedata.initialToDos)
  )

  val helloWorld = PivotItem(new IPivotItemProps {
    linkText = "Message"
    itemKey = "message"
    className = estyles.scrollme.asString
  })(
    Message.make("hello world")()
  )

}

object Main {
  /**
    * This will be exported from the ES module that scala.js outputs.  How you
    *  access it depends on your bundler. webpack can be configured to output a
    *  "library", say named, "Scala" so you would call this function as
    *  `Scala.App()`.
    */
  @JSExportTopLevel("App")
  def App(): Unit = {
    uifabric_icons.initializeIcons()
    reactdom.createAndRenderWithId(
          Fabric(new IFabricProps {
            className = estyles.toplevel.asString
          })(
            Pivot()(
              Pages.todo,
              Pages.helloWorld
            )
          ),
      "container"
    )
  }
}
