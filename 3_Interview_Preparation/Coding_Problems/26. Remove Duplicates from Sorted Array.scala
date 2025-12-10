object Solution {
    def removeDuplicates(nums: Array[Int]): Int = {
        
    var j = 0
    if (nums.length == 0) 0
        else {
            for (i <- 1 until nums.size) {
                 if (nums(j) != nums(i)) {
                      j = j + 1
                     nums(j) = nums(i)
                 }
              }
      j + 1
    } 
        
    
    }
}