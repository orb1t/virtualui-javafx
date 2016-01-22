package com.idyria.osi.vui.javafx.builders

import scala.collection.JavaConversions.asScalaBuffer
import com.idyria.osi.vui.core.components.controls.SliderBuilderInterface
import com.idyria.osi.vui.core.components.controls.SpinnerBuilderInterface
import com.idyria.osi.vui.core.components.controls.ToggleGroup
import com.idyria.osi.vui.core.components.controls.VUIRadioButton
import com.idyria.osi.vui.core.components.controls.VUISlider
import com.idyria.osi.vui.core.components.controls.VUISpinner
import com.idyria.osi.vui.core.components.form.FormBuilderInterface
import com.idyria.osi.vui.core.components.form.ListBuilderInterface
import com.idyria.osi.vui.core.components.form.VUIInputText
import com.idyria.osi.vui.core.components.model.ComboBoxModel
import com.idyria.osi.vui.core.components.model.DefaultComboBoxModel
import com.idyria.osi.vui.core.components.model.ListModel
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import com.idyria.osi.vui.javafx.JavaFXRun
import com.idyria.osi.vui.javafx.listeners.FXChangeListeners
import com.idyria.osi.vui.javafx.model.JFXTextModelSupport
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.RadioButton
import javafx.scene.control.Slider
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.Toggle
import com.idyria.osi.vui.core.VBuilder
import com.idyria.osi.vui.core.ErrorSupportTrait
import java.awt.Robot

trait JFXFormBuilder extends FormBuilderInterface[Node] with ListBuilderInterface[Node] with SliderBuilderInterface[Node] with SpinnerBuilderInterface[Node] with ErrorSupportTrait {

  def comboBox[CT]: com.idyria.osi.vui.core.components.form.VUIComboBox[CT, javafx.scene.Node] = {
    return new JavaFXNodeDelegate[ComboBox[CT]](new ComboBox) with com.idyria.osi.vui.core.components.form.VUIComboBox[CT, javafx.scene.Node] {

      // Model
      //-------------------

      //-- Default
      //this.delegate.setModel(new SwingComboBoxModelModelAdapter(modelImpl))

      onWith("model.changed") {
        m: ComboBoxModel[CT] =>

          //-- Transfer datas to new model
          m.data.foreach {
            obj => this.delegate.getItems().add(obj)
          }

      }

      // this.model.getClass().getS
      // this.modelImpl

      modelImpl = new DefaultComboBoxModel[CT]
      this.modelImpl.onWith("added") {
        o: CT =>

          this.delegate.itemsProperty().get.add(o)
          if (this.delegate.getSelectionModel().isEmpty()) {
            this.delegate.getSelectionModel().selectFirst()
          }
      }
      this.model.onWith("removed") {
        o: CT => this.delegate.itemsProperty().get.remove(o)
      }

      // Events
      //--------------

      /**
       * React on Selection
       */
      def onSelected(cl: CT => Unit) = {

        /* this.delegate.itemsProperty().addListener(new ChangeListener[Object] {
          def changed(obs:ObservableValue[_ <: Object],prev:Object,n:Object) = {
            cl(n)
          }
        })
        */
        this.delegate.selectionModelProperty().get().selectedItemProperty().addListener(new ChangeListener[CT] {
          def changed(obs: ObservableValue[_ <: CT], prev: CT, n: CT) = {
            cl(n)
          }
        })
        /*this.delegate.addItemListener(new ItemListener {
          
          
          def itemStateChanged( e : ItemEvent) = {
            
            cl(e)
            
          }
          
        }
        )*/

      }

      // Selection
      //-----------------

      /**
       * Select the object if contained into model
       */
      override def select(obj: CT): Unit = {

        this.delegate.selectionModelProperty().get().select(obj)

      }

      /**
       * React to user selection by updating selected in model
       */
      this.onSelected {
        s: CT => model.selected = s
      }

    }
  }
  def list[CT]: com.idyria.osi.vui.core.components.form.VUIList[CT, javafx.scene.Node] = {
    return new JavaFXNodeDelegate[ListView[CT]](new ListView) with com.idyria.osi.vui.core.components.form.VUIList[CT, javafx.scene.Node] {

      // Model
      //-------------------

      //-- Default
      //this.delegate.setModel(new SwingComboBoxModelModelAdapter(modelImpl))

      onWith("model.changed") {
        m: ListModel[CT] =>

          m.data.foreach {
            obj => this.delegate.getItems().add(obj)
          }
      }

      def clearSelection = {
        this.delegate.selectionModelProperty().get().clearSelection()
      }

      // Events
      //--------------

      def onSelected(cl: Seq[CT] => Unit) = {

        this.delegate.selectionModelProperty().get().selectedItemProperty().addListener(new ChangeListener[CT] {
          def changed(obs: ObservableValue[_ <: CT], prev: CT, n: CT) = {

            cl(delegate.selectionModelProperty().getValue().getSelectedItems())
          }
        })
        /*this.delegate.addItemListener(new ItemListener {
          
          
          def itemStateChanged( e : ItemEvent) = {
            
            cl(e)
            
          }
          
        }
        )*/

      }

      // Selection
      //-----------------

      /**
       * Select the object if contained into model
       */
      def select(obj: CT) = {

        this.delegate.selectionModelProperty().get().select(obj)

      }

    }
  }
  def textArea: com.idyria.osi.vui.core.components.form.VUIInputText[javafx.scene.Node] = {

    return new JavaFXNodeDelegate[TextArea](new TextArea) with VUIInputText[javafx.scene.Node] with JFXTextModelSupport {

      override def setText(str: String) = JavaFXRun.onJavaFX({ delegate.setText(str) })
      def getText = delegate.getText

      // Model Support
      //------------
      this.model.onWith("model.insertText") {
        str: String => JavaFXRun.onJavaFX({ (delegate.insertText(getText.length, str)) })
      }
      def textProperty = delegate.textProperty

    }

  }
  def textInput: com.idyria.osi.vui.core.components.form.VUIInputText[javafx.scene.Node] = {

    return new JavaFXNodeDelegate[TextField](new TextField) with VUIInputText[javafx.scene.Node] with JFXTextModelSupport {

      // Columns
      //--------------------
      override def setColumns(c: Int): Unit = {
        this.delegate.setPrefColumnCount(c)
      }

      // Text
      //-------------
      override def setText(str: String) = delegate.setText(str)
      def getText = delegate.getText

      // Model Support
      //------------
      def textProperty = delegate.textProperty

      // Special Listeners
      //--------------------------
      override def onEnterKey(cl: => Any): Unit = {

        this.delegate.onActionProperty().set(new EventHandler[ActionEvent] {

          def handle(ev: ActionEvent) = {
            catchError(cl)
          }
        })
        /*this.delegate.onActionProperty().addListener(new ChangeListener[EventHandler[ActionEvent]] {

          def changed(obs: ObservableValue[_ <: ActionEvent], old: ActionEvent, newValue: ActionEvent): Unit = {
           
          }

        })*/

      }

    }

  }

  def checkBox: com.idyria.osi.vui.core.components.form.VUICheckBox[javafx.scene.Node] = {
    return new JavaFXNodeDelegate[CheckBox](new CheckBox) with com.idyria.osi.vui.core.components.form.VUICheckBox[Node] {

      // State
      //--------------
      def isChecked = this.delegate.isSelected()

      this.checkedProperty.onUpdated {
        case v: Boolean => this.delegate.selectedProperty().setValue(v)
      }

      this.delegate.selectedProperty().addListener(FXChangeListeners.closureToChange { value: java.lang.Boolean => checkedProperty.set(value) })

      /*this.delegate.selectedProperty().addListener(new ChangeListener[java.lang.Boolean] {
          def changed(o : ObservableValue[_ <: java.lang.Boolean] , old : java.lang.Boolean, newVal : java.lang.Boolean) = {
              println(s"JFX Changed property to : $newVal")
              checkedProperty.set(newVal)
          }
      })*/

      // Text
      //-----------
      def setText(str: String) = {
        this.delegate.setText(str)
      }
      def getText = delegate.getText()

    }
  }

  def radioButton: VUIRadioButton[Node] = {

    return new JavaFXNodeDelegate[RadioButton](new RadioButton) with VUIRadioButton[Node] {

      // State
      //--------------
      def isChecked = {

        this.delegate.getToggleGroup() match {
          case null => this.delegate.isSelected()
          case tg => tg.getSelectedToggle() == this.delegate
        }

      }

      def setChecked(b: Boolean) = {

        this.delegate.getToggleGroup() match {
          case null => this.delegate.setSelected(true)
          case tg => tg.selectToggle(this.delegate)
        }

      }

      // Text
      //-----------
      def setText(str: String) = {
        this.delegate.setText(str)
      }
      def getText = delegate.getText()

      // ToggleGroup Support
      //----------------
      this.onWith("togglegroup") {
        tg: ToggleGroup =>
          this.delegate.setToggleGroup(tg.asInstanceOf[javafx.scene.control.ToggleGroup])
      }

    }

  }

  def toggleGroup: ToggleGroup = {
    return new javafx.scene.control.ToggleGroup with ToggleGroup {

      /**
       * Listener triggered when a selection changed in the group
       */
      override def onSelected(cl: VUIRadioButton[_] => Unit) = {

        this.selectedToggleProperty().addListener(new ChangeListener[Toggle] {

          def changed(obs: ObservableValue[_ <: javafx.scene.control.Toggle], old: Toggle, newValue: Toggle): Unit = {
            buttons.find(b => b.base == newValue) match {
              case Some(newButton) => cl(newButton)
              case None => throw new RuntimeException("Selection change in toggle group, new value did not match any VUIRadiobutton with same underlying base")
            }
            //cl(this.buttons.fin)
          }

        })

      }

      /**
       * Listener triggered when a selection changed in the group
       */
      override def onSelectedIndex(cl: Int => Unit): Unit = {
        this.selectedToggleProperty().addListener(new ChangeListener[Toggle] {

          def changed(obs: ObservableValue[_ <: javafx.scene.control.Toggle], old: Toggle, newValue: Toggle): Unit = {
            buttons.find(b => b.base == newValue) match {
              case Some(newButton) => cl(buttons.indexOf(newButton))
              case None => throw new RuntimeException("Selection change in toggle group, new value did not match any VUIRadiobutton with same underlying base")
            }
            //cl(this.buttons.fin)
          }

        })
      }

    }
  }

  /**
   * Slider
   */
  def slider: VUISlider[Node] = {
    return new JavaFXNodeDelegate(new Slider) with VUISlider[Node] {

      //base.setSnapToTicks(true)
      def setMin(min: Double) = {
        base.setMin(min)
      }
      def setMax(max: Double) = {
        base.setMax(max)
      }

      def setShowTickLabel(show: Boolean) = {
        base.setShowTickLabels(show)
      }
      def setShowTickMarks(show: Boolean) = {
        base.setShowTickMarks(show)
      }
      def setMajorTickUnit(u: Double) = {
        base.setMajorTickUnit(u)
      }
      def setMinorTickCount(u: Int) = {
        base.setMinorTickCount(u)
      }
      def setBlockIncrement(u: Double) = {
        base.setBlockIncrement(u)
      }

      // Value
      //-----------------
      def setValue(v: Double) = {
        base.setValue(v)
      }

      def onChange(cl: (Double => Unit)) = {
        base.valueProperty().addListener(new ChangeListener[Number] {

          def changed(obs: ObservableValue[_ <: Number], old: Number, newValue: Number): Unit = {
            //println(s"New Value:"+newValue.doubleValue)
            //onUIThread(cl(newValue.doubleValue))
            cl(newValue.doubleValue())

          }

        })
      }

    }
  }

  /**
   * Slider
   */
  def spinner: VUISpinner[Node] = {
    return new JavaFXNodeDelegate(new Spinner[Double](0.0, 0.0, 0.0)) with VUISpinner[Node] {

      //base.setEditable(true)

      //base.setSnapToTicks(true)
      def setMin(min: Double) = {
        base.getValueFactory.asInstanceOf[SpinnerValueFactory.DoubleSpinnerValueFactory].setMin(min)
      }
      def setMax(max: Double) = {
        base.getValueFactory.asInstanceOf[SpinnerValueFactory.DoubleSpinnerValueFactory].setMax(max)
      }
      def setStep(step: Double) = {
        base.getValueFactory.asInstanceOf[SpinnerValueFactory.DoubleSpinnerValueFactory].setAmountToStepBy(step)
      }

      // Value
      //-----------------
      def setValue(v: Double) = {
        base.getValueFactory.setValue(v.toInt)

      }

      def getValue: Double = {
        base.getValue.toDouble
      }

      def onChange(cl: (Double => Unit)) = {
        base.valueProperty().addListener(new ChangeListener[Double] {

          def changed(obs: ObservableValue[_ <: Double], old: Double, newValue: Double): Unit = {
            //println(s"New Value:"+newValue.doubleValue)
            //onUIThread(cl(newValue.doubleValue))
            cl(newValue.doubleValue())

          }

        })
      }

      //-- Automatic filter and update on key type
      /*base.getEditor.setOnKeyTyped(new EventHandler[KeyEvent] {
        def handle(e: KeyEvent) = {

          e.getCharacter match {
            case "" =>
            //case c if !(c.charAt(0).isDigit) => e.consume()
            case c if (!c.charAt(0).isLetterOrDigit) => e.consume()
            case c =>
              
              //-- Construct new value to be 
              var newvalue = base.getEditor.getText+e.getCharacter
              println(s"New valueis: "+newvalue);
              
              //-- Check it is valid 
              try {
                
                // Valid -> Plan an Enter Key
                onUIThread {
                  base.getEditor.requestFocus()
                  var r = new Robot();
                  r.keyPress(java.awt.event.KeyEvent.VK_ENTER)
                  r.keyRelease(java.awt.event.KeyEvent.VK_ENTER)
                }
                
              } catch {
                // Not valid :)
                case e : Throwable => 
                  println(s"Not valid");
              }
              e.consume()
              
              //println(s"Changing thorugh prog enter")

             /*
              
              var r = new Robot();
              r.keyPress(java.awt.event.KeyEvent.VK_ENTER)
              r.keyRelease(java.awt.event.KeyEvent.VK_ENTER)
              r.keyPress(java.awt.event.KeyEvent.VK_END)
              r.keyRelease(java.awt.event.KeyEvent.VK_END)*/
              
              // Prepar Event 
             /* var ke = new KeyEvent(KeyEvent.KEY_RELEASED, "", "", javafx.scene.input.KeyCode.ENTER, false, false, false, false)

              // Fire
              base.getEditor.fireEvent(ke)
              base.fireEvent(ke)*/

             // pressEnter
            //base.f
          }
          // cl(e.getCharacter().charAt(0))
        }
      })*/

      // Editable
      //------------------
      def setEditable(v: Boolean) = {
        base.editableProperty().setValue(v)
      }

    }
  }
}