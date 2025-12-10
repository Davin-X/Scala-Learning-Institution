object Solution {
    def romanToInt(s: String): Int = {
        var ss= s 
        var lookup  = scala.collection.mutable.Map ("I"-> 1, "V"-> 5, "X"-> 10, "L"-> 50, "C"-> 100, "D"-> 500, "M"->1000  )
        
        ss = ss.replace("IV", "IIII").replace("IX", "VIIII")
        ss = ss.replace("XL", "XXXX").replace("XC", "LXXXX")
        ss = ss.replace("CD", "CCCC").replace("CM", "DCCCC")
   
    
    var num = 0 
    
    for (i <- ss)
    {
        num += lookup(i.toString)
    }
    
     num    
    }
}