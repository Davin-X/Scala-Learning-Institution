/**
 * Definition for singly-linked list.
 * class ListNode(_x: Int = 0, _next: ListNode = null) {
 *   var next: ListNode = _next
 *   var x: Int = _x
 * }
 */
object Solution {
   def deleteDuplicates(head: ListNode): ListNode = {
    if (head == null || head.next == null)
      head
    var t = new ListNode(0)
    t.next = head
    var p = t

    while (p.next != null && p.next.next != null) {
      if (p.next.x == p.next.next.x) {
        var dup = p.next.x
        while (p.next != null && p.next.x == dup) {
          p.next = p.next.next
        }
      }

      else
        p = p.next

    }
    t.next
  }

        
}
