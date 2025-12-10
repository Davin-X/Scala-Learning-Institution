object Solution {
    def searchInsert(nums: Array[Int], target: Int): Int = {
        var c =0
        for( i <- 0 until nums.size if( nums (i) < target)  ){
            c +=1
        }
        c
    }
}