package com.idyria.osi.vui.javafx.tree

import com.idyria.osi.vui.core.components.controls.TreeBuilderInterface
import com.idyria.osi.vui.core.components.controls.TreeNode
import com.idyria.osi.vui.core.components.controls.VUIButton
import com.idyria.osi.vui.core.components.controls.VUITree
import com.idyria.osi.vui.core.components.scenegraph.SGNode
import com.idyria.osi.vui.core.components.table.SGTableColumn
import com.idyria.osi.vui.core.components.table.TableBuilderInterface
import com.idyria.osi.vui.javafx.JavaFXNodeDelegate
import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.event.EventHandler
import com.idyria.osi.vui.core.components.controls.DataTreeNode

trait JFXTreeBuilder extends TreeBuilderInterface[Node] {

  def tree[CT <: TreeNode]: VUITree[CT,Node] = {

    return new JavaFXNodeDelegate[TreeView[Any]](new TreeView[Any]) with VUITree[CT,Node] {

      // Root Add
      //-------------
      this.modelImpl.onWith("node.added") {
        nd: TreeNode =>

          println(s"[JFXTree] Added Root")

          // Add Root node
          var item = new TreeNodeTreeItem(nd, this)
          this.delegate.setRoot(item)

      }

      def getSelection: Iterable[TreeNode] = {
        Nil
      }

    }

  }
}

import scala.collection.JavaConversions._

/**
 * Wrapper around tree node model, to react and update the item
 */
class TreeNodeTreeItem(var treeNode: TreeNode, var tree: VUITree[_ <: TreeNode,Node]) extends TreeItem[Any] {

  this.setValue(treeNode)

  // UI Update
  //---------------

  /**
   * On Open, Update value based on closure
   */
  this.addEventHandler(TreeItem.branchExpandedEvent[Any](), new EventHandler[TreeItem.TreeModificationEvent[Any]] {

    def handle(ev: TreeItem.TreeModificationEvent[Any]) = {

      //println(s"Expaned Event")
      TreeNodeTreeItem.this.treeNode.children.foreach {
        childNode =>

      }

      TreeNodeTreeItem.this.getChildren().iterator().map(_.asInstanceOf[TreeNodeTreeItem]).foreach {
        childNode =>

          childNode.treeNode match {
            case dn: DataTreeNode[_] if (dn.data != null) =>

              TreeNodeTreeItem.this.tree.nodesViews
              TreeNodeTreeItem.this.tree.nodesViews.get(dn.data.getClass) match {
                case Some(transformation) =>
                  childNode.setValue(transformation(dn.data.asInstanceOf[Object]))
                case None =>
              }
            case _ =>
          }

      }

    }

  })

  // Add Node
  //----------------------

  //-- Add existing
  this.treeNode.children.foreach {
    child =>

      var item = new TreeNodeTreeItem(child, tree)
      this.getChildren().add(item)

  }

  //-- Listen to new nodes
  this.treeNode.onWith("node.added") {
    nd: TreeNode =>

      println(s"[JFXTree] node added")

      var item = new TreeNodeTreeItem(nd, tree)
      this.getChildren().add(item)

  }
  
  // Expand 
  //-------------------
  this.treeNode.on("expand"){
    this.setExpanded(true)
  }

}
