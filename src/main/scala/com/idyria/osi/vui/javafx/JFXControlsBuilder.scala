package com.idyria.osi.vui.javafx

import scala.collection.JavaConversions._
import com.idyria.osi.vui.core.components.controls.ToggleGroup
import com.idyria.osi.vui.core.components.controls.VUIRadioButton
import com.idyria.osi.vui.core.components.form.FormBuilderInterface
import com.idyria.osi.vui.core.components.form.ListBuilderInterface
import com.idyria.osi.vui.core.components.form.VUIInputText
import com.idyria.osi.vui.core.components.model.ComboBoxModel
import com.idyria.osi.vui.core.components.model.DefaultComboBoxModel
import com.idyria.osi.vui.core.components.model.ListModel
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import com.idyria.osi.vui.javafx.JavaFXRun
import com.idyria.osi.vui.javafx.model.JFXTextModelSupport
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.Toggle
import com.idyria.osi.vui.javafx.listeners.FXChangeListeners
import com.idyria.osi.vui.core.components.controls.SliderBuilderInterface
import javafx.scene.control.Slider
import com.idyria.osi.vui.core.components.controls.VUISlider
import com.idyria.osi.vui.core.components.controls.VUISpinner
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import com.idyria.osi.vui.core.components.controls.SpinnerBuilderInterface
import com.idyria.osi.vui.core.components.controls.ProgressBarBuilderInterface
import com.idyria.osi.vui.core.components.controls.VUIProgressBar
import javafx.scene.control.ProgressBar

trait JFXControlsBuilder extends ProgressBarBuilderInterface[Node] {

  
  /**
   * Slider
   */
  def progressBar: VUIProgressBar[Node] = {
    return new JavaFXNodeDelegate(new ProgressBar(0.0F)) with VUIProgressBar[Node] {

       def setProgress(p:Double) = {
         base.progressProperty().setValue(p)
       }

      def onChange(cl: (Double => Unit)) = {
        base.progressProperty().addListener(new ChangeListener[Number] {

          def changed(obs: ObservableValue[_ <: Number], old: Number, newValue: Number): Unit = {
            //println(s"New Value:"+newValue.doubleValue)
            //onUIThread(cl(newValue.doubleValue))
            cl(newValue.doubleValue())

          }

        })
      }

     

    }
  }
}