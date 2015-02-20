package com.idyria.osi.vui.javafx

import com.idyria.osi.vui.core.VBuilder
import com.idyria.osi.vui.core.VUIBuilder
import javafx.application.Platform
import com.sun.javafx.tk.Toolkit
import javafx.application.Application
import javafx.stage.Stage
import java.util.concurrent.Semaphore
import com.idyria.osi.vui.lib.gridbuilder.GridBuilder

class JavaFXRun extends Application {

    var cl: () ⇒ Unit = { () ⇒ }

    def start(stage: Stage) = {
        stage.close()
        cl()

        // Select VUI implementation for JFX thread
        //---------------
        VUIBuilder.setImplementationForCurrentThread(new JavaFXVuiBuilder)

        JavaFXRun.semaphore.release
        
        //stage.show()
    }

}

object JavaFXRun {

    var semaphore = new Semaphore(0)
    var started = false
    var applicationThread: Option[Thread] = None

    VUIBuilder.setImplementationForCurrentThread(new JavaFXVuiBuilder)
    VUIBuilder.defaultImplementation = new JavaFXVuiBuilder

    def onJavaFX(cl: ⇒ Unit) = {

        started match {
            case true ⇒

                Platform.runLater(new Runnable() {
                    def run = {
                        try { cl } finally { semaphore.release }

                    }
                })

            // No grants in semaphore, start application
            case false ⇒

                /*applicationThread = Some(new Thread(new Runnable() {
          def run = {
            
            try {Application.launch(classOf[JavaFXRun])} finally {semaphore.release}
      
          }
        }))
        applicationThread.get.start

        // Wait started
        semaphore.acquire()*/

                var fxThread = new Thread(new Runnable() {
                    def run = {

                        try { Application.launch(classOf[JavaFXRun]) } finally {  }

                    }
                })
                fxThread.start()
                
                semaphore.acquire()
    
                started = true;
                Platform.runLater(new Runnable {

                    def run = {
                        try { cl } finally { semaphore.release }
                    }
                })
                // Acquire a semaphore to wait for the end of execution
                semaphore.acquire()

        }

        

        /* // Check Java FX has been started
    //----------
    //var d = new DummyApplication
    applicationThread match {
      case Some(appThread) ⇒

       
        
      
        Platform.runLater(new Runnable() {
          def run = {
              try {cl} finally {semaphore.release}
            
          }
        })

      // No grants in semaphore, start application
      case None ⇒

        /*applicationThread = Some(new Thread(new Runnable() {
          def run = {
            
            try {Application.launch(classOf[JavaFXRun])} finally {semaphore.release}
      
          }
        }))
        applicationThread.get.start

        // Wait started
        semaphore.acquire()*/
        

        Platform.runLater(new Runnable {

          def run = {
             try {cl} finally {semaphore.release}
          }
        })

    }
    
    // Acquire a semaphore to wait for the end of execution
    semaphore.acquire()*/

    }

}
