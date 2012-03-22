// int | 0 will sign-extend the result from int to double
// 0xFFFFFFFF | 0 = -1
// 0xFFFFFFFF + 0 = 2^32
// equals comparisons should use (a|0) == (b|0), because this normalizes the bit represetation

function Long(low,high) {
   this.low = low | 0;
   this.high = high | 0;
}
Long.ZERO = new Long(0,0);
Long.ONE = new Long(1,0);
Long.NEGATIVE_ONE = new Long(0xFFFFFFFF, 0xFFFFFFFF);
Long.MIN_VALUE = new Long(0, 0x80000000);
Long.MAX_VALUE = new Long(0xFFFFFFFF, 0x7FFFFFFF);
Long.prototype.l2i = function() {
   return new Integer(this.low);
};
Long.prototype.l2d = function() {
   var result = 0;
   if ((this.high & 0x80000000) != 0) {
      result -= 4294967296.0 * 2147483648.0;
   }
   result += (this.high & 0x7FFFFFFF)*4294967296.0 + this.low;
};
Long.prototype.equals = function(other) {
   return this.low==other.low && this.high==other.high;
};
Long.prototype.isNegative = function() {
   return (this.high & 0x80000000) != 0;
};
Long.prototype.isZero = function() {
   return this.low==0 && this.high==0;
};
Long.prototype.isOdd = function() {
   return (this.low & 1) != 0;
};
Long.prototype.lt = function(other) {
   return this.compareTo(other) < 0;
};
Long.prototype.lte = function(other) {
   return this.compareTo(other) <= 0;
};
Long.prototype.gt = function(other) {
   return this.compareTo(other) > 0;
};
Long.prototype.gte = function(other) {
   return this.compareTo(other) >= 0;
};
Long.prototype.notEquals = function(other) {
   return this.compareTo(other) != 0;
};
Long.prototype.compareTo = function(other) {
   if (this.equals(other))
      return 0;

   var thisNeg = this.isNegative();
   var otherNeg = other.isNegative();
   if (thisNeg && !otherNeg)
      return -1;
   if (!thisNeg && otherNeg)
      return 1;

   var myhigh  = this.high  & 0x7FFFFFFF;
   var hishigh = other.high & 0x7FFFFFFF;

   if (myhigh > hishigh) {
      return 1;
   }
   else if (hishigh > myhigh) {
      return -1;
   }
   else if ((this.low  & 0x80000000) != 0 && 
            (other.low & 0x80000000) == 0) {
      return 1;
   } 
   else if ((other.low & 0x80000000) != 0 &&
            (this.low  & 0x80000000) == 0) {
      return -1;
   } else {
      var mylow  = this.low  & 0x7FFFFFFF;
      var hislow = other.low & 0x7FFFFFFF;
      return (mylow > hislow) ? 1 : -1;
   }
};
Long.prototype.not = function() {
   return new Long(this.low ^ 0xFFFFFFFF, this.high ^ 0xFFFFFFFF);
};
Long.prototype.inc = function() {
   if (this.low == 0xFFFFFFFF) {
      if (this.high == 0xFFFFFFFF) {
         return Long.ZERO;
      } else {
         return new Long(0, this.high+1);
      }
   } else {
      return new Long(this.low+1, this.high);
   }
};
Long.prototype.negate = function() {
   return this.not().inc();
};
Long.prototype.add = function(other) {
   var a48 = (this.high >>> 16) & 0xFFFF;
   var a32 = this.high & 0xFFFF;
   var a16 = (this.low >>> 16) & 0xFFFF;
   var a00 = this.low & 0xFFFF;
   
   var b48 = (other.high >>> 16) & 0xFFFF;
   var b32 = other.high & 0xFFFF;
   var b16 = (other.low >>> 16) & 0xFFFF;
   var b00 = other.low & 0xFFFF;

   var c48 = 0, c32 = 0, c16 = 0, c00 = 0;
   c00 += a00 + b00;
   c16 += (c00 >>> 16) & 0xFFFF;
   c00 &= 0xFFFF;
   c16 += a16 + b16;
   c32 += (c16 >>> 16) & 0xFFFF;
   c16 &= 0xFFFF;
   c32 += a32 + b32;
   c48 += (c32 >>> 16) & 0xFFFF;
   c32 &= 0xFFFF;
   c48 += a48 + b48;
   c48 &= 0xFFFF;
   
   return new Long(c00 | (c16 << 16), c32 | (c48 << 16));
};
Long.prototype.subtract = function(other) {
   return this.add(other.negate());
};
Long.prototype.and = function(other) {
   return new Long(this.low & other.low, this.high & other.high);
};
Long.prototype.or = function(other) {
   return new Long(this.low | other.low, this.high | other.high);
};
Long.prototype.xor = function(other) {
   return new Long(this.low ^ other.low, this.high ^ other.high);
};
Long.prototype.shl = function(amt) {
   // amt is an Integer
   amt = amt.toJS() & 63;
   if (amt == 0) {
      return this;
   } else if (amt >= 32) {
      var newhigh = this.low << (amt-32);
      return new Long(0, newhigh & 0xFFFFFFFF);
   } else {
      var newlow = (this.low << amt) & 0xFFFFFFFF;
      var newhigh = ((this.high << amt) | (this.low >>> (32-amt))) & 0xFFFFFFFF;
      return new Long(newlow, newhigh);
   }
};
Long.prototype.ushr = function(amt) {
   // amt is an Integer
   amt = amt.toJS() & 63;
   if (amt == 0) {
      return this;
   } else if (amt >= 32) {
      var newhigh = 0;
      var newlow = (this.high >>> (amt-32)) & 0xFFFFFFFF;
      return new Long(newlow, newhigh);
   } else { 
      var newhigh = (this.high >>> amt) & 0xFFFFFFFF;
      var newlow = (this.low >>> amt) | (this.high << (32-amt));
      return new Long(newlow & 0xFFFFFFFF, newhigh);
   }
};
Long.prototype.shr = function(amt) {
   // amt is an Integer
   amt = amt.toJS() & 63;
   if (amt == 0) {
      return this;
   } else if (amt >= 32) {
      var newhigh = (this.isNegative() ? 0xFFFFFFFF : 0);
      var newlow;
      if (this.isNegative())
         newlow = (this.high >>> (amt-32)) | (0xFFFFFFFF << (32-(amt-32)));
      else
         newlow = (this.high >>> (amt-32));
      return new Long(newlow & 0xFFFFFFFF, newhigh);
   } else { 
      var newhigh;
      if (this.isNegative())
         newhigh = (this.high >>> amt) | (0xFFFFFFFF << (32-amt));
      else
         newhigh = (this.high >>> amt);
      var newlow = (this.low >>> amt) | (this.high << (32-amt));
      return new Long(newlow & 0xFFFFFFFF, newhigh & 0xFFFFFFFF);
   }
};
Long.prototype.multiply = function(other) {
   // shift-and-add
   var result = Long.ZERO;
   var addend = this;
   for (var i = 0; i < 32; i++) {
      if ((other.low & (1<<i)) != 0) {
         result = result.add(addend);
      }
      addend = addend.shl(Integer.ONE);
   }
   for (var i = 0; i < 32; i++) {
      if ((other.high & (1<<i)) != 0) {
         result = result.add(addend);
      } 
      addend = addend.shl(Integer.ONE);
   }
   return result;
};
Long.prototype.divmod = function(other) {
   Util.assertWithException(!other.equals(Long.ZERO), Util.resolveClass("java.lang.ArithmeticException"));

   if (other.equals(Long.MIN_VALUE)) {
      if (this.equals(other)) {
         return {div:Long.ONE, mod:Long.ZERO};
      } else {
         return {div:Long.ZERO, mod:this};
      }
   }

   if (this.isNegative()) {
      if (other.lt(this) ||
          (!other.isNegative() && other.negate().lt(this))) {
         return {div:Long.ZERO, mod:this};
      }
   } else {
      if (other.gt(this) ||
          (other.isNegative() && other.negate().gt(this))) {
         return {div:Long.ZERO, mod:this};
      }
   }

   var div;
   var prod;
   function iterate(updater, toofar) {
      var newdiv = updater(div);
      var newprod = newdiv.multiply(other);
      if (toofar(newprod))
         return false;
      prod = newprod;
      div = newdiv;
      return true;
   }

   var bigupdater = function(guess) {
      return guess.shl(Integer.ONE);
   };
   var littleupdater;
   var toofar;
   var that = this;

   if (this.isNegative()) {
      toofar = function(prod) {
         return !prod.isNegative() || prod.lt(that);
      };
      if (other.isNegative()) {
         div = Long.ONE;
         littleupdater = function(guess) {
            return guess.inc();
         };
      } else {
         div = Long.NEGATIVE_ONE;
         littleupdater = function(guess) {
            return guess.add(Long.NEGATIVE_ONE);
         };
      }
   } else {
      toofar = function(prod) {
         return prod.isNegative() || prod.gt(that);
      };
      if (other.isNegative()) {
         div = Long.NEGATIVE_ONE;
         littleupdater = function(guess) {
            return guess.add(Long.NEGATIVE_ONE);
         };
      } else {
         div = Long.ONE;
         littleupdater = function(guess) {
            return guess.inc();
         };
      }
   }
   prod = div.multiply(other);

   while (iterate(bigupdater, toofar)) {}
   while (iterate(littleupdater, toofar)) {}

   return {div:div, mod:this.subtract(prod)};
};

Long.prototype.divmodCPS = function(other, cont, exc) {
   var that = this;
   return function() {
      function cont2() {
         if (other.equals(Long.MIN_VALUE)) {
            if (this.equals(other)) {
               return {div:Long.ONE, mod:Long.ZERO};
            } else {
               return {div:Long.ZERO, mod:this};
            }
         }

         if (this.isNegative()) {
            if (other.lt(this) ||
                (!other.isNegative() && other.negate().lt(this))) {
               return {div:Long.ZERO, mod:this};
            }
         } else {
            if (other.gt(this) ||
                (other.isNegative() && other.negate().gt(this))) {
               return {div:Long.ZERO, mod:this};
            }
         }

         var div;
         var prod;
         function iterate(updater, toofar) {
            var newdiv = updater(div);
            var newprod = newdiv.multiply(other);
            if (toofar(newprod))
               return false;
            prod = newprod;
            div = newdiv;
            return true;
         }

         var bigupdater = function(guess) {
            return guess.shl(Integer.ONE);
         };
         var littleupdater;
         var toofar;
         var that = this;

         if (this.isNegative()) {
            toofar = function(prod) {
               return !prod.isNegative() || prod.lt(that);
            };
            if (other.isNegative()) {
               div = Long.ONE;
               littleupdater = function(guess) {
                  return guess.inc();
               };
            } else {
               div = Long.NEGATIVE_ONE;
               littleupdater = function(guess) {
                  return guess.add(Long.NEGATIVE_ONE);
               };
            }
         } else {
            toofar = function(prod) {
               return prod.isNegative() || prod.gt(that);
            };
            if (other.isNegative()) {
               div = Long.NEGATIVE_ONE;
               littleupdater = function(guess) {
                  return guess.add(Long.NEGATIVE_ONE);
               };
            } else {
               div = Long.ONE;
               littleupdater = function(guess) {
                  return guess.inc();
               };
            }
         }
         prod = div.multiply(other);

         while (iterate(bigupdater, toofar)) {}
         while (iterate(littleupdater, toofar)) {}

         var result = {div:div, mod:this.subtract(prod)};
         return cont(result);
      }
      return Util.assertWithException(!other.equals(Long.ZERO), "java.lang.ArithmeticException", cont2, exc);
   };
};

Long.prototype.toString = function() {
   if (this.high == 0) {
      return new Integer(this.low).toString();
   } else if ((this.high == (0 | 0xFFFFFFFF)) && 
              ((this.low & 0x80000000) != 0)) {
      return new Integer(this.low).toString();
   } else {
      return "<large long value>";
   }
};
