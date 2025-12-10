/**
 * Definition for singly-linked list.
 * class ListNode(_x: Int = 0, _next: ListNode = null) {
 *   var next: ListNode = _next
 *   var x: Int = _x
 * }
 */
object Solution {
    def addTwoNumbers(l1: ListNode, l2: ListNode): ListNode = {
         var  head :ListNode = null
         var temp :ListNode = null
         var  l11 :ListNode = l1
         var l22 :ListNode = l2
         
        var carry = 0

        while (l11 != null || l22 != null) {
           
            var sum = carry
           
            if (l11 != null) {
                sum += l11.x
                l11 = l11.next
            }
            if (l22 != null) {
                sum += l22.x
                l22 = l22.next
            }
            
            var node :ListNode = new ListNode(sum % 10)
          
            carry = sum / 10
           
            if (temp == null) {
                temp = node
                 head = node
            }
          
            else {
                temp.next = node
                temp = temp.next
            }
        }
      
        if (carry > 0) {
            temp.next = new ListNode(carry)
        }
         head
    }
    
}