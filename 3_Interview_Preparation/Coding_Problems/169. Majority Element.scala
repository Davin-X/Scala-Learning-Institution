object Solution {
    def majorityElement(nums: Array[Int]): Int = {
        
       val ans =  nums.groupBy(identity).map(x => (x._1,x._2.size)).maxBy(_._2)
        ans._1
    }
}