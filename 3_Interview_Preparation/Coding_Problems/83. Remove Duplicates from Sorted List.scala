/**
 * Definition for singly-linked list.
 * class ListNode(_x: Int = 0, _next: ListNode = null) {
 *   var next: ListNode = _next
 *   var x: Int = _x
 * }
 */
object Solution {
    def deleteDuplicates(head: ListNode): ListNode = {
       
        if(head == null || head.next == null) 
            head
        
        var curr = head
        
        while(curr != null && curr.next != null){
            if( curr.x == curr.next.x)
                curr.next = curr.next.next

            else             
                curr = curr.next
        }
       head
    }
}