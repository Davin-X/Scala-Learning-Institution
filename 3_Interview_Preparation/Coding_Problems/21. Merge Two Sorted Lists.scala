/**
 * Definition for singly-linked list.
 * class ListNode(_x: Int = 0, _next: ListNode = null) {
 *   var next: ListNode = _next
 *   var x: Int = _x
 * }
 */
object Solution {
    def mergeTwoLists(l1: ListNode, l2: ListNode): ListNode = {
       
      if (l1 == null || l2 == null) {
            if (l1 != null) l1 else l2
    }
    val dummy = new ListNode(-1)
    
    var prev = dummy
    var c1 = l1
    var c2 = l2
        
    while (c1 != null && c2 != null) {
      if (c1.x <= c2.x) {
        prev.next = c1
        c1 = c1.next
      } else {
        prev.next = c2
        c2 = c2.next
      }
      prev = prev.next
    }
    prev.next = if (c1 != null) c1 else c2
   
    val head = dummy.next
    dummy.next = null
   
        head
    }
}