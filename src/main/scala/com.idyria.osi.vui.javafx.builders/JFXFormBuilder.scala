package com.idyria.osi.vui.javafx.builders

import com.idyria.osi.vui.core.components.form.FormBuilderInterface
import com.idyria.osi.vui.javafx.model.JFXTextModelSupport
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import com.idyria.osi.vui.core.components.form.VUIInputText
import com.idyria.osi.vui.core.components.model.ComboBoxModel
import com.idyria.osi.vui.core.components.model.DefaultComboBoxModel
import com.idyria.osi.vui.core.components.model.ListModel
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.ComboBox
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import com.idyria.osi.vui.core.components.form.ListBuilderInterface
import javafx.scene.Node
import com.idyria.osi.vui.core.components.controls.VUIRadioButton
import javafx.scene.control.CheckBox
import javafx.scene.control.RadioButton
import com.idyria.osi.vui.core.components.controls.ToggleGroup
import javafx.scene.control.Toggle
import com.idyria.osi.vui.core.VUIBuilder
import com.idyria.osi.vui.javafx.JavaFXRun

trait JFXFormBuilder extends FormBuilderInterface[Node] with ListBuilderInterface[Node] {

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
        s : CT =>  model.selected = s
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

      def onSelected(cl: CT => Unit) = {

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
        str : String => JavaFXRun.onJavaFX({ (delegate.insertText(getText.length,str)) })
      }
      def textProperty = delegate.textProperty 
      
      

    }

  }
  def textInput: com.idyria.osi.vui.core.components.form.VUIInputText[javafx.scene.Node] = {

    return new JavaFXNodeDelegate[TextField](new TextField) with VUIInputText[javafx.scene.Node] with JFXTextModelSupport {

      
       
      // Columns
      //--------------------
      override def setColumns(c:Int) : Unit = {
    	  this.delegate.setPrefColumnCount(c)
      }
      
      // Text
      //-------------
      override def setText(str: String) = delegate.setText(str)
      def getText = delegate.getText

      // Model Support
      //------------
      def textProperty = delegate.textProperty

    }

  }

  def checkBox: com.idyria.osi.vui.core.components.form.VUICheckBox[javafx.scene.Node] = {
    return new JavaFXNodeDelegate[CheckBox](new CheckBox) with com.idyria.osi.vui.core.components.form.VUICheckBox[Node] {

      // State
      //--------------
      def isChecked = this.delegate.isSelected()

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
          case null =>  this.delegate.isSelected()
          case tg => tg.getSelectedToggle() == this.delegate
        }
        
      }
      
      def setChecked(b:Boolean) = {
        
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
        tg : ToggleGroup => 
          this.delegate.setToggleGroup(tg.asInstanceOf[javafx.scene.control.ToggleGroup])
      }
      
    }

  }
  
  def toggleGroup : ToggleGroup = {
    return new javafx.scene.control.ToggleGroup with ToggleGroup {
      
      /**
	   * Listener triggered when a selection changed in the group 
	   */
	  override def onSelected(cl: VUIRadioButton[_] => Unit) = {
	   
	    this.selectedToggleProperty().addListener(new ChangeListener[Toggle] {
	      
	      def changed(obs:ObservableValue[_ <: javafx.scene.control.Toggle],old:Toggle,newValue:Toggle):Unit = {
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
	  override def onSelectedIndex(cl: Int => Unit) : Unit = {
	     this.selectedToggleProperty().addListener(new ChangeListener[Toggle] {
	      
	      def changed(obs:ObservableValue[_ <: javafx.scene.control.Toggle],old:Toggle,newValue:Toggle):Unit = {
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

}