package com.idyria.osi.vui.javafx.chart

import com.idyria.osi.vui.javafx.JavaFXRun
import com.idyria.osi.vui.core.VBuilder
import com.idyria.osi.vui.lib.gridbuilder.GridBuilder
import com.idyria.osi.vui.lib.chart.DatasetsBuilder
import com.idyria.osi.vui.lib.chart.ChartBuilder
import com.idyria.osi.vui.javafx.JavaFXRun

object JFXChartTest extends App with GridBuilder with DatasetsBuilder with ChartBuilder {

  // Prepare Dataset
  //---------------
  var ds = timeDataset[Double]("test")

  Thread.sleep(100)
  ds <= 0

  Thread.sleep(100)
  ds <= 1

  Thread.sleep(100)
  ds <= 2

  //  ds.relativeToStartTime

  // Open GUI
  //-----------------
  var stop = false
  JavaFXRun.onJavaFX {

    var ui = frame {
      f ⇒

        f size (1024, 768)
        f title ("Chart Test")

        f <= grid {

          "-" row {
            lineChart() {
              c ⇒ c.addDataSet(ds)
            }
          }

        }

    }

    ui.show

  }
  
  // Open a Thread to add values
  //------------------
  var t = new Thread(new Runnable {
    
    
    def run = {
    
      while(!stop) {
        
        ds <= Math.random() * 10
        
        Thread.sleep(500)
        
      }
      
    }
  })
  t.setDaemon(true)
  t.start

}