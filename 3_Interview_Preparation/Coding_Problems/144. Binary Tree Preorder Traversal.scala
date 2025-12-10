/**
 * Definition for a binary tree node.
 * class TreeNode(_value: Int = 0, _left: TreeNode = null, _right: TreeNode = null) {
 *   var value: Int = _value
 *   var left: TreeNode = _left
 *   var right: TreeNode = _right
 * }
 */
import scala.collection.mutable._
object Solution {
    def preorderTraversal(root: TreeNode): List[Int] = {
        
        if (root == null) return List()
        
        val stack = scala.collection.mutable.Stack[TreeNode]()
        var res = scala.collection.mutable.ListBuffer[Int]()
        
        stack.push(root)
        
        while (stack.nonEmpty) {
            val node = stack.pop
            
            if (node != null) {
                res += node.value

                stack.push(node.right)
                stack.push(node.left)
            }
        }
        
        res.toList

    }
}