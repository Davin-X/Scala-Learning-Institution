object Solution {
    def removeElement(nums: Array[Int], v: Int): Int = {
        
        var c = 0
        for (i <- 0 until nums.length) {
             if (nums(i) != v ){
                  nums( c ) = nums(i)
                  c += 1
             }
            
          }
      c
    }
}