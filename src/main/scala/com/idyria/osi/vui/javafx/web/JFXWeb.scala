package com.idyria.osi.vui.javafx.web

import com.idyria.osi.vui.javafx.JavaFXNodeDelegate

trait WebBrowser {
  
  
  def loadContent(str:String)
}

trait JFXWeb {
  
  
  def browser  = {
    
    
     var w = new JavaFXNodeDelegate(new javafx.scene.web.WebView()) with WebBrowser {
       
        def loadContent(str:String) = {
          base.getEngine.loadContent(str)
        }
       
     }
     
     
     w
  }
  
  
}