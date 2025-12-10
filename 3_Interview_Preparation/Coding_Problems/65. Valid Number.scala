object Solution {
    def isNumber(s: String): Boolean = {
                
        var r = "^[+-]?((\\d+\\.\\d*)|(\\.\\d+)|(\\d+))([eE][+-]?\\d+)?$"
     
     if (s.matches(r) )        true
     
        else         false 
    }
}