package com.idyria.osi.vui.javafx.chart

import com.idyria.osi.vui.javafx.JavaFXRun
import com.idyria.osi.vui.core.VBuilder
import com.idyria.osi.vui.lib.gridbuilder.GridBuilder
import com.idyria.osi.vui.lib.chart.DatasetsBuilder
import com.idyria.osi.vui.lib.chart.ChartBuilder
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.vui.lib.chart.TimeValuesDataset
import com.idyria.osi.tea.logging.TLog
import com.idyria.osi.ooxoo.core.buffers.structural.VerticalBuffer

object JFXChartTest extends App with GridBuilder with DatasetsBuilder  {

  var fromXML = """<?xml version="1.0" ?>
    <TimeValuesDataset name="TestXML" startTime="1390410600623" xType="scala.Long" yType="scala.Long">
    <Value x="1">0</Value><Value x="101">1</Value>
    
    <Value x="201">2</Value>
    </TimeValuesDataset>"""
  
    //TLog.setLevel(classOf[VerticalBuffer], TLog.Level.FULL)
    //TLog.setLevel(classOf[StAXIOBuffer], TLog.Level.FULL)
    
  
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
          
          "second" row {
            
            lineChart {
              c  => 
                
                // Parse
                var io = StAXIOBuffer(fromXML)
                var tds = new TimeValuesDataset[Long]()
                tds.appendBuffer(io)
                io.streamIn
                
                println(s"Done Dataset: "+tds.values.size)
                tds.values.foreach {
                  tuple => 
                    println(s"--> "+tuple.x+":"+tuple.y)
                }
                
                c.addDataSet(tds)
            }
          }

        }

    }

    ui.show

  }
  
  // Open a Thread to add values
  //------------------
  /*var t = new Thread(new Runnable {
    
    
    def run = {
    
      while(!stop) {
        
        ds <= Math.random() * 10
        
        Thread.sleep(500)
        
      }
      
    }
  })
  t.setDaemon(true)
  t.start*/

}