package com.idyria.osi.vui.javafx.table

import com.idyria.osi.vui.core.components.controls.VUIButton
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import com.idyria.osi.vui.core.components.table.SGTable
import com.idyria.osi.vui.core.components.table.SGTableColumn
import com.idyria.osi.vui.core.components.table.TableBuilderInterface
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.Region
import javafx.util.Callback
import javafx.beans.property.SimpleStringProperty
import com.idyria.osi.vui.core.components.containers.VUIPanel
import javafx.scene.layout.Pane
import java.awt.Container
import com.idyria.osi.vui.core.components.VUIComponent
import scala.reflect.ClassTag


/**
 * JFX Table always works in Object Mode
 */
trait JFXTableBuilder extends TableBuilderInterface[Node] {

  /**
   *
   */
  def table[CT](implicit tag : ClassTag[CT]): SGTable[CT, Node] = {

    return new JavaFXNodeDelegate[TableView[CT]](new TableView[CT]()) with SGTable[CT, Node] {

      this.clear
      this.delegate.setTableMenuButtonVisible(true)
      this.delegate.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
      this.mode = SGTable.DataMode.OBJECT

      this.delegate.setPrefHeight(Region.USE_COMPUTED_SIZE)

      this.delegate.setStyle(""".table-row-cell:empty {-fx-background-color: lightyellow;}""")

      this.delegate.getStylesheets().add("test_table")

      //this.delegate.set
      // Clear
      //----------
      override def clear = {
        //this.delegate.getColumns().clear()
        this.delegate.getItems().clear()
        super.clear
      }

      // Edit
      //-------------

      def setEditable(v: Boolean) = {
        this.delegate.setEditable(true)
      }

      // On Column
      //-----------------
      def createColumn(name: String): SGTableColumn[CT] = {

        new TableColumnWrapper[CT](name)

      }
      this.onWith("column.added") {
        c: TableColumnWrapper[CT] ⇒

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

                  var column = new TableColumn[CT, Button](c.name)
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

                      return new SimpleObjectProperty(cl(data.getValue()).asInstanceOf[VUIButton[_]].base.asInstanceOf[Button])

                    }
                  })

                // Something else
                //----------------
                case rt if (classOf[SGNode[_]].isAssignableFrom(rt)) =>

                  var column = new TableColumn[CT, Node](c.name)
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

                      return new SimpleObjectProperty(cl(data.getValue()).asInstanceOf[SGNode[_]].base.asInstanceOf[Node])

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
                      cl(data.getValue()).asInstanceOf[ObservableValue[String]]

                    }
                  })

                // Add Normal content
                //------------------
                case rt =>

                  //var column = new TableColumn[CT, String](c.name)
                  println(s"Addign column " + c.jfxColumn.hashCode() + " cl return type is: " + rt.getCanonicalName)

                  this.delegate.getColumns().add(c.jfxColumn)

                  c.jfxColumn.setCellValueFactory(new Callback[CellDataFeatures[CT, String], ObservableValue[String]] {

                    def call(data: CellDataFeatures[CT, String]): ObservableValue[String] = {

                      // Create value
                      var res = cl(data.getValue()) match {

                        case null => ""
                        case Nil => ""
                        case r => r.toString
                      }

                      // Return as observable

                      return new SimpleStringProperty(res)

                    }
                  })

              }

          }
        //column.setC

      }

      // Datas
      //-------------------
      this.delegate.getStylesheets.add(Thread.currentThread().getContextClassLoader.getResource("test_table.css").toString())
      this.onWith("object.added") {
        data: CT ⇒

          import scala.collection.JavaConversions._
          this.delegate.getItems().add(data)
        //this.delegate.setPrefHeight(COMPUTE_HEIGHT)
        /*this.delegate.getItems.foreach {
                    child => 
                        println(s"(**) Child type: "+child.getClass.getCanonicalName)
                }*/
        //-- Look for content of table
        //this.delegate.
        //this.delegate.getChildrenUnmodifiable().get(0).get
        //this.delegate.setPrefHeight(this.delegate.getItems().size() * 24 + 30 + 20)

      }

      /*this.delegate.setRowFactory(new Callback[TableView[CT],TableRow[CT]]() {
        
        def call(x$1: javafx.scene.control.TableView[CT]):javafx.scene.control.TableRow[CT] = {
        
          null
          
      	}
      })*/

      this.on("removeAll") {
        this.delegate.getItems().clear()
      }

    }
  }
}

class TableColumnWrapper[CT](name: String) extends SGTableColumn[CT](name) {

  //-- Create JFX Column
  var jfxColumn = new TableColumn[CT, String](name)

  // Edit 
  //-----------------
  override def onEditDone(cl: (CT, String, String) => Any): Unit = {
    jfxColumn.setEditable(true)
    jfxColumn.setOnEditCommit(new EventHandler[TableColumn.CellEditEvent[CT, String]] {
      def handle(event: TableColumn.CellEditEvent[CT, String]) = {
        cl(event.getRowValue, event.getOldValue, event.getNewValue)
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
    jfxColumn.setCellFactory(TextFieldTableCell.forTableColumn())

    /*jfxColumn.setCellFactory(TextFieldTableCell.forTableColumn[CT](new StringConverter[CT] {
      def fromString(v:String) : CT = {
        
      } 
      
      def toString(ct:CT) : String = ct.toString
    }))*/
  }
}
