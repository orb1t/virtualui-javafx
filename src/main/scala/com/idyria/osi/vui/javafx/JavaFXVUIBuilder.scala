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
import com.idyria.osi.vui.javafx.model.JFXTextModelSupport
import com.idyria.osi.vui.javafx.css.JFXCSSSupport
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.stage.WindowEvent
import javafx.scene.image.ImageView
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.AlphaComposite
import java.awt.RenderingHints
import javafx.scene.image.Image
import com.idyria.osi.vui.javafx.containers.JFXTabPaneInterface
import com.idyria.osi.vui.javafx.containers.JFXSplitPaneInterface
import javafx.scene.control.TextArea
import com.idyria.osi.vui.javafx.containers.JFXTitledPaneInterface
import javafx.scene.control.ComboBox
import com.idyria.osi.vui.core.components.model.ComboBoxModel
import javafx.scene.control.ListView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import com.idyria.osi.vui.javafx.table.JFXTableBuilder
import com.idyria.osi.vui.javafx.tree.JFXTreeBuilder
import com.idyria.osi.vui.core.components.model.ListModel
import com.idyria.osi.vui.core.components.form.ListBuilderInterface
import javafx.scene.text.Text
import com.idyria.osi.vui.core.components.controls.VUIText
import com.idyria.osi.vui.core.components.model.DefaultComboBoxModel
import com.idyria.osi.vui.javafx.containers.JFXScrollPaneInterface
import com.idyria.osi.tea.logging.TeaLogging
import com.idyria.osi.tea.logging.TLogSource
import com.idyria.osi.vui.core.components.controls.ControlsBuilderInterface
import com.idyria.osi.vui.javafx.builders.JFXFormBuilder
import com.idyria.osi.vui.javafx.JavaFXRun
import com.idyria.osi.vui.core.VUIBuilder
import javafx.event.ActionEvent
import javafx.geometry.Insets
import com.idyria.osi.vui.core.components.controls.TableTreeBuilder
import com.idyria.osi.vui.javafx.table.JFXTableTree

class JavaFXVuiBuilder extends VUIBuilder[javafx.scene.Node]
    with JFXChartBuilder
    with JFXTabPaneInterface
    with JFXSplitPaneInterface
    with JFXScrollPaneInterface
    with JFXTitledPaneInterface
    with JFXTableBuilder
    with JFXTreeBuilder
    with JFXFormBuilder
    with JFXControlsBuilder
    with ControlsBuilderInterface[Node]
    with ListBuilderInterface[Node]
    with JFXTableTree
    with TLogSource {

  // Utils
  //------------
  override def onUIThread(cl: => Unit) {

    JavaFXRun.onJavaFX({ cl })

  }

  // Members declared in com.idyria.osi.vui.core.components.scenegraph.SceneGraphBuilder
  //--------------------

  def group(): com.idyria.osi.vui.core.components.scenegraph.SGGroup[javafx.scene.Node] = {

    return panel

  }

  // Members declared in com.idyria.osi.vui.core.components.containers.ContainerBuilder
  //--------------------
  def panel: com.idyria.osi.vui.core.components.containers.VUIPanel[javafx.scene.Node] = {
    return new JavaFXNodeDelegate(new Pane()) with VUIPanel[javafx.scene.Node] with JFXCSSSupport {

      // Add node
      //----------------
      this.onMatch("child.added") {

        case n: SGNode[_] ⇒

          this.delegate.getChildren().add(n.base.asInstanceOf[Node])

        /*this.layout match {
            case null =>
           
            case l => l.applyConstraints(node)
          }*/
        // println(s"Adding nodes to ${this.delegate} panel: " + n.base)

        case _ ⇒
      }
      this.onMatch("child.removed") {

        case n: SGNode[_] ⇒

          this.delegate.getChildren().remove(n.base.asInstanceOf[Node])

          this.layout match {
            case p: javafx.scene.layout.Pane =>
              p.getChildren.remove(n.base.asInstanceOf[Node])
            case _ =>
          }

        // println(s"Adding nodes to ${this.delegate} panel: " + n.base)

        case _ ⇒
      }

      // Layout
      //-------------
      this.onMatch("layout.updated") {

        case p: javafx.scene.layout.Pane ⇒

          /*this.delegate.getChildren()
          p.children.getChildren().add*/
          p.getChildren().addAll(this.delegate.getChildren())
          /*this.delegate.getChildren().foreach {
          c => 
            
        }*/
          this.delegate = p

        case _ ⇒

        /* case gp: GridPane ⇒

          println(s"Chaging layout  to ${gp}")
          this.delegate = gp*/

      }

      // Conflicts resolution
      override def clear: Unit = {

        this.delegate.getChildren().clear
        super.clear
      }

    }
  }

  // Members declared in com.idyria.osi.vui.core.components.controls.ControlsBuilder
  def button(text: String): com.idyria.osi.vui.core.components.controls.VUIButton[javafx.scene.Node] = {

    return new JavaFXNodeDelegate(new Button(text)) with VUIButton[javafx.scene.Node] with JFXCSSSupport {

      override def click = {
        this.delegate.fire
        //this.delegate.fireEvent(new ActionEvent(null, this.delegate))
      }

    }

  }
  def image(path: java.net.URL): com.idyria.osi.vui.core.components.controls.VUIImage[javafx.scene.Node] = {

    return new JavaFXNodeDelegate(new ImageView) with com.idyria.osi.vui.core.components.controls.VUIImage[javafx.scene.Node] with JFXCSSSupport {

      // Load Image
      //----------------
      // Save URL
      var url = path

      /**
       * Load into JLabel and resize if necessary
       */
      def load = {

        if (this.delegate.getImage() == null) {

          //println(s"********* LOADING IMAGE ***********")
          // Load
          //------------------
          //Image(java.lang.String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth)
          var img = new Image(url.toExternalForm())
          this.delegate.setImage(img)
          this.delegate.setFitWidth(img.getWidth())
          this.delegate.setFitHeight(img.getHeight())
          /*
            var sizeDimension = (delegate.getFitWidth(),delegate.getFitHeight())

            // Read Source Image
            //----------------
            var originalImage = ImageIO.read(url)

            // Resolve Sizes for resizing
            //-------------------------------
            (delegate.getFitWidth(),delegate.getFitHeight()) match {

              // Resize based on Width
              case (width,-1) =>

                  sizeDimension = (sizeDimension._1,width * originalImage.getHeight / originalImage.getWidth)
                  size(sizeDimension._1.toInt,sizeDimension._2.toInt)

              // Resize based on Height
              case (-1,height) =>
                  
                  sizeDimension = ( height * originalImage.getWidth / originalImage.getHeight,sizeDimension._2)
                  size(sizeDimension._1.toInt,sizeDimension._2.toInt)

              // Do nothing
              case _ =>
            }

            //-- Prepare target Image
            //-----------
            var resizedImage = new BufferedImage(sizeDimension._1.toInt, sizeDimension._2.toInt, originalImage.getType);
            var g = resizedImage.createGraphics

            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(originalImage, 0, 0,sizeDimension._1.toInt,sizeDimension._2.toInt, null);
            g.dispose

            // Set to Label
            //------------------
            this.delegate.setImage(resizedImage)*/

        }
      }
    }

  }
  def label(text: String): com.idyria.osi.vui.core.components.controls.VUILabel[javafx.scene.Node] = {

    return new JavaFXNodeDelegate(new Label(text)) with VUILabel[javafx.scene.Node] with JFXCSSSupport {

      // Text
      //----------
      def setText(str: String) = this.delegate.setText(str)
      def getText = delegate.getText

    }
  }
  def text: com.idyria.osi.vui.core.components.controls.VUIText[javafx.scene.Node] = {
    return new JavaFXNodeDelegate(new Text) with VUIText[javafx.scene.Node] with JFXCSSSupport {

      // Text
      //----------
      def setText(str: String) = onUIThread(this.delegate.setText(str))
      def getText = delegate.getText

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

            //println("--- Stack Pane align center")
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

      //this.setGridLinesVisible(true)
      this.setVgap(5.0)
      this.setHgap(5.0)

      // Add//Remove
      //-------------------

      this.onMatch("child.removed") {

        case n: SGNode[_] ⇒

          getChildren().remove(n.base.asInstanceOf[Node])

        // println(s"Adding nodes to ${this.delegate} panel: " + n.base)

        case _ ⇒
      }

      override def applyConstraints(node: SGNode[Node]) = {

        // Prepare input constraints 
        // FIXME? as Mutable List, in case some constraints are not properly ordered and need to be repushed at the end
        //-------------------
       /* var constraints = inputConstraints
        node match {
          case constrainable: Constrainable ⇒ constraints = inputConstraints + constrainable.fixedConstraints
          case _ ⇒
        }*/

        //GridPane.setHgrow(node.base, Priority.NEVER)
        //GridPane.setVgrow(node.base, Priority.NEVER)

        // Placement Opt
        //----------
        //var rowOffset = 0
        /*println(s"Doing "+node.base)
        var cList : Seq[Constraint] = constraints
        cList.foreach {
          case c => println(s"Constraint: "+c.name)
        }*/

        // Resolve
        //------------------
        GridPane.setHgrow(node.base, Priority.NEVER)
        node.fixedConstraints.foreach {

          // Row/Column Placement
          //-----------------------
          case Constraint("column", c: Int) ⇒

            GridPane.setColumnIndex(node.base, c)

          case Constraint("row", r: Int) ⇒
            //logInfo("[JFX] Pushing node " + node.base + " up one, actual pos:")

            //println("[JFX] Setting node row " + node.base + " to "+r)

            GridPane.setRowIndex(node.base, r)

          case Constraint("pushUp", r: Int) if (GridPane.getRowIndex(node.base) != null) ⇒
            //println("[JFX] Pushing node " + node.base + s" up $r, actual pos: "+GridPane.getRowIndex(node.base))
            GridPane.setRowIndex(node.base, GridPane.getRowIndex(node.base) - r - 1)
          //println(s"[JFX] -> now "+GridPane.getRowIndex(node.base))

          case Constraint("pushUp", r: Int) =>
          //println("[JFX] Pushing node " + node.base + " up one, but no index has been set")
          //rowOffset -= r

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

          case Constraint("align", "top") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.CENTER)
            GridPane.setValignment(node.base, javafx.geometry.VPos.TOP)

          case Constraint("align", "top-left") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.LEFT)
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
          case Constraint("align", "bottom") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.CENTER)
            GridPane.setValignment(node.base, javafx.geometry.VPos.BOTTOM)
          case Constraint("align", "bottom-right") ⇒
            GridPane.setHalignment(node.base, javafx.geometry.HPos.RIGHT)
            GridPane.setValignment(node.base, javafx.geometry.VPos.BOTTOM)

          // Expand
          //-----------------

          case Constraint("expand", v) ⇒

            // println("Setting expand")
            GridPane.setHgrow(node.base, Priority.ALWAYS)
            GridPane.setVgrow(node.base, Priority.ALWAYS)

            //GridPane.set

            GridPane.setHalignment(node.base, javafx.geometry.HPos.CENTER)
            GridPane.setValignment(node.base, javafx.geometry.VPos.CENTER)

          case Constraint("expandWidth", v) ⇒

            logFine("[JFX]Setting expandWidth on " + node.base)
            GridPane.setHgrow(node.base, Priority.ALWAYS)

          // Spead/Span
          //-------------------
          case Constraint("spread", v) ⇒

            //println("Setting spread on: " + node.base)
            GridPane.setColumnSpan(node.base, GridPane.REMAINING)
          //GridPane.setRowSpan(node.base, GridPane.REMAINING)

          case Constraint("rowspan", v) ⇒

            //println("[JFX] Setting row span " + node.base + " to "+v)
            GridPane.setRowSpan(node.base, v.asInstanceOf[Int])

          case Constraint("colspan", v) ⇒

            //println("Setting colspan on: " + node.base+" => "+v)
            GridPane.setColumnSpan(node.base, v.asInstanceOf[Int])
          case c ⇒

            logWarn("[Unsupported Constraint] Constraint: " + c.name)
          //println()
        }

      }

    }
  }
  def hbox: com.idyria.osi.vui.core.components.layout.VUIHBoxLayout[javafx.scene.Node] = {
    new HBox with com.idyria.osi.vui.core.components.layout.VUIHBoxLayout[javafx.scene.Node] {

      override def applyConstraints(node: SGNode[Node], inputConstraints: Constraints) = {

        // Prepare input constraints
        //-------------------
        var constraints = inputConstraints
        node match {
          case constrainable: Constrainable ⇒ constraints = inputConstraints + constrainable.fixedConstraints
          case _ ⇒
        }

        //GridPane.setHgrow(node.base, Priority.NEVER)
        //GridPane.setVgrow(node.base, Priority.NEVER)

        // Resolve
        //------------------
        GridPane.setVgrow(node.base, Priority.NEVER)
        constraints.foreach {

          // Expand
          //-----------------

          case Constraint("expand", v) ⇒

            HBox.setHgrow(node.base, Priority.ALWAYS)
            GridPane.setHgrow(node.base, Priority.ALWAYS)

          case Constraint("expandWidth", v) ⇒

            println(s"HBOX HGROW for: " + node.base)
            HBox.setHgrow(node.base, Priority.ALWAYS)
            GridPane.setHgrow(node.base, Priority.ALWAYS)

          case c ⇒

          //logWarn("[Unsupported Constraint] Constraint: " + c.name)
          //println()
        }

      }

    }
  }
  def none: com.idyria.osi.vui.core.components.layout.VUIFreeLayout[javafx.scene.Node] = {
    null
  }
  def vbox: com.idyria.osi.vui.core.components.layout.VUIVBoxLayout[javafx.scene.Node] = {
    new VBox with com.idyria.osi.vui.core.components.layout.VUIVBoxLayout[javafx.scene.Node] {

      override def applyConstraints(node: SGNode[Node], inputConstraints: Constraints) = {

        // Prepare input constraints
        //-------------------
        var constraints = inputConstraints
        node match {
          case constrainable: Constrainable ⇒ constraints = inputConstraints + constrainable.fixedConstraints
          case _ ⇒
        }

        //GridPane.setHgrow(node.base, Priority.NEVER)
        //GridPane.setVgrow(node.base, Priority.NEVER)

        // Resolve
        //------------------
        GridPane.setHgrow(node.base, Priority.NEVER)
        constraints.foreach {

          // Expand
          //-----------------

          case Constraint("expand", v) ⇒

          //VBox.setHgrow(node.base, Priority.ALWAYS)

          case Constraint("expandWidth", v) ⇒

          //HBox.setHgrow(node.base, Priority.ALWAYS)

          case Constraint("margin", v) =>

            VBox.setMargin(node.base, new Insets(v.asInstanceOf[Double]))

          case c ⇒

          //logWarn("[Unsupported Constraint] Constraint: " + c.name)
          //println()
        }

      }

    }
  }

  // Members declared in com.idyria.osi.vui.core.components.main.MainBuilder
  def frame(): com.idyria.osi.vui.core.components.main.VuiFrame[javafx.scene.Node] = {

    new Stage() with VuiFrame[Stage] {

      // Init
      //---------------

      //-- Create a Default Scene with Group
      //var topGroup = new Group
      this.setScene(new Scene(new Group))

      //-- Per default close, don't hide

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
      override def clear: Unit = {

        this.sceneProperty().get() match {
          case null ⇒
          case scene ⇒ scene.getRoot() match {
            case g: Group ⇒ g.getChildren().clear()
            case _ ⇒
          }
        }

        super.clear

      }

      /**
       * Does nothing
       */
      /* def children: Seq[com.idyria.osi.vui.core.components.scenegraph.SGNode[javafx.stage.Stage]] = {
        Nil
      }

      /**
       * Does nothing
       */
      override def removeChild(c: com.idyria.osi.vui.core.components.scenegraph.SGNode[Stage]): Unit = {

      }*/

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

      override def close() = {
        onUIThread(super.close)
      }

      /*override def show() = {
          onUIThread(super.show())
      }*/

      // Events
      //---------------------

      /**
       * When the Window gets closed
       */
      override def onClose(cl: => Unit) = {

        this.setOnCloseRequest(new EventHandler[WindowEvent] {
          def handle(e: WindowEvent) = {

            cl
          }
        })

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
 
