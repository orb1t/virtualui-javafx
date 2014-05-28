package com.idyria.osi.vui.javafx.svg

import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import javafx.scene.shape.SVGPath
import javafx.scene.Group
import javafx.scene.layout.Pane
import javafx.scene.transform.Translate
import javafx.scene.control.Control
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.shape.FillRule
import javafx.scene.transform.Scale
import java.net.URL
import scala.io.Source

class JFXSVGPath(

    /**
     * The SVG Path string
     */
    var path: String) extends JavaFXNodeDelegate(new Canvas) {

  // Alternate constructors
  //--------------
  def this(url: URL) = this(Source.fromURL(url, "UTF-8").mkString)

  // SVG Path
  //------------------

  // USe SVG Path to detect the original size
  var svgPath = new SVGPath
  svgPath.setContent(path)

  /**
   * Change the SVG Path to be drawn
   */
  def setSVGPath(str: String): Unit = {
    path = str
    svgPath.setContent(path)
    repaint
  }

  /**
   * Change the SVG Path to be drawn
   */
  def setSVGPath(url: URL): Unit = setSVGPath(Source.fromURL(url, "UTF-8").mkString)

  //-- Size
  this.delegate.setWidth(svgPath.boundsInLocalProperty().get().getWidth())
  this.delegate.setHeight(svgPath.boundsInLocalProperty().get().getHeight())

  var scaleX = 1.0
  var scaleY = 1.0

  // Paint Customisation
  //----------------
  var fillColor = Color.BLACK

  /**
   * Scale SVG and update group size
   */
  def changeSize(targetWidth: Double, targetHeight: Double): JFXSVGPath = {

    // println("Changing size of actual:" + svgPath.boundsInLocalProperty().get())

    var actualWidth = svgPath.boundsInLocalProperty().get().getMaxX()
    var actualHeight = svgPath.boundsInLocalProperty().get().getMaxY()

    // Change Size
    //----------------

    // Get Scaling for painted path
    //-------------
    scaleX = targetWidth / actualWidth
    scaleY = targetHeight / actualHeight
    this.delegate.setWidth(targetWidth)
    this.delegate.setHeight(targetHeight)
    
    repaint
    //println("Changing size of actual:" + this.delegate.boundsInLocalProperty().get())

    this
  }

  private def repaint = {

    // Clear and Paint
    //----------------
    var g2 = this.delegate.getGraphicsContext2D()
    g2.clearRect(0, 0, this.delegate.getWidth(), this.delegate.getHeight())
    
    // Paint path
    //---------------
    g2.setFill(fillColor)
    g2.setStroke(fillColor)
    g2.setLineWidth(1)

    g2.beginPath()
    //g2.moveTo(-1,-1)

    //-- scale
    g2.scale(scaleX, scaleY)
    g2.translate(-svgPath.boundsInLocalProperty().get().getMinX / 2, -svgPath.boundsInLocalProperty().get().getMinY / 2)
    //var sc = new Scale(scaleX, scaleY)
    //g2.transform(sc.getMxx(),sc.getMyx(),sc.getMxy(),sc.getMyy,sc.getMxz(),sc.getMyz())

    g2.appendSVGPath(path)

    g2.fill
    g2.closePath

  }

}


