object Solution {
    def climbStairs(n: Int): Int = {
        if (n == 0)
             1
        else{
           var lookstep = Array[Int]()
            lookstep = lookstep :+ 0 :+1
           
            for (i <- 2 to n+2 ){
                lookstep = lookstep :+  lookstep(i-1) + lookstep(i-2)
            }
        lookstep(n+1)
    }
        
        
    }
}