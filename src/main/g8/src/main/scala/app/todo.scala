package app

import scala.scalajs.js
import js.annotation._
import js.Dynamic.{literal => lit, global => g}
import js.JSConverters._
import org.scalajs.dom
import react._
import react.implicits._
import fabric._
import fabric.components._
import fabric.styling._
import vdom._

case class ToDo(id: Int, name: String, added: js.Date = null, completed: Boolean = false)

object ToDoItem {
  val Name = "ToDoItem"

  trait Props extends js.Object {
    var todo: ToDo
    var remove: () => Unit
    var rootClassname: js.UndefOr[String] = js.undefined
    var titleClassname: js.UndefOr[String] = js.undefined
    var key: js.UndefOr[String] = js.undefined
  }

  def apply(props: Props) = render.elementWith(props)

  val render: ReactFC[Props] = props => {
    divWithClassname(
      props.rootClassname,
      Label(new Label.Props {
        className = props.titleClassname
      })(
        props.todo.name
      ),
      Button.Default(new Button.Props {
        text = "Remove"
        onClick = js.defined(_ => props.remove())
      })()
    )
  }
  render.displayName(Name)
}

object ToDoListHeader {
  val Name = "ToDoListHeader"

  trait Props extends js.Object {
    var length: Int
  }

  def apply(props: Props) = render.elementWith(props)

  val render: ReactFC[Props] = props => {
    div(Label(s"# To Dos - ${props.length}"))
  }
  render.displayName(Name)
}

object ToDoList {
  val Name = "ToDoList"

  trait Props extends js.Object {
    var length: Int
    var todos: Seq[ToDo]
    var remove: Int => Unit
    var listClassname: js.UndefOr[String] = js.undefined
    var todoClassname: js.UndefOr[String] = js.undefined
    var titleClassname: js.UndefOr[String] = js.undefined
  }

  def apply(props: Props) = render.elementWith(props)

  val render: ReactFC[Props] = props => {
    divWithClassname(
      props.listClassname,
      ToDoListHeader(new ToDoListHeader.Props{ var length = props.length}),
      props.todos.map(t =>
        ToDoItem(new ToDoItem.Props {
          var todo = t
          var remove = () => props.remove(t.id)
          rootClassname = props.todoClassname
          titleClassname = props.titleClassname
          key = t.id.toString
        })
      ))
  }
  render.displayName(Name)
}

object ToDos {
  sealed trait Action
  case class Add(todo: ToDo)                     extends Action
  case class Remove(id: Int)                     extends Action
  case class InputChanged(input: Option[String]) extends Action

  var idCounter: Int = -1
  def mkId(): Int    = { idCounter = idCounter + 1; idCounter }

  /** We put all state into one fat object. Probably better
   * to separate out `input` into its own useState.
   */
  case class State(
    todos: Seq[ToDo] = Seq(),
    input: Option[String] = None,
    var textFieldRef: Option[TextField.ITextField] = None
  )

  val Name = "ToDos"

  def addit(input: Option[String], dispatch: Dispatch[Action]) =
    input.foreach { i =>
      dispatch(Add(ToDo(mkId(), i)))
    }

  def reducer(state: State, action: Action): State =
    action match {
      case Add(t) =>
        state.copy(todos = state.todos :+ t, input = None)
      case Remove(id) =>
        state.copy(todos = state.todos.filterNot(_.id == id))
      case InputChanged(iopt) =>
        state.copy(input = iopt)
    }
  
  trait Props extends js.Object {
    var title: String
    var todos: Seq[ToDo]
    var className: js.UndefOr[String] = js.undefined
    var styles: js.UndefOr[IStyleFunctionOrObject[StyleProps, Styles]] = js.undefined
  }

  def apply(props: Props) = render.elementWith(props)

  val render: ReactFC[Props] = props => {
    val ifield = useRef[TextField.ITextField](null)    
    useEffectMounting{() =>
      println("ToDo: subscriptions: called during mount")
        () => println("ToDo: subscriptions: unmounted")
    }

    val (state, dispatch) =
      useReducer[State,Action](reducer, State(props.todos, None))
    // if the input is added as a todo or todo remove, reset focus
    useEffect(state.todos.length){() =>
      if(ifield.current != null) ifield.current.focus()
    }

    val cn = getClassNames(
      new StyleProps { className = props.className /* add style hints from props if any */ },
      props.styles 
    )

    div(new DivProps {
      className = cn.root
    })(
      Label(s"""App: ${props.title}"""),
      div(new DivProps { className = cn.dataEntry })(
        TextField(new TextField.Props {
          placeholder = "enter new todo"
          componentRef = js.defined{ifield}
          onChangeInput = js.defined{(_, v) =>
            dispatch(InputChanged(v.toOption))
          }
          value = state.input.getOrElse[String]("")
          autoFocus = true
          onKeyPress = js.defined{
            e => if (e.which == dom.ext.KeyCode.Enter) addit(state.input, dispatch)
          }
        }),
          Button.Primary(new Button.Props {
            text = "Add"
            disabled = state.input.size == 0
            // demonstrates inline callback
            // could be:
            // _ => since we don't use 'e'
            // ReactEvent[dom.html.Input] to be more specific
            // ReactKeyboardEvent[_] to be more specific
            // ReactKeyboardEvent[dom.html.Input] to be more specific
            onClick = js.defined((e: ReactEvent[_]) => addit(state.input, dispatch))
          })()
        ),
        ToDoList(new ToDoList.Props {
          var length = state.todos.length
          var todos = state.todos
          var remove = (id: Int) => dispatch(Remove(id))
          todoClassname = cn.todo
          titleClassname = cn.title
        })
    )
  }
  render.displayName(Name)

  @deriveClassNames trait Styles extends IStyleSetTag {
    var root: js.UndefOr[IStyle] = js.undefined
    var todo: js.UndefOr[IStyle] = js.undefined
    var title: js.UndefOr[IStyle] = js.undefined
    var dataEntry: js.UndefOr[IStyle] = js.undefined
  }

  trait StyleProps extends js.Object {
    var className: js.UndefOr[String] = js.undefined
    var randomArg: js.UndefOr[Int] = js.undefined
  }

  val getStyles = stylingFunction[StyleProps, Styles] { props =>
    val randomArg = props.randomArg.getOrElse(300)
    new Styles {
      root = stylearray(
        new IRawStyle {
          selectors = selectorset(
            ":global(:root)" -> lit(
              "--label-width" -> s"${randomArg}px",
            ))
        })
      todo = new IRawStyle {
        displayName = "machina"
        display = "flex"
        marginBottom = "10px"
        selectors = selectorset("& $title" -> new IRawStyle {})
      }
      title =  new IRawStyle {
        width = "var(--label-width)"
        marginRight = "10px"
      }
      dataEntry = new IRawStyle {
        display = "flex"
        selectors = selectorset("& .ms-Textfield" -> new IRawStyle {
          width = "var(--label-width)"
          marginRight = "10px"
        })
      }
    }
  }

  import merge_styles._
  val getClassNames: GetClassNamesFn[StyleProps, Styles, ClassNames] =
    (p,s) => mergeStyleSets(concatStyleSetsWithProps(p,getStyles,s))
}

object fakedata {
  val initialToDos = Seq(
    ToDo(ToDos.mkId(), "Call Fred")
  )
}
