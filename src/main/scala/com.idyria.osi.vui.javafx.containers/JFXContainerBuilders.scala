package com.idyria.osi.vui.javafx.containers

import com.idyria.osi.vui.core.components.containers.TabPaneBuilderInterface
import com.idyria.osi.vui.core.components.containers.VUITabPane
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import javafx.scene.Node
import javafx.scene.control.TabPane
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import javafx.scene.control.Tab
import com.idyria.osi.vui.core.components.containers.VUISplitPane
import com.idyria.osi.vui.core.components.containers.SplitPaneBuilderInterface
import javafx.scene.control.SplitPane
import javafx.geometry.Orientation
import com.idyria.osi.vui.core.components.containers.TitledPaneBuilderInterface
import javafx.scene.control.TitledPane
import com.idyria.osi.vui.core.components.containers.VUITitledPane
import com.idyria.osi.vui.core.components.containers.VUITab
import com.idyria.osi.vui.core.components.containers.ScrollPaneBuilderInterface
import com.idyria.osi.vui.core.components.containers.VUIPanel
import javafx.scene.layout.Pane
import javafx.scene.layout.GridPane

trait JFXTabPaneInterface extends TabPaneBuilderInterface[Node] {

  def tabpane: com.idyria.osi.vui.core.components.containers.VUITabPane[javafx.scene.Node] = {
    return new JavaFXNodeDelegate[TabPane](new TabPane) with VUITabPane[javafx.scene.Node] {

      // Conflicts resolution
      override def clear: Unit = {

        this.delegate.getTabs().clear()
        super.clear
      }

      def node[NT <: SGNode[Node]](tabname: String)(node: NT): NT = {

        // Prepare Tab
        //-----------
        var tab = new Tab
        tab.setText(tabname)
        tab.setContent(node.base)
        tab.setClosable(false)

        delegate.getTabs().add(tab)
        node

      }

      def addTab[NT <: SGNode[Node]](tabname: String)(node: NT): VUITab[Node] = {

        // Prepare Tab
        //-----------
        var tab = new Tab
        var vuiTab = new VUITab[Node] {

          def setClosable(v: Boolean) = {
            tab.setClosable(v)
          }

        }
        tab.setText(tabname)
        tab.setContent(node.base)
        tab.setClosable(false)

        delegate.getTabs().add(tab)
        vuiTab

      }

    }
  }

}

trait JFXSplitPaneInterface extends SplitPaneBuilderInterface[Node] {

  /**
   * Creates a tabpane component to store nodes into panes
   */
  def splitpane: VUISplitPane[Node] = {

    return new JavaFXNodeDelegate[SplitPane](new SplitPane) with VUISplitPane[javafx.scene.Node] {

      // Add node
      //----------------
      this.onMatch("child.added") {

        case n: SGNode[_] ⇒

          this.delegate.getItems.add(n.base.asInstanceOf[Node])
          SplitPane.setResizableWithParent(n.base.asInstanceOf[Node], true)
        //this.delegate.setDividerPositions(0.3f);
        // println(s"Adding nodes to ${this.delegate} splitpane: " + n.base)

        case _ ⇒
      }

      /**
       * Clear
       */
      override def clear: Unit = {

        this.delegate.getItems().clear()
        super.clear
      }

      def setVertical = delegate.setOrientation(Orientation.VERTICAL)

      override def setHorizontal = {
        delegate.setOrientation(Orientation.HORIZONTAL)
        //delegate.orientationProperty().setValue(Orientation.HORIZONTAL)
        //println(s"Orientation set to: "+delegate.getOrientation())
      }

    }
  }

}

trait JFXScrollPaneInterface extends ScrollPaneBuilderInterface[Node] {

  def scrollpane : com.idyria.osi.vui.core.components.containers.VUIPanel[javafx.scene.Node] = {
    return new JavaFXNodeDelegate(new javafx.scene.control.ScrollPane()) with VUIPanel[javafx.scene.Node] {

      // Add Content Panel
      //-----------------
      var pane = new Pane
      //this.delegate.setContent(pane)
      this.delegate.fitToHeightProperty().set(true)
      this.delegate.fitToWidthProperty().set(true)
      /*this.delegate.fitToHeightProperty().set(true)
      this.delegate.fitToWidthProperty().set(true)
      this.delegate.setPrefViewportWidth( javafx.scene.layout.Region.USE_COMPUTED_SIZE)
      this.delegate.setPrefWidth(javafx.scene.layout.Region.USE_COMPUTED_SIZE)*/
      
      // Add node
      //----------------
      this.onMatch("child.added") {

        case n: SGNode[_] ⇒
        
          this.delegate.setContent(n.base.asInstanceOf[Node])
          this.revalidate
          //pane.getChildren().add(n.base.asInstanceOf[Node])

         //println(s"****** Adding nodes to ${this.delegate}: " + n.base+" which has children: "+n.base.asInstanceOf[GridPane].getChildren().size())

        case _ ⇒
      }

      

      // Conflicts resolution
      override def clear: Unit = {

        pane.getChildren().clear
        super.clear
      }

    }
  }

}

trait JFXTitledPaneInterface extends TitledPaneBuilderInterface[Node] {

  def titledPane(title: String): VUITitledPane[Node] = {

    return new JavaFXNodeDelegate[TitledPane](new TitledPane) with VUITitledPane[javafx.scene.Node] {

      // Set title
      this.delegate.setText(title)
      
     

      // Add node
      //----------------
      this.onMatch("child.added") {
    	  
        //-- If Content is already set and is a pane, add to this pane
        case n : SGNode[_] if(this.delegate.getContent()!=null && this.delegate.getContent().isInstanceOf[javafx.scene.layout.Pane]) =>
        
          this.delegate.getContent.asInstanceOf[javafx.scene.layout.Pane].getChildren().add(n.base.asInstanceOf[Node])
          
        //-- Add as content
        case n: SGNode[_] ⇒

          this.delegate.setContent(n.base.asInstanceOf[Node])

        // println(s"Adding nodes to ${this.delegate} panel: " + n.base)

        case _ ⇒
      }
      
      // Layout
      //-------------
      this.onMatch("layout.updated") {

        case p: javafx.scene.layout.Pane ⇒

          this.delegate.setContent(p)

        case _ ⇒

        /* case gp: GridPane ⇒

          println(s"Chaging layout  to ${gp}")
          this.delegate = gp*/

      }

      /**
       * Opens the Titled Pane
       */
      def open = {
        this.delegate.setExpanded(true)
      }

      /**
       * Close the Titled Pane
       */
      def close = {
        this.delegate.setExpanded(false)
      }

      /**
       * Sets if the panel can be opened/closed, or must always be openened
       */
      def setCollapsible(b: Boolean) = {
        this.delegate.setCollapsible(false)
      }

      // Conflicts resolution
      override def clear: Unit = {

        // this.delegate.getItems().clear()
        super.clear
      }

    }

  }
}

