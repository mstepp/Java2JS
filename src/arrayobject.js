function PrimitiveType(which, name) {
   this.which = which;
   this.classname = name;
}
PrimitiveType.prototype.equals = function(other) {
   return other.isPrimitive() && other.which == this.which;
};
PrimitiveType.prototype.toString = function() {
   return "<type " + this.classname + ">";
};
PrimitiveType.prototype.isArray = Util.FALSE;
PrimitiveType.prototype.isPrimitive = Util.TRUE;
PrimitiveType.BOOLEAN = new PrimitiveType(0, "boolean");
PrimitiveType.BYTE = new PrimitiveType(1, "byte");
PrimitiveType.CHAR = new PrimitiveType(2, "char");
PrimitiveType.DOUBLE = new PrimitiveType(3, "double");
PrimitiveType.FLOAT = new PrimitiveType(4, "float");
PrimitiveType.INT = new PrimitiveType(5, "int");
PrimitiveType.LONG = new PrimitiveType(6, "long");
PrimitiveType.SHORT = new PrimitiveType(7, "short");
PrimitiveType.VOID = new PrimitiveType(8, "void");

///////////////////////////////////////////

function ArrayType(elementType) {
   this.elementType = elementType;
   this.superclass = Packages.java.lang.Object(); // do not need to resolve
   this.interfaces = [Util.resolveClass("java.lang.Cloneable"), Util.resolveClass("java.io.Serializable")];
   this.classname = elementType.classname + "[]";
}
ArrayType.prototype.equals = function(other) {
   return other.isArray() && other.elementType.equals(this.elementType);
};
ArrayType.prototype.isArray = Util.TRUE;
ArrayType.prototype.isPrimitive = Util.FALSE;
ArrayType.prototype.initialized = false;
ArrayType.prototype.toString = function() {
   return "<type " + this.classname + ">";
};

////////////////////////////////////////////

// length is a JS number
function ArrayObject(length, type) {
   this.array = new Array(length);
   this.thisclass = type;
   this.length = length;
}
// do not need to resolve
ArrayObject.prototype = new (Packages.java.lang.Object())(); // DO NOT CALL newInstance!
ArrayObject.prototype.get = function(index) {
   if (index < 0 || index >= this.length) {
      var ex = Util.resolveClass("java.lang.ArrayIndexOutOfBoundsException").newInstance();
      ex["<init>(I)V"](new Integer(index));
      throw ex;
   }
   return this.array[index];
};
ArrayObject.prototype.get_cps = function(index, cont, exc) {
   var that = this;
   return function() {
      if (index < 0 || index >= that.length) {
         function cont2(type) {
            function cont3(obj) {
               function cont4() {
                  return exc(obj);
               }
               return obj["<init>(I)V"]([new Integer(index)], cont4, exc);
            }
            return type.newInstance(cont3, exc);
         }
         return Util.resolveClass("java.lang.ArrayIndexOutOfBoundsException", cont2, exc);
      }
      return cont(that.array[index]);
   };
};
ArrayObject.prototype.set = function(index, value) {
   if (index < 0 || index >= this.length) {
      var ex = Util.resolveClass("java.lang.ArrayIndexOutOfBoundsException").newInstance();
      ex["<init>(I)V"](new Integer(index));
      throw ex;
   }
   this.array[index] = value;
};
ArrayObject.prototype.set_cps = function(index, value, cont, exc) {
   var that = this;
   return function() {
      if (index < 0 || index >= that.length) {
         function cont2(type) {
            function cont3(obj) {
               function cont4() {
                  return exc(obj);
               }
               return obj["<init>(I)V"]([new Integer(index)], cont4, exc);
            }
            return type.newInstance(cont3, exc);
         }
         return Util.resolveClass("java.lang.ArrayIndexOutOfBoundsException", cont2, exc);
      }
      that.array[index] = value;
      return cont;
   };
};
// returns Integer
ArrayObject.prototype.arraylength = function() {
   return new Integer(this.length);
};
ArrayObject.prototype["clone()Ljava/lang/Object;"] = function() {
   // deep copy of one dimension
   var result = new ArrayObject(this.length, this.thisclass);
   for (var i = 0; i < this.length; i++) {
      result.array[i] = this.array[i];
   }
   return result;
};
