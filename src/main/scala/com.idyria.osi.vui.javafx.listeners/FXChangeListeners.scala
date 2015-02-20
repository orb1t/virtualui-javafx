package com.idyria.osi.vui.javafx.listeners

import javafx.beans.value.ObservableValue
import javafx.beans.value.ChangeListener




object FXChangeListeners {
    
    implicit def closureToChange[T](cl : T => Unit) = {
        
        new ChangeListener[T] {
          def changed(o : ObservableValue[_ <: T] , old : T, newVal : T) = {
              if (old!=newVal) {
                  cl(newVal)
              }
              
          }
      }
        
    }     
    
}