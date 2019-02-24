package app

import scala.scalajs.js
import js.annotation._

import org.scalajs.dom
import ttg.react
import react._
import elements._
import implicits._
import vdom._
import fabric._
import components._

/** This shows importing an existing css page that is processed by your bundler
 * if you want. It's better to use a css-in-js or even a scala-in-js solution.
 * This loads the CSS file using the webpack defined bundler so to understand
 * how it is processed, you need to look at the webpack config file.
 */
@js.native
@JSImport("App/app.css", JSImport.Namespace)
private object componentStyles extends js.Object

object styles {
  val estyles = componentStyles.asInstanceOf[js.Dynamic]
}

import styles._

object Pages {

  val todo = PivotItem(new PivotItem.Props {
    headerText = "To Do"
    itemKey = "todo"
    className = estyles.scrollme.asString
  })(
    Label()("Note: The To Do manager's data is reset each time you switch tabs."),
    ToDos.make(Some("Your To Do List"), fakedata.initialToDos)
  )

  val helloWorld = PivotItem(new PivotItem.Props {
    headerText = "Message"
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
          Fabric(new Fabric.Props {
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
