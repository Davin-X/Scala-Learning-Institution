object Solution {
    // you need treat n as an unsigned value
    def reverseBits(n: Int): Int = {
        var reversed = 0
        var nn = n
        for (i <- 0.until(32)) {
          reversed += nn & 1
          nn >>= 1
          if (i < 31) {
            reversed <<= 1
          }
        }
        reversed
}
}