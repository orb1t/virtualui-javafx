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
import com.idyria.osi.vui.lib.chart.CustomChart
import com.idyria.osi.vui.lib.chart.Dataset
import com.idyria.osi.vui.lib.chart.XYZDataSet
import javafx.collections.FXCollections
import javafx.scene.shape.Ellipse
import javafx.scene.paint.Color
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import javafx.scene.shape.Rectangle

/**
 * Implementation of Chartbuilder interface
 */
trait JFXChartBuilder extends ChartBuilderInterface[Node] with UtilsTrait {

  def lineChart: LineChart[Node] = {

    return new JavaFXNodeDelegate(new javafx.scene.chart.LineChart[Number, Number](new NumberAxis, new NumberAxis)) with LineChart[Node] {
    
     on("datasets.clean") {
       
       println(s"Remove all")
       this.delegate.getData.removeAll()
       this.delegate.getData.clear()
       
     }
      
      onWith("dataset.added") {
        ds: XYDataset[_, _] =>

          // Translation Dataset to Series
          //-------------------------
          var serie = new XYChart.Series[Number, Number]
          serie.setName(ds.name)

          //-- Add initial datas
          ds.values.foreach {

            v =>
              //onUIThread {
                serie.getData.add(new XYChart.Data(v.value._1.asInstanceOf[Number], v.value._2.asInstanceOf[Number]))
              //}

          }

          // Wait for data
          //----------------
          ds.onWith("value.added") {
            v: ValueTuple[_, _] =>
              //onUIThread {
                serie.getData.add(new XYChart.Data(v.value._1.asInstanceOf[Number], v.value._2.asInstanceOf[Number]))
              //}

          }

          // Set to Chart
          //---------------------
          this.delegate.getData().add(serie);

      }

    }

  }

  import scala.collection.JavaConversions._

  override def customChart[DT <: Dataset]: CustomChart[Node, DT] = {

    var chart = new javafx.scene.chart.XYChart[Number, Number](new NumberAxis, new NumberAxis) {

      var nodeForValue: ((Number, Number) => Option[SGNode[Node]]) = { (x, y) => None }

      var extraValue: ((Number, Number) => Option[Any]) = { (x, y) => None }

      var datas = new java.util.LinkedList[javafx.scene.chart.XYChart.Series[Number, Number]]()
      this.dataProperty().set(FXCollections.observableList[javafx.scene.chart.XYChart.Series[Number, Number]](datas))

      protected def dataItemAdded(series: javafx.scene.chart.XYChart.Series[Number, Number], itemIndex: Int, data: javafx.scene.chart.XYChart.Data[Number, Number]): Unit = {

        //println(s"Data Item added")

        // nodeForValue(data.getXValue(),data.getYValue())

        //this.getChartChildren().add(ell)

      }
      protected def dataItemChanged(x$1: javafx.scene.chart.XYChart.Data[Number, Number]): Unit = {

      }
      protected def dataItemRemoved(x$1: javafx.scene.chart.XYChart.Data[Number, Number], x$2: javafx.scene.chart.XYChart.Series[Number, Number]): Unit = {

      }
      protected def layoutPlotChildren(): Unit = {

        //println(s"PLOT")

        //this.getPlotChildren().

        this.getPlotChildren().clear()

        datas.foreach {
          serie =>
          
             var xgap = this.getXAxis().getDisplayPosition(1) - this.getXAxis().getDisplayPosition(0)
             var ygap = this.getYAxis().getDisplayPosition(0) - this.getYAxis().getDisplayPosition(1)
             println(s"Gap: $xgap $ygap")
             
            serie.getData().foreach {
              data =>
                
                var x = data.getXValue()
                var y = data.getYValue()

                

                
                
                //this.getChartChildren().add(ell)

               
                
                //-- Prepare small point
                var ell = new Rectangle(xgap, ygap)
                ell.setFill(Color.BLACK)
                
                var xpos = this.getXAxis().getDisplayPosition(x)
                ell.setLayoutX(xpos)

                var ypos = this.getYAxis().getDisplayPosition(y)
                ell.setLayoutY(ypos)

                //println(s"Data Item added: " + x + " - " + y + s" at position ($xpos:$ypos)")

                extraValue(data.getXValue(), data.getYValue()) match {
                  
                /*case Some(extra: Double) =>

                    //println(s"Using extra value: "+extra)

                    var red = (extra.toInt >> 16) & 0xFF;
                    var green = (extra.toInt >> 8) & 0xFF;
                    var blue = extra.toInt & 0xFF;

                    ell.setFill(Color.rgb(red, green, blue))*/

                  case Some((r : Int,g: Int,b: Int)) =>
                    
                    ell.setFill(Color.rgb(r, g, b))
                    
                    
                  case _ =>
                }

                this.getPlotChildren().add(ell)

            }

        }

      }
      protected def seriesAdded(series: javafx.scene.chart.XYChart.Series[Number, Number], index: Int): Unit = {

      }
      protected def seriesRemoved(series: javafx.scene.chart.XYChart.Series[Number, Number]): Unit = {

      }

    }

    return new JavaFXNodeDelegate(chart) with CustomChart[Node, DT] {

      def extraValue(cl: (Number, Number) => Option[Any]) = {
        chart.extraValue = cl
      }

      onMatch("dataset.added") {
        case ds: XYDataset[_, _] =>

          // Translation Dataset to Series
          //-------------------------
          var serie = new XYChart.Series[Number, Number]
          serie.setName(ds.name)

          //-- Add initial datas
          ds.values.foreach {

            v =>
              onUIThread {
                serie.getData.add(new XYChart.Data(v.value._1.asInstanceOf[Number], v.value._2.asInstanceOf[Number]))
              }

          }

          // Wait for data
          //----------------
          ds.onWith("value.added") {
            v: ValueTuple[_, _] =>
              onUIThread {
                serie.getData.add(new XYChart.Data(v.value._1.asInstanceOf[Number], v.value._2.asInstanceOf[Number]))
              }

          }

          // Set to Chart
          //---------------------
          this.delegate.getData().add(serie);

        case ds: XYZDataSet[_, _, _] =>

          // Translation Dataset to Series
          //-------------------------
          var serie = new XYChart.Series[Number, Number]
          serie.setName(ds.name)

          //-- Add initial datas
          ds.values.foreach {

            case ((x, y), z) =>
              onUIThread {

                serie.getData.add(new XYChart.Data(x.asInstanceOf[Number], y.asInstanceOf[Number]))
              }

          }

          // Wait for data
          //----------------
          ds.onMatch("value.added") {
            case (x, y, z) =>
              onUIThread {
                serie.getData.add(new XYChart.Data(x.asInstanceOf[Number], y.asInstanceOf[Number]))
              }

          }

          // Set to Chart
          //---------------------
          println(s"Adding to " + this.delegate + " -> " + this.delegate.dataProperty().isNull().get())
          //this.delegate.g
          this.delegate.getData().add(serie);

      }

    }

  }

}