
object Solution {
    def twoSum(nums: Array[Int], target: Int): Array[Int] = {
        
       /* var ans = Array[Int]()
        var numss = nums 
        for(i <- 0 until nums.size ){
            numss = numss.tail
            val p = target - nums(i)
             if (numss.contains(p))
            {
                val v = nums.lastIndexOf(p)
                ans = Array(i,v)
            }  
           
        }
        ans */
        
       var start =0 ; var end = nums.length -1 
        
        var ans = Array[Int]();    var tmp = nums.sorted
        
    while (start < end){
           
            if ( tmp(start) + tmp(end) == target ){
                 return  Array(nums.indexOf(tmp(start) ) , nums.lastIndexOf(tmp(end) )    )
            }
             
             if(tmp(start) + tmp(end) < target)
               start +=1
            else end -=1
        }
        Array[Int]()
    }
}