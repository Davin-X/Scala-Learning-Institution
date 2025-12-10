/**
 * Definition for singly-linked list.
 * class ListNode(var _x: Int = 0) {
 *   var next: ListNode = null
 *   var x: Int = _x
 * }
 */

object Solution {
    def hasCycle(head: ListNode): Boolean = {
        
        if (head ==null)  return false
        
        var pre = head 
        var curr = head.next 
        
        while ( pre != curr ){
            if ( curr == null || curr.next == null )
              return false 
            
             pre = pre.next 
             curr = curr.next.next
        }
        true 
    }
}