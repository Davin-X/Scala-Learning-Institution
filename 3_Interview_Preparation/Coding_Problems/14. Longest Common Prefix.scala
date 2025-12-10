import scala.util.control.Breaks._
object Solution {
    def longestCommonPrefix(strs: Array[String]): String = {
     
        var ans = ""
        breakable{
        for (i <- strs (0)){
            if (strs.forall(x => x.startsWith(ans+i)) )
            {
                ans = ans+i
            }
            else break
        }
     
        }
          ans
    }
}