object Solution {
    def plusOne(digits: Array[Int]): Array[Int] = {
        val n= digits.size
        var arr =digits
        var tmp = 0
        var carry =0
        for (i <- n-1 to 0 by -1)
        {
        
            if (i == n -1 )
             tmp  =arr(i) + 1 + carry
            else 
             tmp = arr(i) + carry 

            
         if (tmp >= 10 && i==0)
            {
                arr(i) = tmp % 10 
                carry = tmp / 10 
                arr = carry +: arr
            }
            else if (tmp >= 10 )
            {
                 arr(i) = tmp % 10
                 carry= tmp / 10 
            } 
           
            else 
            {
                carry = 0
                 arr(i) = tmp
            }
               
            
            
        }
        
        arr
    }
}