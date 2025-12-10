object Solution {
    def isPalindrome(s: String): Boolean = {
        
         val ans = s.replaceAll("[^a-zA-Z0-9]","").toLowerCase
         ans == ans.reverse
        
    }
}