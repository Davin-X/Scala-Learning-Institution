object Solution {
    def lengthOfLastWord(s: String): Int = {
        
        if (s==" " || s.isEmpty) 0
        else 
        {
            val splitted = s.split(" ")
            
            if(splitted.isEmpty)   0
            else splitted(splitted.size-1).size
        }
        
    }
}