/**
 * Definition for a binary tree node.
 * class TreeNode(_value: Int = 0, _left: TreeNode = null, _right: TreeNode = null) {
 *   var value: Int = _value
 *   var left: TreeNode = _left
 *   var right: TreeNode = _right
 * }
 */
object Solution {
    def isSymmetric(root: TreeNode): Boolean = {
        
        var ans = false
        if (root == null ) ans= true
        
        if( (root.left ==null && root.right != null) || ( root.left !=null && root.right == null) )
         ans= false
        
        else 
        ans = isSymmetric( root.left,root.right)
        
        def isSymmetric(left : TreeNode ,right : TreeNode ): Boolean ={
            
            
         if ( left == null && right ==null)    return true
             else if ( left == null || right ==null) return false
             else if (left.value != right.value)    return false
            else 
             isSymmetric( left.left, right.right) && isSymmetric(right.left , left.right)
            
        }
        ans
    }
}