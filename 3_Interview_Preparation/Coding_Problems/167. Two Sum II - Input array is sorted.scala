object Solution {
    def twoSum(numbers: Array[Int], target: Int): Array[Int] = {
        
        var ans = Array[Int]()
        var numss = numbers 
        for(i <- 0 until numbers.size ){
            numss = numss.tail
            val p = target - numbers(i)
             if (numss.contains(p))
            {
                val v = numbers.lastIndexOf(p)
                ans = Array(i+1,v+1)
            }  
           
        }
        ans 
    }
}