package com.idyria.osi.vui.javafx.model

import com.idyria.osi.vui.core.components.model.TextModelSupport
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

trait JFXTextModelSupport extends TextModelSupport {

  // Get the text property from underlying implementation
  //--------------
  def textProperty: StringProperty

  // Listen on values for modifications
  //----------------

  /// Set to false to avoid Listener trigger loop update, when events are send from one of the listeners to the other listener
  var listenerActive = true

  textProperty.addListener(new ChangeListener[String] {

    def changed(v: ObservableValue[_ <: String], old: String, newval: String): Unit = {
    		
      if(!listenerActive)
        return
      
      // Update TextModel text
      modelImpl.setText(newval)

    }

  })

  // Update
  //------------------

  /**
   * Updated implementation text property when main model is changed
   */
  modelImpl.onWith("model.setText") {
    text: String =>
      listenerActive = false
      textProperty.setValue(text)
      listenerActive = true
  }


}