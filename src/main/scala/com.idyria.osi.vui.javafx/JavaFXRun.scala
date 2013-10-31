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

    JavaFXRun.semaphore.release()
    //stage.show()
  }

} 

object JavaFXRun {

  var semaphore = new Semaphore(0)

  def onJavaFX(cl: ⇒ Unit) = {

    // Check Java FX has been started
    //----------
    //var d = new DummyApplication
    semaphore.tryAcquire() match {
      case true ⇒

        semaphore.release

        Platform.runLater(new Runnable() {
          def run = {
           cl
          }
        })

      // No grants in semaphore, start application
      case false ⇒

        new Thread(new Runnable() {
          def run = {
            Application.launch(classOf[JavaFXRun])
          }
        }).start()

        // Wait started
        semaphore.acquire()
        semaphore.release()

        Platform.runLater(new Runnable {

          def run = {
            cl
          }
        })

    }

  }

}
