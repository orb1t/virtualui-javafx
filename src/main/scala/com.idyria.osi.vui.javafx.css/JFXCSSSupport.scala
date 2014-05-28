package com.idyria.osi.vui.javafx.css

import com.idyria.osi.vui.core.styling.CSSStylable
import javafx.scene.Node
import com.idyria.osi.vui.core.components.scenegraph.SGNode


trait JFXCSSSupport extends CSSStylable {
  
  // Implementation
  //-----------------------------
  
  override def cssProperty(p:String,v:String) = {
    
    var node = this.asInstanceOf[SGNode[_]].base.asInstanceOf[Node]
    
    var currentStyle = node.getStyle
    
    println(s"------ CSS PROP SET : $p -> $v")
    
    // Property transformation for Idiotic JFX parameters
    //---------------
    var propertyName = p match {
      case "color" => "text-fill"
      case _ => p
    }
    
    // Update
    //-------------------
    node.setStyle(currentStyle+s";-fx-$p: $v")
    
  }
  
  //-- Fonts
  override def cssFontSize(s: Float) = { 
    
    logFine("[JFXCSS] Change Font Size of current element")
    
  }
  
}