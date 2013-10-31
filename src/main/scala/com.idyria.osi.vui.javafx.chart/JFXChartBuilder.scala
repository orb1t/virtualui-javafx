package com.idyria.osi.vui.javafx.chart

import com.idyria.osi.vui.lib.chart.ChartBuilderInterface
import javafx.scene.Node
import com.idyria.osi.vui.lib.chart.LineChart
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.NumberAxis
import com.idyria.osi.vui.lib.chart.XYDataset
import javafx.scene.chart.XYChart
import com.idyria.osi.vui.lib.chart.ValueTuple
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import com.idyria.osi.vui.core.VBuilder
import com.idyria.osi.vui.javafx.JavaFXVuiBuilder
import com.idyria.osi.vui.core.UtilsTrait



/**
 * Implementation of Chartbuilder interface
 */
trait JFXChartBuilder extends ChartBuilderInterface[Node] with UtilsTrait {

  def lineChart : LineChart[Node] = {
    
    return new JavaFXNodeDelegate(new javafx.scene.chart.LineChart[Number,Number](new NumberAxis,new NumberAxis)) with LineChart[Node] {
      
      onWith("dataset.added") {
        ds : XYDataset[_,_] => 
          
        // Translation Dataset to Series
        //-------------------------
        var serie = new XYChart.Series[Number,Number]
        serie.setName(ds.name)
        
        //-- Add initial datas
        ds.values.foreach {
          
          v => 
            onUIThread {
            serie.getData.add(new XYChart.Data(v.value._1.asInstanceOf[Number],v.value._2.asInstanceOf[Number]))
          }
           
        }
        
        // Wait for data
        //----------------
        ds.onWith("value.added") {
          v : ValueTuple[_,_] => onUIThread {
            serie.getData.add(new XYChart.Data(v.value._1.asInstanceOf[Number],v.value._2.asInstanceOf[Number]))
          }
            
            
        }
        
        // Set to Chart
        //---------------------
        this.delegate.getData().add(serie);
          
      }
      
    
      
    }
    
   
  }
  
}