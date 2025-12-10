object Solution {
    def findMinFibonacciNumbers(k: Int): Int = {
        
        var fibs = List(1,1)
        while ( fibs.last  < k){
            var n = fibs(fibs.size-1) + fibs(fibs.size-2)
            fibs = fibs :+ n
        }
        var cnt =0
        var kk=k
        var last = fibs.length - 1
        while( last >= 0 && kk > 0 ){
            if(fibs(last)<= kk){
                kk = kk - fibs(last)
                cnt +=1
            }
            else 
            last -=1
        }
    return cnt 
    }
}