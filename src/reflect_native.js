Util.register_native("java.lang.reflect.Array", "method_get$Length_I_Ljava$Dlang$DObjectE_", 
		     function(arg0) {
			 Util.assertWithException(arg0 != null, Util.resolveClass("java.lang.NullPointerException"));
			 Util.assertWithException(arg0.thisclass.isArray(), Util.resolveClass("java.lang.IllegalArgumentException"));
			 return arg0.arraylength();
		     });

(function() {
    function iae() {
	Util.assertWithException(test(elt), Util.resolveClass("java.lang.IllegalArgumentException"));
    }

    function myget(result, arg0, arg1) {
	Util.assertWithException(arg0 != null, Util.resolveClass("java.lang.NullPointerException"));
	Util.assertWithException(arg0.thisclass.isArray(), Util.resolveClass("java.lang.IllegalArgumentException"));
	var type = arg0.thisclass.elementType;
	return result(type, arg0.get(arg1));
    }

    Util.register_native("java.lang.reflect.Array", "method_get_Ljava$Dlang$DObjectE_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function result(type, elt) {
				 if (!elt.isPrimitive()) {
				     return elt;
				 } else {
				     iae();
				 }
			     }
			     return myget(result, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_getBoolean_Z_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.BOOLEAN) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Boolean")) {
				     return elt.method_booleanValue_Z__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_getByte_B_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.BYTE) {
				     return elt;
				 } else if (elt == Util.resolveClass("java.lang.Byte")) {
				     return elt.method_byteValue_B__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_getChar_C_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.CHAR) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Character")) {
				     return elt.method_charValue_C__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_getShort_S_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.SHORT) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Short")) {
				     return elt.method_shortValue_S__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_getInt_I_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.INT) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Integer")) {
				     return elt.method_intValue_I__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_get$Long_J_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.LONG) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Long")) {
				     return elt.method_longValue_J__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_getFloat_F_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.FLOAT) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Float")) {
				     return elt.method_floatValue_F__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
    Util.register_native("java.lang.reflect.Array", "method_getDouble_D_Ljava$Dlang$DObjectEI_", 
			 function(arg0, arg1) {
			     function test(type, elt) {
				 if (type == PrimitiveType.DOUBLE) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Double")) {
				     return elt.method_doubleValue_D__();
				 } else {
				     iae();
				 }
			     }
			     return myget(test, arg0, arg1);
			 });
})();


(function() {
    function myset(test, array, index, value) {
	Util.assertWithException(arg0 != null, Util.resolveClass("java.lang.NullPointerException"));
	Util.assertWithException(arg0.thisclass.isArray(), Util.resolveClass("java.lang.IllegalArgumentException"));
	var type = arg0.thisclass.elementType;
	array.set(index.toJS(), test(type, value));
    }

    Util.register_native("java.lang.reflect.Array", "method_set_V_Ljava$Dlang$DObjectEILjava$Dlang$DObjectE_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (!type.isPrimitive()) {
				     return elt;
				 } else {
				     iae();
				 }
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_setBoolean_V_Ljava$Dlang$DObjectEIZ_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.BOOLEAN) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Boolean")) {
				     var b = Util.resolveClass("java.lang.Boolean").newInstance();
				     b.init_Z_(elt);
				     return b;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_setByte_V_Ljava$Dlang$DObjectEIB_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.BYTE) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Byte")) {
				     var b = Util.resolveClass("java.lang.Byte").newInstance();
				     b.init_B_(elt);
				     return b;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_setChar_V_Ljava$Dlang$DObjectEIC_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.CHAR) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Character")) {
				     var c = Util.resolveClass("java.lang.Character").newInstance();
				     c.init_C_(elt);
				     return c;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_setShort_V_Ljava$Dlang$DObjectEIS_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.SHORT) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Short")) {
				     var s = Util.resolveClass("java.lang.Short").newInstance();
				     s.init_S_(elt);
				     return s;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_setInt_V_Ljava$Dlang$DObjectEII_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.INT) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Integer")) {
				     var i = Util.resolveClass("java.lang.Integer").newInstance();
				     i.init_I_(elt);
				     return i;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_set$Long_V_Ljava$Dlang$DObjectEIJ_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.LONG) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Long")) {
				     var l = Util.resolveClass("java.lang.Long").newInstance();
				     l.init_J_(elt);
				     return l;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_setFloat_V_Ljava$Dlang$DObjectEIF_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.FLOAT) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Float")) {
				     var f = Util.resolveClass("java.lang.Float").newInstance();
				     f.init_F_(elt);
				     return f;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
    Util.register_native("java.lang.reflect.Array", "method_setDouble_V_Ljava$Dlang$DObjectEID_", 
			 function(arg0, arg1, arg2) {
			     function test(type, elt) {
				 if (type == PrimitiveType.DOUBLE) {
				     return elt;
				 } else if (type == Util.resolveClass("java.lang.Double")) {
				     var d = Util.resolveClass("java.lang.Double").newInstance();
				     d.init_D_(elt);
				     return d;
				 } else {
				     iae();
				 } 
			     }
			     myset(test, arg0, arg1, arg2);
			 });
})();


Util.register_native("java.lang.reflect.Array", "method_new$Array_Ljava$Dlang$DObjectE_Ljava$Dlang$DClassEI_", 
		     function(clazz, length) {
			 length = length.toJS();
			 Util.assertWithException(clazz != null, Util.resolveClass("java.lang.NullPointerException"));
			 Util.assertWithException(clazz.pojo != PrimitiveType.VOID, Util.resolveClass("java.lang.IllegalArgumentException"));
			 Util.assertWithException(length >= 0, Util.resolveClass("java.lang.NegativeArraySizeException"));
			 return new ArrayObject(length, new ArrayType(clazz.pojo));
		     });
Util.register_native("java.lang.reflect.Array", "method_multiNew$Array_Ljava$Dlang$DObjectE_Ljava$Dlang$DClassEAI_", 
		     function(clazz, dims) {
			 Util.assertWithException(clazz != null, Util.resolveClass("java.lang.NullPointerException"));
			 Util.assertWithException(clazz.pojo != PrimitiveType.VOID, Util.resolveClass("java.lang.IllegalArgumentException"));
			 Util.assertWithException(dims != null && dims.length > 0, Util.resolveClass("java.lang.NullPointerException"));
			 
			 var newdims = [];
			 var type = clazz.pojo;
			 for (var i = 0; i < dims.length; i++) {
			     newdims.push(dims.get(i));
			     type = new ArrayType(type);
			 }

			 return Util.multianewarray(newdims, type);
		     });

Util.register_native("java.lang.Class", "method_isInstance_Z_Ljava$Dlang$DObjectE_", 
		     function(arg0) {
			 Util.instance_of(arg0, this.pojo);
		     });
Util.register_native("java.lang.Class", "method_is$AssignableFrom_Z_Ljava$Dlang$DClassE_", 
		     function(arg0) {
			 Util.assertWithException(arg0 != null, Util.resolveClass("java.lang.NullPointerException"));
			 return Util.isAssignableFrom(this.pojo, arg0.pojo) ? Integer.ONE : Integer.ZERO;
		     });
Util.register_native("java.lang.Class", "method_isInterface_Z__", 
		     function() {
			 return this.pojo.isInterface() ? Integer.ONE : Integer.ZERO;
		     });
Util.register_native("java.lang.Class", "method_is$Array_Z__", 
		     function() {
			 return this.pojo.isArray() ? Integer.ONE : Integer.ZERO;
		     });
Util.register_native("java.lang.Class", "method_isPrimitive_Z__", 
		     function() {
			 return this.pojo.isPrimitive() ? Integer.ONE : Integer.ZERO;
		     });
Util.register_native("java.lang.Class", "method_getName0_Ljava$Dlang$DStringE__", 
		     function() {
			 return Util.js2java_string(this.pojo.classname);
		     });
Util.register_native("java.lang.Class", "method_getClass$Loader0_Ljava$Dlang$DClass$LoaderE__", 
		     function() {
			 return null;
		     });
Util.register_native("java.lang.Class", "method_getSuperclass_Ljava$Dlang$DClassE__", 
		     function() {
			 if (this.pojo.isPrimitive()) {
			     return null;
			 } else if (this.pojo.isArray()) {
			     return Util.getClass(Packages.java.lang.Object()); // do not need to resolve
			 } else {
			     return Util.getClass(this.pojo.superclass);
			 }
		     });
Util.register_native("java.lang.Class", "method_getInterfaces_ALjava$Dlang$DClassE__", 
		     function() {
			 if (this.pojo.isPrimitive()) {
			     return new ArrayObject(0, this.thisclass);
			 }
			 var length = this.pojo.interfaces.length;
			 var result = new ArrayObject(length, this.thisclass);
			 for (var i = 0; i < length; i++) {
			     result.set(i, Util.getClass(this.pojo.interfaces[i]));
			 }
			 return result;
		     });
Util.register_native("java.lang.Class", "method_getComponentType_Ljava$Dlang$DClassE__", 
		     function() {
			 if (this.pojo.isArray()) {
			     return Util.getClass(this.pojo.elementType);
			 } else {
			     return null;
			 }
		     });
Util.register_native("java.lang.Class", "method_getModifiers_I__", 
		     function() {
			 var modifiers = Util.resolveClass("java.lang.reflect.Modifier");
			 function getmods(type) {
			     if (type.isArray()) {
				 var inner = type;
				 while (inner.isArray()) {
				     inner = inner.elementType;
				 }
				 var result = getmods(inner);
				 return result.or(modifiers.field_FIN$A$L_I).and(modifiers.field_INT$ERF$AC$E_I.not());
			     } else if (type.isPrimitive()) {
				 return modifiers.field_PUB$LIC_I.or(modifiers.field_FIN$A$L_I);
			     } else {
				 return new Integer(type.modifiers);
			     }
			 }

			 return getmods(this.pojo);
		     });
Util.register_native("java.lang.Class", "method_getSigners_ALjava$Dlang$DObjectE__", 
		     function() {
			 return null;
		     });
Util.register_native("java.lang.Class", "method_setSigners_V_ALjava$Dlang$DObjectE_", 
		     function(arg0) {
			 // do nothing
		     });
Util.register_native("java.lang.Class", "method_get$EnclosingMethod0_ALjava$Dlang$DObjectE__", 
		     function() {
			 return null;
		     });
Util.register_native("java.lang.Class", "method_getDeclaringClass_Ljava$Dlang$DClassE__", 
		     function() {
			 return null;
		     });
Util.register_native("java.lang.Class", "method_getProtectionDomain0_Ljava$Dsecurity$DProtectionDomainE__", 
		     function() {
			 return null; // TODO
		     });
Util.register_native("java.lang.Class", "method_setProtectionDomain0_V_Ljava$Dsecurity$DProtectionDomainE_", 
		     function(arg0) {
			 // do nothing
		     });
Util.register_native("java.lang.Class", "method_getGenericSignature_Ljava$Dlang$DStringE__", 
		     function() {
			 return null;
		     });
Util.register_native("java.lang.Class", "method_getRaw$Annotations_AB__", 
		     function() {
			 return null;
		     });
Util.register_native("java.lang.Class", "method_getConstantPool_Lsun$Dreflect$DConstantPoolE__", 
		     function() {
			 return null;
		     });
Util.register_native("java.lang.Class", "method_getDeclaredFields0_ALjava$Dlang$Dreflect$DFieldE_Z_", 
		     function(arg0) {
			 return new ArrayObject(0, Util.resolveClass("java.lang.reflect.Field"));
		     });
Util.register_native("java.lang.Class", "method_getDeclaredMethods0_ALjava$Dlang$Dreflect$DMethodE_Z_", 
		     function(publicOnly) {
			 if (this.pojo.isPrimitive() || this.pojo.isArray()) {
			     return null;
			 }
			 var memberinfo = this.pojo.memberinfo;
			 var methods = [];
			 for (var membername in memberinfo) {
			     if (membername.length > 7 && membername.substring(0,7)=="method_") {
				 // method
				 if (publicOnly.isTrue()) {
				     

				 }

				 

			     }
			 }
		     });
Util.register_native("java.lang.Class", "method_getDeclaredConstructors0_ALjava$Dlang$Dreflect$DConstructorE_Z_", 
		     function(arg0) {
			 return null; // TODO
		     });
Util.register_native("java.lang.Class", "method_getDeclaredClasses0_ALjava$Dlang$DClassE__", 
		     function() {
			 return null; // TODO
		     });
Util.register_native("java.lang.Class", "method_registerNatives_V__", 
		     function() {
			 // do nothing
		     });
Util.register_native("java.lang.Class", "method_forName0_Ljava$Dlang$DClassE_Ljava$Dlang$DStringEZLjava$Dlang$DClass$LoaderE_", 
		     function(arg0, arg1, arg2) {
			 return Util.getClassByName(Util.java2js_string(arg0));
		     });
Util.register_native("java.lang.Class", "method_getPrimitiveClass_Ljava$Dlang$DClassE_Ljava$Dlang$DStringE_", 
		     function(arg0) {
			 var name = Util.java2js_string(arg0);
			 var clazz =  Util.getClassByName(name);
			 return clazz;
		     });
Util.register_native("java.lang.Class", "method_desired$AssertionStatus0_Z_Ljava$Dlang$DClassE_", 
		     function(arg0) {
			 return Integer.ZERO;
		     });
