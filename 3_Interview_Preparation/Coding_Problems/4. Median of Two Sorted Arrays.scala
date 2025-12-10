object Solution {
    def findMedianSortedArrays(nums1: Array[Int], nums2: Array[Int]): Double = {
         var arr : Array[Int] = (nums1 ++ nums2).sorted
          val arraysize = arr.size 
        var ans =0.0
        if ( arraysize % 2 ==0 )
         ans = (arr(arraysize / 2 ).toFloat + arr(arraysize/2 -1 ).toFloat ) / 2 
        else 
          ans = arr(arraysize /2 ).toFloat
        
        ans    
    }
}