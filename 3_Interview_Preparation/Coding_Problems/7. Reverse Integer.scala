object Solution {
    def reverse(x: Int): Int = {
    var num = x
    var isNegative = false
    if (num < 0) {
        isNegative = true
        num = -num
    }
    var reverse: Long = 0
    while (num > 0) {
        reverse = reverse * 10 + num % 10
        num /= 10
    }
     if (reverse > Int.MaxValue)
        0
        else {
            
            if (isNegative)
            -reverse.toInt 
            else 
            reverse.toInt
        }
    }
}