package com.idyria.osi.vui.javafx

import com.idyria.osi.vui.core.VUIBuilder
import javafx.stage.Stage
import com.idyria.osi.vui.core.components.main.VuiFrame
import javafx.scene.Group
import javafx.scene.control.Label
import com.idyria.osi.vui.core.components.controls.VUILabel
import com.idyria.osi.vui.core.components.controls.VUIButton
import javafx.scene.control.Button
import javafx.scene.Scene
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import javafx.scene.Node
import com.idyria.osi.vui.core.components.scenegraph.SGGroup
import com.idyria.osi.vui.core.components.containers.VUIPanel
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.FlowPane
import javafx.scene.Parent
import com.idyria.osi.vui.core.constraints.Constraint
import com.idyria.osi.vui.core.constraints.Constraints
import com.idyria.osi.vui.core.constraints.Constrainable
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.geometry.Pos
import com.idyria.osi.vui.core.components.form.VUIInputText
import javafx.scene.control.TextField
import com.idyria.osi.vui.javafx.chart.JFXChartBuilder

class JavaFXVuiBuilder extends VUIBuilder[javafx.scene.Node] with JFXChartBuilder {

  // Utils
  //------------
   override def onUIThread(cl: => Unit) {
     
     JavaFXRun.onJavaFX({cl})

  }
  
  // Members declared in com.idyria.osi.vui.core.components.scenegraph.SceneGraphBuilder
  //--------------------
  def group(): com.idyria.osi.vui.core.components.scenegraph.SGGroup[javafx.scene.Node] = {
    panel
  }

  // Members declared in com.idyria.osi.vui.core.components.containers.ContainerBuilder
  //--------------------
  def panel: com.idyria.osi.vui.core.components.containers.VUIPanel[javafx.scene.Node] = {
    return new JavaFXNodeDelegate(new Pane()) with VUIPanel[javafx.scene.Node] {

      // Add node
      //----------------
      this.onMatch("child.added") {

        case n: SGNode[_] ⇒

          this.delegate.getChildren().add(n.base.asInstanceOf[Node])

          println(s"Adding nodes to ${this.delegate} panel: " + n.base)

        case _ ⇒
      }

      // Layout
      //-------------
      this.onMatch("layout.updated") {

        case p: javafx.scene.layout.Pane ⇒

          this.delegate = p

        case _ ⇒

        /* case gp: GridPane ⇒

          println(s"Chaging layout  to ${gp}")
          this.delegate = gp*/

      }

      /*
      
      var currentLayout: com.idyria.osi.vui.core.components.layout.VUILayout[javafx.scene.Node] = null

      def layout: com.idyria.osi.vui.core.components.layout.VUILayout[javafx.scene.Node] = currentLayout

      /**
       * Change Layout
       * In JFX, this might need replacing the current node
       */
      def layout(newLayout: com.idyria.osi.vui.core.components.layout.VUILayout[javafx.scene.Node]): Unit = {

        newLayout.setTargetGroup(this)

        this.currentLayout = newLayout

        println(s"Chaging layout  to ${newLayout}")

        newLayout match {
          case gp: GridPane =>

            println(s"Chaging layout  to ${gp}")
            this.delegate = gp

          case _ =>
        }

      }*/

    }
  }
  def tabpane: com.idyria.osi.vui.core.components.containers.VUITabPane[javafx.scene.Node] = {
    null
  }

  // Members declared in com.idyria.osi.vui.core.components.controls.ControlsBuilder
  def button(text: String): com.idyria.osi.vui.core.components.controls.VUIButton[javafx.scene.Node] = {

    return new JavaFXNodeDelegate(new Button(text)) with VUIButton[javafx.scene.Node] {

    }

  }
  def image(text: java.net.URL): com.idyria.osi.vui.core.components.controls.VUIImage[javafx.scene.Node] = {
    null
  }
  def label(text: String): com.idyria.osi.vui.core.components.controls.VUILabel[javafx.scene.Node] = {

    return new JavaFXNodeDelegate(new Label(text)) with VUILabel[javafx.scene.Node] {

      // Text
      //----------
      def setText(str: String) = this.delegate.setText(str)

    }
  }
  def text: com.idyria.osi.vui.core.components.controls.VUIText[javafx.scene.Node] = {
    null
  }
  def tree: com.idyria.osi.vui.core.components.controls.VUITree[javafx.scene.Node] = {
    null
  }

  // Members declared in com.idyria.osi.vui.core.components.form.FormBuilder
  def checkBox: com.idyria.osi.vui.core.components.form.VUICheckBox[javafx.scene.Node] = {
    null
  }
  def comboBox: com.idyria.osi.vui.core.components.form.VUIComboBox[javafx.scene.Node] = {
    null
  }
  def list(): com.idyria.osi.vui.core.components.form.VUIList[javafx.scene.Node] = {
    null
  }
  def textArea(): com.idyria.osi.vui.core.components.form.VUIInputText[javafx.scene.Node] = {
    null
  }
  def textInput(): com.idyria.osi.vui.core.components.form.VUIInputText[javafx.scene.Node] = {

    return new JavaFXNodeDelegate[TextField](new TextField) with VUIInputText[javafx.scene.Node] {

      override def setText(str: String) = delegate.setText(str)

    }

  }

  // Members declared in com.idyria.osi.vui.core.components.layout.LayoutBuilder
  //-----------------------------------

  override def stack: com.idyria.osi.vui.core.components.layout.VUIStackPane[javafx.scene.Node] = {

    new StackPane with com.idyria.osi.vui.core.components.layout.VUIStackPane[javafx.scene.Node] with CSS3ConstraintsResolver {

      override def applyConstraints(node: SGNode[Node], inputConstraints: Constraints) = {

        inputConstraints.foreach {

          // Alignment
          //----------------
          case Constraint("align", "center") ⇒

            println("--- Stack Pane align center")
            StackPane.setAlignment(node.base, Pos.CENTER)

          case c ⇒

          //println("Constraint: " + c)
        }

        resolve(node, inputConstraints)

      }

    }

  }

  def grid: com.idyria.osi.vui.core.components.layout.VUIGridLayout[javafx.scene.Node] = {

    new GridPane with com.idyria.osi.vui.core.components.layout.VUIGridLayout[javafx.scene.Node] {

      this.setGridLinesVisible(true)

      override def applyConstraints(node: SGNode[Node], inputConstraints: Constraints) = {

        // Prepare input constraints
        //-------------------
        var constraints = inputConstraints
        node match {
          case constrainable: Constrainable ⇒ constraints = inputConstraints + constrainable.fixedConstraints
          case _                            ⇒
        }

        // Resolve
        //------------------
        constraints.foreach {

          case Constraint("column", c: Int) ⇒

            GridPane.setColumnIndex(node.base, c)

          case Constraint("row", r: Int) ⇒

            GridPane.setRowIndex(node.base, r)

          // Alignment
          //----------------
          case Constraint("align", "center") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.CENTER)
            GridPane.setValignment(node.base, javafx.geometry.VPos.CENTER)

          case Constraint("align", "right") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.RIGHT)
            GridPane.setValignment(node.base, javafx.geometry.VPos.CENTER)

          case Constraint("align", "top-right") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.RIGHT)
            GridPane.setValignment(node.base, javafx.geometry.VPos.TOP)

          case Constraint("align", "bottom-right") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.RIGHT)
            GridPane.setValignment(node.base, javafx.geometry.VPos.BOTTOM)

          case Constraint("align", "left") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.LEFT)
            GridPane.setValignment(node.base, javafx.geometry.VPos.CENTER)

          case Constraint("align", "top-left") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.LEFT)
            GridPane.setValignment(node.base, javafx.geometry.VPos.TOP)

          case Constraint("align", "bottom-left") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.LEFT)
            GridPane.setValignment(node.base, javafx.geometry.VPos.BOTTOM)

          // Expand
          //-----------------

          case Constraint("expand", v) ⇒

            println("Setting expand")
            GridPane.setHgrow(node.base, Priority.ALWAYS)
            GridPane.setVgrow(node.base, Priority.ALWAYS)

            //GridPane.set

            GridPane.setHalignment(node.base, javafx.geometry.HPos.CENTER)
            GridPane.setValignment(node.base, javafx.geometry.VPos.CENTER)

          case Constraint("expandWidth", v) ⇒

            GridPane.setHgrow(node.base, Priority.ALWAYS)

          // Spead
          //-------------------
          case Constraint("spread", v) ⇒

            //println("Setting spread on: " + node.base)
            GridPane.setColumnSpan(node.base, GridPane.REMAINING)

          case c ⇒

          //println("Constraint: " + c)
        }

      }

    }
  }
  def hbox: com.idyria.osi.vui.core.components.layout.VUIHBoxLayout[javafx.scene.Node] = {
    null
  }
  def none: com.idyria.osi.vui.core.components.layout.VUIFreeLayout[javafx.scene.Node] = {
    null
  }
  def vbox: com.idyria.osi.vui.core.components.layout.VUIVBoxLayout[javafx.scene.Node] = {
    null
  }

  // Members declared in com.idyria.osi.vui.core.components.main.MainBuilder
  def frame(): com.idyria.osi.vui.core.components.main.VuiFrame[javafx.scene.Node] = {

    new Stage() with VuiFrame[Stage] {

      // Init
      //---------------

      //-- Create a Default Scene with Group
      this.setScene(new Scene(new Group))

      // Members declared in com.idyria.osi.vui.core.components.scenegraph.SGGroup
      //-------------------

      /**
       * Override Node Method to add children to Top Group
       */
      this.onMatch("child.added") {

        // Adding A group in the scene replaces the top group
        case g: SGGroup[_] ⇒

          println("Adding Group: " + g.base)
          this.getScene().setRoot(g.base.asInstanceOf[Parent])

        // Adding nodes only addes to the top node
        case n: SGNode[_] ⇒

          this.getScene().getRoot().asInstanceOf[Group].getChildren().add(n.base.asInstanceOf[Node])

      }

      /**
       *    Remove scene content by setting an empty group
       */
      def clear: Unit = {

        this.sceneProperty().get() match {
          case null ⇒
          case scene ⇒ scene.getRoot() match {
            case g: Group ⇒ g.getChildren().clear()
            case _        ⇒
          }
        }

      }

      /**
       * Does nothing
       */
      def children: Seq[com.idyria.osi.vui.core.components.scenegraph.SGNode[javafx.stage.Stage]] = {
        Nil
      }

      /**
       * Does nothing
       */
      override def removeChild(c: com.idyria.osi.vui.core.components.scenegraph.SGNode[Stage]): Unit = {

      }

      // Members declared in com.idyria.osi.vui.core.components.scenegraph.SGNode
      //-------------------
      def base: javafx.stage.Stage = this

      /**
       * Revalidate requests new layouting
       */
      def revalidate: Unit = this.base.getScene().getRoot().requestLayout()

      /**
       * Name maps to top group id
       */
      override def setName(str: String): Unit = this.base.getScene.getRoot.setId(str)

      // Members declared in com.idyria.osi.vui.core.components.main.VuiFrame
      def height(height: Int): Unit = this.base.setHeight(height)
      def width(width: Int): Unit = this.base.setWidth(width)

      def size(width: Int, height: Int): Unit = {
        this.base.setHeight(height)
        this.base.setWidth(width)
      }

      def title(title: String): Unit = {

        this.base.setTitle(title)

      }

    }.asInstanceOf[VuiFrame[javafx.scene.Node]]

  }

}

trait CSS3ConstraintsResolver {

  def resolve(node: SGNode[Node], c: Constraints) = {

    c.foreach {
      // case Constraint("""""".r"background-color",color : String) => 
      case Constraint(name, value) if (name.startsWith("css.")) ⇒

        node.base.setStyle(name.replace("css.", "-fx-") + ": " + value)

      case _ ⇒
    }

  }

}

