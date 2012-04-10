package tetris3d;

/*
function mergeSort(list, comparator) {
   if (list.length < 2) {
      return list;
   }
   else if (list.length == 2) {
      // one test
      if (comparator(list[0],list[1]) <= 0)
         return list;
      else
         return [list[1],list[0]];
   } 
   else if (list.length == 3) {
      // three tests
      var a = list[0];
      var b = list[1];
      var c = list[2];
      if (comparator(a, b) > 0) {
         var temp = a;
         a = b;
         b = temp;
      }
      if (comparator(a, c) > 0) {
         var temp = a;
         a = c;
         c = temp;
      }
      if (comparator(b,c) > 0) {
         var temp = b;
         b = c;
         c = temp;
      }
      return [a,b,c];
   }
   else {
      var list1 = [];
      var list2 = [];
      for (var i = 0; i < list.length; i++) {
         if (i<list.length/2)
            list1.push(list[i]);
         else
            list2.push(list[i]);
      }
      list1 = mergeSort(list1, comparator);
      list2 = mergeSort(list2, comparator);
      return merge(list1, list2, comparator);
   }
    
}
function merge(list1, list2, comparator) {
   var result = [];
   var index1 = 0;
   var index2 = 0;
   while (index1<list1.length && index2<list2.length) {
      if (comparator(list1[index1],list2[index2]) <= 0)
         result.push(list1[index1++]);
      else
         result.push(list2[index2++]);
   }
   while (index1<list1.length)
      result.push(list1[index1++]);
   while (index2<list2.length)
      result.push(list2[index2++]);
   return result;
}

var sort = mergeSort;
*/