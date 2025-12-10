object Solution {
    def singleNumber(nums: Array[Int]): Int = {
        val ans = nums.groupBy(identity).map(x => (x._1,x._2.size)).filter(x => x._2 ==1)
        ans.head._1
        
    }
}