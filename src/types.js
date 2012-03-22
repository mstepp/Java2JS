// name is in string form i.e. "float"
function PrimitiveTD(name) {
   if (name in PrimitiveTD.NAME2SIGNATURE) {
      this.name = name;
   } else {
      throw "Invalid name for primitive type descriptor";
   }
}
PrimitiveTD.prototype.isPrimitive = function() {return true;};
PrimitiveTD.prototype.isClass = function() {return false;};
PrimitiveTD.prototype.isArray = function() {return false;};
PrimitiveTD.prototype.isParameterType = function() {
   return this.name != "void";
};
PrimitiveTD.prototype.getSignature = function() {
   return PrimitiveTD.NAME2SIGNATURE[this.name];
};
PrimitiveTD.prototype.toString = function() {
   return this.name;
};
PrimitiveTD.prototype.getStackSlots = function() {
   if (this.name == "double" || this.name = "long") {
      return 2;
   } else if (this.name == "void") {
      return 0;
   } else {
      return 1;
   }
};
PrimitiveTD.NAME2SIGNATURE = {
   "boolean":"Z",
   "byte":"B",
   "char":"C",
   "double":"D",
   "float":"F",
   "int":"I",
   "long":"J",
   "short":"S",
   "void":"V"
};
PrimitiveTD.BOOLEAN = new PrimitiveTD("boolean");
PrimitiveTD.BYTE = new PrimitiveTD("byte");
PrimitiveTD.CHAR = new PrimitiveTD("char");
PrimitiveTD.DOUBLE = new PrimitiveTD("double");
PrimitiveTD.FLOAT = new PrimitiveTD("float");
PrimitiveTD.INT = new PrimitiveTD("int");
PrimitiveTD.LONG = new PrimitiveTD("long");
PrimitiveTD.SHORT = new PrimitiveTD("short");
PrimitiveTD.VOID = new PrimitiveTD("void");

///////////////////////////////////////////

// name is in '.' form
function ClassTD(classname) {
   this.classname = classname;
}
ClassTD.prototype.isPrimitive = function() {return false;};
ClassTD.prototype.isClass = function() {return true;};
ClassTD.prototype.isArray = function() {return false;};
ClassTD.prototype.getSignature = function() {
   return "L" + this.classname.replace(/[.]/gi, "/") + ";";
};
ClassTD.prototype.toString = function() {
   return this.classname;
};
ClassTD.prototype.isParameterType = function() {return true;};
ClassTD.prototype.getStackSlots = function() {return 1;};

////////////////////////////////////////

function ArrayTD(elementTD) {
   this.elementTD = elementTD;
}
ArrayTD.prototype.isPrimitive = function() {return false;};
ArrayTD.prototype.isClass = function() {return false;};
ArrayTD.prototype.isArray = function() {return true;};
ArrayTD.prototype.getSignature = function() {
   return "[" + this.elementTD.getSignature();
};
ArrayTD.prototype.toString = function() {
   return this.elementTD.getSignature() + "[]";
};
ArrayTD.prototype.isParameterType = function() {return true;};
ArrayTD.prototype.getStackSlots = function() {return 1;};
