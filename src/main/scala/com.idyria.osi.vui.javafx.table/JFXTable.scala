package com.idyria.osi.vui.javafx.table

import com.idyria.osi.vui.core.components.table.TableBuilderInterface
import javafx.scene.Node
import com.idyria.osi.vui.core.components.table.SGTable
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import javafx.scene.control.TableView
import com.idyria.osi.vui.core.components.table.SGTableColumn
import javafx.scene.control.TableColumn
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.beans.value.ObservableValue
import javafx.beans.property.ReadOnlyStringWrapper
import com.idyria.osi.vui.core.components.controls.VUIButton
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import javafx.scene.control.TableCell
import javafx.scene.control.Button
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Region
import javafx.scene.control.TableRow

/**
 * JFX Table always works in Object Mode
 */
trait JFXTableBuilder extends TableBuilderInterface[Node] {

  /**
   *
   */
  def table[CT]: SGTable[CT, Node] = {

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
        this.delegate.getColumns().clear()
        this.delegate.getItems().clear()
        super.clear
      }

      // On Column
      //-----------------
      this.onWith("column.added") {
        c: SGTableColumn[CT] ⇒

          //-- Delegate content creation to closure
          //-- Create Simple Content of special components
          //-- Guess Result Type
          c.contentClosures match {

            // Add a default column
            case closures if (closures.size == 0) =>

              var column = new TableColumn[CT, String](c.name)
              this.delegate.getColumns().add(column)
            
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

                /*column.setCellFactory(new Callback[TableColumn[CT, Button], TableCell[CT,Button]] {

                    def call(data: CellDataFeatures[CT, String]): ObservableValue[String] = {

                      // Create value
                      var res = cl(data.getValue()) match {

                          case null => ""
                          case Nil  => ""
                          case r    => r.toString
                        }
                      

                      // Return as observable
                      return new ReadOnlyStringWrapper(res)

                    }
                  })*/

                // Non Supported
                //----------------
                case rt if (classOf[SGNode[_]].isAssignableFrom(rt)) => throw new RuntimeException(s"Cannot create table column with special component: $rt , not supported")

                // Add Normal content
                //------------------
                case rt =>

                  var column = new TableColumn[CT, String](c.name)
                  this.delegate.getColumns().add(column)
                  column.setCellValueFactory(new Callback[CellDataFeatures[CT, String], ObservableValue[String]] {

                    def call(data: CellDataFeatures[CT, String]): ObservableValue[String] = {

                      // Create value
                      var res = cl(data.getValue()) match {

                        case null => ""
                        case Nil  => ""
                        case r    => r.toString
                      }

                      // Return as observable
                      return new ReadOnlyStringWrapper(res)

                    }
                  })

              }

          }
        //column.setC

      }

      // Datas
      //-------------------
      this.onWith("object.added") {
        data: CT ⇒

          this.delegate.getItems().add(data)
          
          //-- Look for content of table
          //this.delegate.
          //this.delegate.getChildrenUnmodifiable().get(0).get
          this.delegate.setPrefHeight(this.delegate.getItems().size()*24+30+20)

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
