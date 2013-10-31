package com.idyria.osi.vui.javafx

import com.idyria.osi.vui.core.components.VUIComponent
import javafx.scene.Node
import com.idyria.osi.vui.core.components.events.VUIDragEvent
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import com.idyria.osi.vui.core.components.events.VUIMouseEvent
import com.idyria.osi.vui.core.components.events.VUIClickEvent
import javafx.scene.input.MouseEvent
import javafx.scene.Group
import scala.collection.JavaConversions
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.layout.Pane



class JavaFXNodeDelegate[DT <: Node](var delegate: DT) extends VUIComponent[Node] {

  //---------------------------------------
  // Node
  //---------------------------------------

  /**
   * Name maps to ID
   */
  override def setName(str: String) = {
    //super.setName(str)
    delegate.setId(str)
  }

  def base: DT = delegate
  override def revalidate = delegate match {
    case g: Group => g.requestLayout()
    case _        =>
  }

  def clear = delegate match {
    case g: Group => g.getChildren().clear()
    case p: Pane => p.getChildren().clear()
    case _        =>
  }

  /**
   *
   */
  def removeChild(c: SGNode[_]) = delegate match {
    case g: Group => g.getChildren().remove(c.base)
    case _        =>
  }

  /**
   * Do not return anything
   */
  def children: Seq[SGNode[Node]] = delegate match {
    case g: Group => g.getChildren().toArray().map(c => new JavaFXNodeDelegate[Node](c.asInstanceOf[Node]))
    case _        => Nil
  }

  //---------------------------------------
  // General
  //---------------------------------------
  
  //-- Enable / Disable
  
  override def disable = delegate.setDisable(true)
  override def enable = delegate.setDisable(false)

  //-- Visibility
  override def setVisible(state: Boolean) : Unit = {
    delegate.setVisible(state)
    
  }
  
  //---------------------------------------
  // Actions
  //---------------------------------------

  override def onMousePressed(action: VUIMouseEvent => Unit) = {

    /*delegate.addMouseListener(new MouseAdapter() {

      override def mousePressed(e: MouseEvent) = {
        action(populateVUIMouseEvent(e, new VUIMouseEvent))
      }

    })*/

  }

  override def onDrag(action: VUIMouseEvent => Unit) = {

    /* delegate.addMouseMotionListener(new MouseMotionAdapter() {

      override def mouseDragged(ev: MouseEvent) = {
        action(populateVUIMouseEvent(ev, new VUIMouseEvent))
      }

      /*override def mouseDrag(evt, x, y) = {

          }*/

    })*/

  }

  override def onClicked(action: VUIClickEvent => Any) = {
	  
    delegate.setOnMouseClicked(new EventHandler[MouseEvent] {
    	def handle(event: MouseEvent) = {
    	  action(event)
    	}
    })
    
    
  } /*delegate.addMouseListener(new MouseAdapter() {
   
    /*override def mouseClicked(e: MouseEvent) = SwingUtilities.invokeLater(new Runnable {

      override def run() = action(e)
    })*/
  
  })*/

  //----------------------
  //-- Geometry listeners
  override def onShown(action: => Unit) = {

    var wrapper: (() => Unit) = {
      () => action
    }

    /* delegate.addComponentListener(new ComponentAdapter() {

      override def componentShown(e: ComponentEvent) = wrapper()
      override def componentResized(e: ComponentEvent) = wrapper()
    })

    delegate.addAncestorListener(new AncestorListener() {

      var wrapper: (() => Unit) = {
        () => action
      }

      override def ancestorAdded(e: AncestorEvent) = wrapper()
      override def ancestorMoved(e: AncestorEvent) = {}
      override def ancestorRemoved(e: AncestorEvent) = {}

    })*/

  }

  //---------------------------------------
  // Positioning
  //---------------------------------------
  def setPosition(x: Int, y: Int) = { delegate.setLayoutX(x); delegate.setLayoutY(y) }
  def getPosition: Pair[Int, Int] = Pair[Int, Int](delegate.getLayoutX().toInt, delegate.getLayoutY().toInt)

  //----------------------
  // Styling
  //----------------------

  def size(width: Int, height: Int) = {
    delegate.prefWidth(width)
    delegate.prefHeight(height)
  }

  // Conversions
  //-----------------------

  //-- Convert component events
  //-----------------------
  def convertMouseEventToVUIDragEvent(ev: MouseEvent): VUIDragEvent = {
    this.populateVUIMouseEvent[VUIDragEvent](ev, new VUIDragEvent)
  }

  //-- Mouse Events
  //-----------------------
  implicit def convertMouseEventToVUIMouseEvent(ev: MouseEvent): VUIMouseEvent = {
    this.populateVUIMouseEvent[VUIMouseEvent](ev, new VUIMouseEvent)
  }

  implicit def convertMouseEventToClickEvent(ev: MouseEvent): VUIClickEvent = {

    // Common
    var click = this.populateVUIMouseEvent[VUIClickEvent](ev, new VUIClickEvent)

    // Click
    click.clickCount = ev.getClickCount()
    click
  }

  private def populateVUIMouseEvent[ET <: VUIMouseEvent](srcEvent: MouseEvent, targetEvent: ET): ET = {

    // Fill in positions
    //-----------
    targetEvent.actualX = srcEvent.getX().toInt
    targetEvent.actualY = srcEvent.getY().toInt

    // Click counts and button
    //-------------------

    targetEvent
  }

  //override def toString = delegate.toString

}