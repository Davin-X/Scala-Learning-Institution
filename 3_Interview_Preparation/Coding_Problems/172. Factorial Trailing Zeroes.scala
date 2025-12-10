object Solution {
    def trailingZeroes(n: Int): Int = {
        
      /* def fact(n: BigInt): BigInt = {
            if (n == 0)   1
            else  n * fact(n-1)
        }
        
        val p = fact(n).toString
        var c =0
        var l = p.size-1
        while(p(l) == '0'){
            c+=1
            l-=1
        }
        c */
        var nn=n
        var count =0 
        while(nn/5 >= 1)
        {
          count += nn/5
          nn = nn/5 
        }
        count 
    }
}