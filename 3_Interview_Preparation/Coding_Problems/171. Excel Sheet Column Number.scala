object Solution {
    def titleToNumber(columnTitle: String): Int = {
        
        var ans =0
        for (i <- columnTitle){
            
            ans = ans *26 +(i-'A' + 1 )
        }
        ans
    }
}