object Solution {
    // you need treat n as an unsigned value
    def hammingWeight(n: Int): Int = {
        var x= n 
        var count = 0
        var m = 1
        for (i <- 0 to 32 )
        {
           if ((x & m) != 0) {
                count += 1 
            }
            m = m << 1 
        }
        count 
    }
}
