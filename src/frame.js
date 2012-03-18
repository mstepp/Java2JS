function Frame(locals, maxLocals) {
   this.locals = locals;
   while (this.locals.length < maxLocals) {
      this.locals.push(null);
   }
   this.stack = [];
   this.return_value = null;
}
Frame.prototype.load = function(index, cont, exc) {
   var that = this;
   return function() {
      that.push(that.locals[index]);
      return cont;
   };
};
Frame.prototype.load2 = function(index, cont, exc) {
   var that = this;
   return function() {
      that.push2(that.locals[index]);
      return cont;
   };
};
Frame.prototype.store = function(index, cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.locals[index] = value;
      return cont;
   };
};
Frame.prototype.store2 = function(index, cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.locals[index] = value;
      return cont;
   };
};
Frame.prototype.POP = function() {
   return this.stack.pop();
};
Frame.prototype.POP2 = function() {
   var result = this.stack.pop();
   this.stack.pop();
   return result;
};
Frame.prototype.push = function(item) {
   this.stack.push(item);
};
Frame.prototype.push2 = function(item) {
   this.stack.push(item);
   this.stack.push(item);
};
Frame.prototype["return"] = function(cont, exc) {
   var that = this;
   return function() {
      that.return_value = null;
      return cont;
   };
};
Frame.prototype.return1 = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.return_value = value;
      return cont;
   };
};
Frame.prototype.return2 = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.return_value = value;
      return cont;
   };
};
Frame.prototype.ret = function(index, cont, exc) {
   var that = this;
   return function() {
      // TODO??
      return that.locals[index].address();
   };
};
Frame.prototype.jsr = function(targetCont, ftCont, exc) {
   var that = this;
   return function() {
      that.push({address:ftCont});
      return targetCont;
   };
};
Frame.prototype.aconst_null = function(cont, exc) {
   var that = this;
   return function() {
      that.push(null);
      return cont;
   };
};
Frame.prototype.dadd = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs + rhs);
      return cont;
   };
};
Frame.prototype.ddiv = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs / rhs);
      return cont;
   };
};
Frame.prototype.dmul = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs * rhs);
      return cont;
   };
};
Frame.prototype.dneg = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.push2(-value);
      return cont;
   };
};
Frame.prototype.drem = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs % rhs);
      return cont;
   };
};
Frame.prototype.dsub = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs - rhs);
      return cont;
   };
};

Frame.prototype.fadd = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs + rhs);
      return cont;
   };
};
Frame.prototype.fdiv = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs / rhs);
      return cont;
   };
};
Frame.prototype.fmul = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs * rhs);
      return cont;
   };
};
Frame.prototype.fneg = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push(-value);
      return cont;
   };
};
Frame.prototype.frem = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs % rhs);
      return cont;
   };
};
Frame.prototype.fsub = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs - rhs);
      return cont;
   };
};

Frame.prototype.iadd = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.add(rhs));
      return cont;
   };
};
Frame.prototype.iand = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.and(rhs));
      return cont;
   };
};
Frame.prototype.idiv = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      function cont2(divmod) {
         that.push(divmod.div);
         return cont;
      }
      return lhs.divmod(rhs, cont2, exc); // TODO
   };
};
Frame.prototype.imul = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.multiply(rhs));
      return cont;
   };
};
Frame.prototype.ineg = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push(value.negate());
      return cont;
   };
};
Frame.prototype.ior = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.or(rhs));
      return cont;
   };
};
Frame.prototype.irem = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      function cont2(divmod) {
         that.push(divmod.mod);
         return cont;
      }
      return lhs.divmod(rhs, cont2, exc); // TODO
   };
};
Frame.prototype.ishl = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.shl(rhs));
      return cont;
   };
};
Frame.prototype.ishr = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.shr(rhs));
      return cont;
   };
};
Frame.prototype.isub = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.subtract(rhs));
      return cont;
   };
};
Frame.prototype.iushr = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.ushr(rhs));
      return cont;
   };
};
Frame.prototype.ixor = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(lhs.xor(rhs));
      return cont;
   };
};

Frame.prototype.ladd = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs.add(rhs));
      return cont;
   };
};
Frame.prototype.land = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs.and(rhs));
      return cont;
   };
};
Frame.prototype.ldiv = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      function cont2(divmod) {
         that.push2(divmod.div);
         return cont;
      }
      return lhs.divmod(rhs, cont2, exc);
   };
};
Frame.prototype.lmul = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs.multiply(rhs));
      return cont;
   };
};
Frame.prototype.lneg = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.push2(value.negate());
      return cont;
   };
};
Frame.prototype.lor = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs.or(rhs));
      return cont;
   };
};
Frame.prototype.lrem = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      function cont2(divmod) {
         that.push2(divmod.mod);
         return cont;
      }
      return lhs.divmod(rhs, cont2, exc);
   };
};
Frame.prototype.lshl = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP2();
      that.push2(lhs.shl(rhs));
      return cont;
   };
};
Frame.prototype.lshr = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP2();
      that.push2(lhs.shr(rhs));
      return cont;
   };
};
Frame.prototype.lsub = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs.subtract(rhs));
      return cont;
   };
};
Frame.prototype.lushr = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP2();
      that.push2(lhs.ushr(rhs));
      return cont;
   };
};
Frame.prototype.lxor = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push2(lhs.xor(rhs));
      return cont;
   };
};
Frame.prototype.aload = function(cont, exc) {
   var that = this;
   return function() {
      var index = that.POP();
      var array = that.POP();
      function cont2() {
         that.push(array.get(index.toJS()));         
         return cont;
      }
      return Util.assertWithException(array != null, "java.lang.NullPointerException", cont2, exc); // TODO
   };
};
Frame.prototype.aload2 = function(cont, exc) {
   var that = this;
   return function() {
      var index = that.POP();
      var array = that.POP();
      function cont2() {
         that.push2(array.get(index.toJS()));
         return cont;
      }
      return Util.assertWithException(array != null, "java.lang.NullPointerException", cont2, exc); // TODO
   };
};
Frame.prototype.astore = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      var index = that.POP();
      var array = that.POP();
      function cont2() {
         array.set(index.toJS(), value);
         return cont;
      }
      return Util.assertWithException(array != null, "java.lang.NullPointerException", cont2, exc); // TODO
   };
};
Frame.prototype.astore2 = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      var index = that.POP();
      var array = that.POP();
      function cont2() {
         array.set(index.toJS(), value);
         return cont;
      }
      return Util.assertWithException(array != null, "java.lang.NullPointerException", cont2, exc); // TODO
   };
};
Frame.prototype.arraylength = function(cont, exc) {
   var that = this;
   return function() {
      var array = that.POP();
      function cont2() {
         that.push(array.arraylength());
         return cont;
      }
      return Util.assertWithException(array != null, "java.lang.NullPointerException", cont2, exc); // TODO
   };
};
Frame.prototype.athrow = function(cont, exc) {
   var that = this;
   return function() {
      var exception = that.POP();
      function cont2() {
         return exc(exception);
      }
      return Util.assertWithException(exception != null, "java.lang.NullPointerException", cont2, exc); // TODO
   };
};
Frame.prototype.bipush = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push(new Integer(value));
      return cont;
   };
};
Frame.prototype.d2f = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.push(value);
      return cont;
   };
};
Frame.prototype.d2i = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.push(new Integer(value % 4294967296.0));
      return cont;
   };
};
Frame.prototype.f2d = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push2(value);
      return cont;
   };
};
Frame.prototype.f2i = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push(new Integer(value % 4294967296.0));
      return cont;
   };
};
Frame.prototype.i2b = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push(value.i2b());
      return cont;
   };
};
Frame.prototype.i2c = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push(value.i2c());
      return cont;
   };
};
Frame.prototype.i2d = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push2(value.i2d());
      return cont;
   };
};
Frame.prototype.i2f = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push(value.i2d());
      return cont;
   };
};
Frame.prototype.i2l = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push2(value.i2l());
      return cont;
   };
};
Frame.prototype.i2s = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP();
      that.push(value.i2s());
      return cont;
   };
};
Frame.prototype.l2i = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.push(value.l2i());
      return cont;
   };
};
Frame.prototype.d2l = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      var negative = false;
      if (value < 0) {
         value = -value;
         negative = true;
      }
      var low = (value%2147483648.0) & 0xFFFFFFFF;
      var high = ((value/2147483648.0)%2147483648.0) & 0xFFFFFFFF;
      var l = new Long(low, high);
      that.push2(negative ? l.negate() : l);
      return cont;
   };
};
Frame.prototype.f2l = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      var negative = false;
      if (value < 0) {
         value = -value;
         negative = true;
      }
      var low = (value%2147483648.0) & 0xFFFFFFFF;
      var high = ((value/2147483648.0)%2147483648.0) & 0xFFFFFFFF;
      var l = new Long(low, high);
      that.push(negative ? l.negate() : l);
      return cont;
   };
};
Frame.prototype.l2d = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.push2(value.l2d());
      return cont;
   };
};
Frame.prototype.l2f = function(cont, exc) {
   var that = this;
   return function() {
      var value = that.POP2();
      that.push(value.l2d());
      return cont;
   };
};
Frame.prototype.anewarray = function(typename, cont, exc) {
   var that = this;
   return function() {
      var length = that.POP();
      function cont3(array) {
         that.push(array);
         return cont;
      }
      function cont2(type) {
         return Util.multianewarray([length], type, cont3, exc);
      }
      function cont1() {
         return Util.nameToType(typename, cont2, exc);
      }
      return Util.assertWithException(!length.isNegative(), "java.lang.NegativeArraySizeException", cont1, exc); // TODO
   };
};
Frame.prototype["goto"] = function(cont, exc) {
   return cont; // TODO??
};
Frame.prototype.if_acmpeq = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs == rhs) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.if_acmpne = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs != rhs) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.if_icmpeq = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs.equals(rhs)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.if_icmpne = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs.notEquals(rhs)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.if_icmpge = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs.gte(rhs)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.if_icmpgt = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs.gt(rhs)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.if_icmple = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs.lte(rhs)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.if_icmplt = function(contT, contF, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      if (lhs.lt(rhs)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.ifeq = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs.equals(Integer.ZERO)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.ifne = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs.notEquals(Integer.ZERO)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.ifge = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs.gte(Integer.ZERO)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.ifgt = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs.gt(Integer.ZERO)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.ifle = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs.lte(Integer.ZERO)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.iflt = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs.lt(Integer.ZERO)) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.ifnull = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs == null) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.ifnonnull = function(contT, contF, exc) {
   var that = this;
   return function() {
      var lhs = that.POP();
      if (lhs != null) {
         return contT;
      } else {
         return contF;
      }
   };
};
Frame.prototype.checkcast = function(typename, cont, exc) {
   var that = this;
   return function() {
      var obj = that.POP();
      function cont3() {
         that.push(obj);
         return cont;
      }
      function cont2(type) {
         var check = Util.instance_of(obj, type);
         return Util.assertWithException(check, "java.lang.ClassCastException", cont3, exc); // TODO
      }
      return Util.nameToType(typename, cont2, exc);
   };
};
Frame.prototype.getfield = function(fieldname, is2slot, cont, exc) {
   var that = this;
   return function() {
      var obj = that.POP();
      function cont2() {
         if (is2slot) {
            that.push2(obj[fieldname]);
         } else {
            that.push(obj[fieldname]);
         }
         return cont;
      }
      return Util.assertWithException(obj != null, "java.lang.NullPointerException", cont2, exc);
   };
};
Frame.prototype.getstatic = function(classname, fieldname, is2slot, cont, exc) {
   var that = this;
   return function() {
      function cont3(type) {
         if (is2slot) {
            that.push2(type[fieldname]);
         } else {
            that.push2(type[fieldname]);
         }
         return cont;
      }
      function cont2(type) {
         return Util.initialize(type, function() {return cont3(type);}, exc);
      }
      return Util.resolveClass(classname, cont2, exc);
   };
};
Frame.prototype.putfield = function(fieldname, is2slot, cont, exc) {
   var that = this;
   return function() {
      var value = is2slot ? that.POP2() : that.POP();
      var obj = that.POP();
      function cont2() {
         obj[fieldname] = value;
         return cont;
      }
      return Util.assertWithException(obj != null, "java.lang.NullPointerException", cont2, exc);
   };
};
Frame.prototype.putstatic = function(classname, fieldname, is2slot, cont, exc) {
   var that = this;
   return function() {
      var value = is2slot ? that.POP2() : that.POP();
      function cont2(type) {
         type[fieldname] = value;
         return cont;
      }
      function cont1(type) {
         return Util.initialize(type, function() {return cont2(type);}, exc);
      }
      return Util.resolveClass(classname, cont1, exc);
   };
};
// INVOKE*
Frame.prototype["instanceof"] = function(typename, cont, exc) {
   var that = this;
   return function() {
      var obj = that.POP();
      function cont2(type) {
         that.push(Util.instance_of(obj, type) ? Integer.ONE : Integer.ZERO);
         return cont;
      }
      return Util.nameToType(typename, cont2, exc);
   };
};
Frame.prototype.ldc_value = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push(value);
      return cont;
   };
};
Frame.prototype.ldc_string = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push(Util.js2java_string(value));
      return cont;
   };
};
Frame.prototype.ldc_class = function(classname, cont, exc) {
   var that = this;
   return function() {
      function cont2(clazz) {
         that.push(clazz);
         return cont;
      }
      return Util.getClassByName(classname, cont2, exc);
   };
};
Frame.prototype.ldc2_w = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push2(value);
      return cont;
   };
};
Frame.prototype.multianewarray = function(ndims, typename, cont, exc) {
   var that = this;
   return function() {
      var dims = new Array(ndims);
      for (var i = ndim-1; i >= 0; i--) {
         dims[i] = that.POP();
      }
      function cont2(array) {
         that.push(array);
         return cont;
      }
      function cont1(type) {
         return Util.multianewarray(dims, type, cont2, exc);
      }
      return Util.nameToType(typename, cont1, exc);
   };
};
Frame.prototype["new"] = function(typename, cont, exc) {
   var that = this;
   return function() {
      function cont3(obj) {
         that.push(obj);
         return cont;
      }
      function cont2(type) {
         return type.newInstance(cont3, exc);
      }
      return Util.resolveClass(typename, cont2, exc);
   };
};
Frame.prototype.dcmpg = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push(Util.dcmpg(lhs, rhs));
      return cont;
   };
};
Frame.prototype.dcmpl = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push(Util.dcmpl(lhs, rhs));
      return cont;
   };
};
Frame.prototype.fcmpg = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(Util.fcmpg(lhs, rhs));
      return cont;
   };
};
Frame.prototype.fcmpl = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP();
      var lhs = that.POP();
      that.push(Util.fcmpl(lhs, rhs));
      return cont;
   };
};
Frame.prototype.dconst = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push2(value);
      return cont;
   };
};
Frame.prototype.fconst = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push(value);
      return cont;
   };
};
Frame.prototype.iconst = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push(new Integer(value));
      return cont;
   };
};
Frame.prototype.lcmp = function(cont, exc) {
   var that = this;
   return function() {
      var rhs = that.POP2();
      var lhs = that.POP2();
      that.push(new Integer(lhs.compareTo(rhs)));
      return cont;
   };
};
Frame.prototype.lconst = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push2(value);
      return cont;
   };
};
Frame.prototype.iinc = function(index, value, cont, exc) {
   var that = this;
   return function() {
      that.locals[index] = that.locals[index].add(new Integer(value));
      return cont;
   };
};
Frame.prototype.monitorenter = function(cont, exc) {
   var that = this;
   return function() {
      var obj = that.POP();
      return Util.assertWithException(obj != null, "java.lang.NullPointerException", cont, exc);
   };
};
Frame.prototype.monitorexit = Frame.prototype.monitorenter;
Frame.prototype.newarray = function(type, cont, exc) {
   var that = this;
   return function() {
      var count = that.POP();
      function cont3(array) {
         that.push(array);
         return cont;
      }
      function cont2() {
         return Util.multianewarray([count], type, cont3, exc);
      }
      return Util.assertWithException(!count.isNegative(), "java.lang.NegativeArraySizeException", cont2, exc);
   };
};
Frame.prototype.nop = function(cont, exc) {
   return cont;
};
Frame.prototype.sipush = function(value, cont, exc) {
   var that = this;
   return function() {
      that.push(new Integer(value));
      return cont;
   };
};
Frame.prototype.dup = function(cont, exc) {
   var that = this;
   return function() {
      var top = that.POP();
      that.push(top);
      that.push(top);
      return cont;
   };
};
Frame.prototype.dup_x1 = function(cont, exc) {
   var that = this;
   return function() {
      var top1 = that.POP();
      var top2 = that.POP();
      that.push(top1);
      that.push(top2);
      that.push(top1);
      return cont;
   };
};
Frame.prototype.dup_x2 = function(cont, exc) {
   var that = this;
   return function() {
      var top1 = that.POP();
      var top2 = that.POP();
      var top3 = that.POP();
      that.push(top1);
      that.push(top3);
      that.push(top2);
      that.push(top1);
      return cont;
   };
};
Frame.prototype.dup2 = function(cont, exc) {
   var that = this;
   return function() {
      var top1 = that.POP();
      var top2 = that.POP();
      that.push(top2);
      that.push(top1);
      that.push(top2);
      that.push(top1);
      return cont;
   };
};
Frame.prototype.dup2_x1 = function(cont, exc) {
   var that = this;
   return function() {
      var top1 = that.POP();
      var top2 = that.POP();
      var top3 = that.POP();
      that.push(top2);
      that.push(top1);
      that.push(top3);
      that.push(top2);
      that.push(top1);
      return cont;
   };
};
Frame.prototype.dup2_x2 = function(cont, exc) {
   var that = this;
   return function() {
      var top1 = that.POP();
      var top2 = that.POP();
      var top3 = that.POP();
      var top4 = that.POP();
      that.push(top2);
      that.push(top1);
      that.push(top4);
      that.push(top3);
      that.push(top2);
      that.push(top1);
      return cont;
   };
};
Frame.prototype.pop = function(cont, exc) {
   var that = this;
   return function() {
      that.POP();
      return cont;
   };
};
Frame.prototype.pop2 = function(cont, exc) {
   var that = this;
   return function() {
      that.POP2();
      return cont;
   };
};
Frame.prototype.swap = function(cont, exc) {
   var that = this;
   return function() {
      var top1 = that.POP();
      var top2 = that.POP();
      that.push(top1);
      that.push(top2);
      return cont;
   };
};
