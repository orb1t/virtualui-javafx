package com.idyria.osi.vui.javafx

import com.idyria.osi.vui.core.VBuilder
import com.idyria.osi.vui.core.VUIBuilder
import javafx.application.Platform
import com.sun.javafx.tk.Toolkit
import javafx.application.Application
import javafx.stage.Stage
import java.util.concurrent.Semaphore
import com.idyria.osi.vui.lib.gridbuilder.GridBuilder

/**
 *
 */
object JavaFXFirstSimpleApp extends App with GridBuilder {

 
  println("Welcome to Java FX Example")

  JavaFXRun.onJavaFX {

    var ui = frame {
      f =>
        f.size(800, 600)
        f title ("Hello World application")

 
        
        f <= grid {
          
          "-" row {
            
            label("Test Label") | button("Test Button")
            
          }
        }

    }
    ui.show

  }

  /*  var d = new DummyApplication
  d.init()
 
  
  
  Application.launch(classOf[DummyApplication])
 println("After launch")
 Platform.runLater(new Runnable {

    def run = {

      println("Shwoing stage")
      
      var ui = frame {
        f =>
          f.size(800,600)
          f title("Hello World application")

          f <= label("Test Label")
          f <= button("Test Button")
          
      }
      ui.show

    }

  })*/

  //Application.launch(classOf[JavaFXFirstSimpleApp])
  /*println("Welcome to Java FX Example")

  VUIBuilder.actualImplementation = new JavaFXVuiBuilder

 
  
  Toolkit.getToolkit().init()
 
  //Toolkit.getToolkit().addShutdownHook(Toolkit.getToolkit().exit())
  Toolkit.getToolkit().startup(new Runnable {

    def run = {

      var ui = frame {
        f =>
          f.size(800,600)
          f title("Hello World application")

          f <= label("Test Label")
          f <= button("Test Button")
          
      }
      ui.show

    }

  })*/
  /*
  Platform.runLater(new Runnable {

    def run = {

      var ui = frame {
        f =>

      }
      ui.show

    }

  })*/

  /*  def start(stage: Stage) = {

    //stage.close()
    
    println("Welcome to Java FX Example")

    VUIBuilder.actualImplementation = new JavaFXVuiBuilder

    var ui = frame {
      f =>
        f.size(800, 600)
        f title ("Hello World application")

        f <= label("Test Label")
        f <= button("Test Button")

    }
    ui.show

  }*/

}