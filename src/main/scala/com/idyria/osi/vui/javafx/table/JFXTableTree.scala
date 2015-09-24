package com.idyria.osi.vui.javafx.table

import com.idyria.osi.vui.core.components.controls.TableTreeBuilderInterface
import javafx.scene.Node
import com.idyria.osi.vui.core.components.controls.VUITree
import com.idyria.osi.vui.core.components.controls.VUITableTree
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import javafx.scene.control.TreeTableView
import com.idyria.osi.vui.core.components.table.SGTableColumn
import javafx.scene.control.TreeView
import javafx.scene.control.TreeItem
import javafx.beans.value.ObservableValue
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import com.idyria.osi.vui.core.components.controls.VUIButton
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TreeTableColumn.CellDataFeatures
import javafx.scene.control.TableColumn
import javafx.util.Callback
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.cell.TextFieldTableCell
import javafx.event.EventHandler
import com.idyria.osi.vui.core.components.controls.TreeNode
import javafx.scene.control.TableView

/**
 * @author zm4632
 */
trait JFXTableTree extends TableTreeBuilderInterface[Node] {

  def tableTree[CT <: TreeNode](tree: VUITree[CT,Node]): VUITableTree[CT, Node] = {

    new JavaFXNodeDelegate(new TreeTableView[CT]) with VUITableTree[CT, Node] {
    
      // Make columns use the full width
      this.delegate.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY)
      
      // Clear
      //----------
      override def clear = {
        //this.delegate.getColumns().clear()
        //this.delegate.getItems().clear()
        super.clear
      }
      
      
      // Root Handle
      //---------------
      override def setShowRootHandle(v:Boolean) = {
        this.delegate.setShowRoot(v)
      }
      
      // Table Stuff
      //----------------------

      def setEditable(v: Boolean) = {
        this.delegate.setEditable(true)
      }

      def createColumn(name: String): SGTableColumn[CT] = {

        new TreeTableColumnWrapper[CT](name)

      }
      
      
      // Tree Set 
      //------------------
      this.onWith("tree.set") {
        t : VUITree[CT,Node] =>
          
          //println(s"Adding tree to table tree view")
          //this.delegate.setShowRoot(true)
          var root = t.base.asInstanceOf[TreeView[_]].getRoot.asInstanceOf[TreeItem[CT]]
          
          //println(s"Root: "+root.toString())
          //println(s"Root children: "+root.getChildren.size())
          
          root.setExpanded(true)
          
          this.delegate.setRoot(root)
          
          
      }

      // Columns
      //---------------------
      this.onWith("column.added") {
        c: TreeTableColumnWrapper[CT] ⇒

          // React to table column events 
          //---------------

          //-- Delegate content creation to closure
          //-- Create Simple Content of special components
          //-- Guess Result Type
          c.contentClosures match {

            // Add a default column
            case closures if (closures.size == 0) =>

              this.delegate.getColumns().add(c.jfxColumn)

            case closures =>

              var cl = closures.head
              var returnType = c.contentClosuresResultsType(cl)
              //println(s"--- Column ${c.name} returns type: $returnType")

              returnType match {

                // Add Button to Cell
                //-------------------------
                case rt if (rt == classOf[VUIButton[_]]) =>

                  var column = new TreeTableColumn[CT, Button](c.name)
                  this.delegate.getColumns().add(column)

                  column.setCellValueFactory(new Callback[CellDataFeatures[CT, Button], ObservableValue[Button]] {

                    def call(data: CellDataFeatures[CT, Button]): ObservableValue[Button] = {

                      // Create value
                      /* var res = cl(data.getValue()) match {

                          case null => ""
                          case Nil  => ""
                          case r    => r.toString
                        }*/

                      // Return as observable
                      //return new ReadOnlyStringWrapper(res)

                      return new SimpleObjectProperty(cl(data.getValue().getValue).asInstanceOf[VUIButton[_]].base.asInstanceOf[Button])

                    }
                  })

                // Something else
                //----------------
                case rt if (classOf[SGNode[_]].isAssignableFrom(rt)) =>

                  var column = new TreeTableColumn[CT, Node](c.name)
                  this.delegate.getColumns().add(column)

                  column.setCellValueFactory(new Callback[CellDataFeatures[CT, Node], ObservableValue[Node]] {

                    def call(data: CellDataFeatures[CT, Node]): ObservableValue[Node] = {

                      // Create value
                      /* var res = cl(data.getValue()) match {

                          case null => ""
                          case Nil  => ""
                          case r    => r.toString
                        }*/

                      // Return as observable
                      //return new ReadOnlyStringWrapper(res)

                      return new SimpleObjectProperty(cl(data.getValue().getValue).asInstanceOf[SGNode[_]].base.asInstanceOf[Node])

                    }
                  })

                // Non Supported
                //----------------
                //case rt if (classOf[SGNode[_]].isAssignableFrom(rt)) => throw new RuntimeException(s"Cannot create table column with special component: $rt , not supported")

                // Property
                //----------------
                case rt if (classOf[ObservableValue[String]].isAssignableFrom(rt)) =>

                  this.delegate.getColumns().add(c.jfxColumn)
                  c.jfxColumn.setCellValueFactory(new Callback[CellDataFeatures[CT, String], ObservableValue[String]] {

                    def call(data: CellDataFeatures[CT, String]): ObservableValue[String] = {

                      // Create value
                      //println(s"Result of property closure is: "+cl(data.getValue()))
                      cl(data.getValue().getValue).asInstanceOf[ObservableValue[String]]

                    }
                  })

                // Add Normal content
                //------------------
                case rt =>

                  //var column = new TableColumn[CT, String](c.name)
                  println(s"Addign column " + c.jfxColumn.hashCode() + s" cl ${c.jfxColumn.getText} return type is: " + rt.getCanonicalName)
                  
                  var column = new TreeTableColumn[CT, Any](c.name)
                  //column.setPrefWidth()
                  this.delegate.getColumns().add(column)
                  
                  //this.delegate.getColumns().add(c.jfxColumn)
                 
                  column.setCellValueFactory(new Callback[CellDataFeatures[CT, Any], ObservableValue[Any]] {

                    def call(data: CellDataFeatures[CT, Any]): ObservableValue[Any] = {

                      // Create value
                      cl(data.getValue().getValue) match {

                        case null => new SimpleObjectProperty("")
                        case Nil => new SimpleObjectProperty("")
                        case button : VUIButton[_] => new SimpleObjectProperty(button.base.asInstanceOf[Button])
                        case r => new SimpleObjectProperty(r.toString)
                      }

                      // Return as observable

                      //return new SimpleStringProperty(res)

                    }
                  })

              }

          }
        //column.setC

      }
      
    }

  }

}

class TreeTableColumnWrapper[CT](name: String) extends SGTableColumn[CT](name) {

  //-- Create JFX Column
  var jfxColumn = new TreeTableColumn[CT, String](name)

  // Edit 
  //-----------------
  override def onEditDone(cl: (CT, String, String) => Any): Unit = {
    jfxColumn.setEditable(true)
    jfxColumn.setOnEditCommit(new EventHandler[TreeTableColumn.CellEditEvent[CT, String]] {
      def handle(event: TreeTableColumn.CellEditEvent[CT, String]) = {
        cl(event.getRowValue.getValue, event.getOldValue, event.getNewValue)
      }
    })
  }

  // Implementation
  //--------------------

  override def setEditable(v: Boolean) = {
    //println(s"Setting editable $v on " + this.jfxColumn.hashCode())
    jfxColumn.setEditable(v)

    /* jfxColumn.setOnEditStart(new EventHandler[TableColumn.CellEditEvent[CT, String]] {
      def handle(event: TableColumn.CellEditEvent[CT, String]) = {
        // action(event)
        //println(s"Start Edit")
      }
    })
    jfxColumn.setOnEditCommit(new EventHandler[TableColumn.CellEditEvent[CT, String]] {
      def handle(event: TableColumn.CellEditEvent[CT, String]) = {
        // action(event)
        //println(s"EOD Edit: "+event.getNewValue+" on "+event.getRowValue)
      }
    })*/
   
    // FIXME
    //jfxColumn.setCellFactory(TextFieldTableCell.forTableColumn())

    /*jfxColumn.setCellFactory(TextFieldTableCell.forTableColumn[CT](new StringConverter[CT] {
      def fromString(v:String) : CT = {
        
      } 
      
      def toString(ct:CT) : String = ct.toString
    }))*/
  }
}