package app

import scala.scalajs.js
import js.|
import js.annotation._
import js.Dynamic.{literal => lit, global => g}
import js.JSConverters._

import org.scalajs.dom
import ttg.react._
import ttg.react.elements._
import ttg.react.reactdom._
import ttg.react.implicits._
import ttg.react.fabric
import fabric._
import fabric.components._
import fabric.styling._

import vdom._
import vdom.tags._

/**
 * Use the fabric css-in-js styling patterns. They are much like glamor in
 * spirit. We use `getClassNames` directly in some of the components even though
 * you normally would pass the props down throuh the component hierarchy.
 */
object ToDoStyling {

  @js.native
  trait ToDoClassNames extends js.Object {
    var root: String      = js.native
    var todo: String      = js.native
    var title: String     = js.native
    var dataEntry: String = js.native
  }

  private def _getClassNames(randomArg: Int = 400) =
    Styling.mergeStyleSets[ToDoClassNames](
      styleset(
        "root" -> new IRawStyle {
          selectors = selectorset(
            // totally not needed but demonstrates css vars
            ":global(:root)" -> lit(
              "--label-width" -> s"${randomArg}px",
            ))
        },
        "todo" -> new IRawStyle {
          displayName = "machina"
          display = "flex"
          marginBottom = "10px"
          selectors = selectorset("& $title" -> new IRawStyle {})
        },
        "title" -> new IRawStyle {
          width = "var(--label-width)"
          marginRight = "10px"
        },
        "dataEntry" -> new IRawStyle {
          display = "flex"
          selectors = selectorset("& .ms-Textfield" -> new IRawStyle {
            width = "var(--label-width)"
            marginRight = "10px"
          })
        }
      ))

  // example of memoizing, you need a js.Function to use memoizeFunction
  val getClassNames = fabric.Utilities.memoizeFunction(js.Any.fromFunction1(_getClassNames))
}

case class ToDo(id: Int, name: String, added: js.Date = null)

object ToDo {
  val c = statelessComponent("ToDoItem")
  import c.ops._

  def make(todo: ToDo, remove: Unit => Unit) =
    render { self =>
      val cn = ToDoStyling.getClassNames(500)
      div(new DivProps { className = cn.todo })(
        Label(new ILabelProps {
          className = cn.title
        })(
          todo.name
        ),
        DefaultButton(new IButtonProps {
          text = "Remove"
          onClick = js.defined(_ => remove(()))
        })()
      )
    }
}

/** Show a header above the todo items. */
object ToDoListHeader {
  val c = statelessComponent("ToDoListHeader")
  import c.ops._

  def make(length: Int) =
    render { self =>
      div(Label()(s"# To Dos - ${length}"))
    }
}

/** Show a list. We use a retained prop, but its not needed. */
object ToDoList {
  val c = statelessComponentWithRetainedProps[Int]("ToDoList")
  import c.ops._

  def make(length: Int, todos: Seq[ToDo], remove: Int => Unit, todoClassName: Option[String] = None) =
    c.copy(new methods {
      val retainedProps = length
      val render = self => {
        div(
          ToDoListHeader.make(length),
          arrayToElement(
            todos.map(t => element(ToDo.make(t, _ => remove(t.id)), key = Some(t.id.toString()))))
        )
      }
    })
}

object ToDos {

  sealed trait ToDoAction
  case class Add(todo: ToDo)                     extends ToDoAction
  case class Remove(id: Int)                     extends ToDoAction
  case class InputChanged(input: Option[String]) extends ToDoAction
  case class Complete(id: Int) extends ToDoAction

  var idCounter: Int = -1
  def mkId(): Int    = { idCounter = idCounter + 1; idCounter }

  case class State(
      todos: Seq[ToDo] = Seq(),
      input: Option[String] = None,
      var textFieldRef: Option[ITextField] = None)

  case class RP(title: Option[String] = None)
  val c = reducerComponentWithRetainedProps[State, RP, ToDoAction]("ToDos")
  import c.ops._

  def remove(id: Int)(self: c.Self): Unit = self.send(Remove(id))
  def inputChanged(e: Option[String])(self: c.Self): Unit =
    self.send(InputChanged(e))

  def addit(self: Self) =
    self.state.input.foreach { i =>
      self.handle { s =>
        s.send(Add(ToDo(mkId(), i)))
        s.state.textFieldRef.foreach(ref => refToJs(ref).focus())
      }
    }

  def make(title: Option[String] = None, todos: Seq[ToDo] = Seq()) =
    c.copy(new methods {
      subscriptions = js.defined { self =>
        js.Array(() => {
          println("ToDo: subscriptions: called during mount")
          () =>
            println("ToDo: subscriptions: unmounted")
        })
      }
      val retainedProps = RP(title)
      val reducer = (action, state, gen) => {
        action match {
          case Add(t) =>
            gen.update(state.copy(todos = state.todos :+ t, input = None))
          case Remove(id) =>
            gen.updateAndEffect(state.copy(todos = state.todos.filterNot(_.id == id)))
          case InputChanged(iopt) =>
            gen.updateAndEffect(state.copy(input = iopt))
          case _ =>
            gen.skip
        }
      }

      val initialState = _ => State(todos, None)

      val render =
        self => {
          val cn = ToDoStyling.getClassNames(500)
          div(new DivProps {
            className = cn.root
          })(
            Label()(s"""App: ${title.getOrElse("The To Do List")}"""),
            div(new DivProps { className = cn.dataEntry })(
              TextField(new ITextFieldProps {
                placeholder = "enter new todo"
                componentRef = js.defined((r: ITextField) => self.state.textFieldRef = Option(r))
                onChanged = js.defined((e: String) => self.handle(inputChanged(Option(e))))
                value = self.state.input.getOrElse[String]("")
                autoFocus = true
                onKeyPress = js.defined(e => if (e.which == dom.ext.KeyCode.Enter) addit(self))
              })(),
              PrimaryButton(new IButtonProps {
                text = "Add"
                disabled = self.state.input.size == 0
                // demonstrates inline callback
                // could be:
                // _ => since we don't use 'e', could
                // ReactEvent[dom.html.Input] to be more specifci
                // ReactKeyboardEvent[_] to be more specific
                // ReactKeyboardEvent[dom.html.Input] to be more specific
                onClick = js.defined((e: ReactEvent[_]) => addit(self))
              })()
            ),
            ToDoList.make(self.state.todos.length,
              self.state.todos,
              (id: Int) => self.handle(remove(id)),
            )
          )
        }
    })

  // You do not need this unles you will use this componen from javascript.
  @JSExportTopLevel("ToDos")
  val exportedApp = c.wrapScalaForJs((jsProps: js.Object) => make())
}

object fakedata {
  val initialToDos = Seq(
    ToDo(ToDos.mkId(), "Call Fred")
  )
}
