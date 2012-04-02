// TODO!
Util.register_native("java.lang.reflect.Array", "getLength(Ljava/lang/Object;)I", 
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

   Util.register_native("java.lang.reflect.Array", "get(Ljava/lang/Object;I)Ljava/lang/Object;", 
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
   Util.register_native("java.lang.reflect.Array", "getBoolean(Ljava/lang/Object;I)Z", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.BOOLEAN) {
                                 return elt;
                              } else if (type == Util.resolveClass("java.lang.Boolean")) {
                                 return elt["booleanValue()Z"]();
                              } else {
                                 iae();
                              }
                           }
                           return myget(test, arg0, arg1);
                        });
   Util.register_native("java.lang.reflect.Array", "getByte(Ljava/lang/Object;I)B", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.BYTE) {
                                 return elt;
                              } else if (elt == Util.resolveClass("java.lang.Byte")) {
                                 return elt["byteValue()B"]();
                              } else {
                                 iae();
                              }
                           }
                           return myget(test, arg0, arg1);
                        });
   Util.register_native("java.lang.reflect.Array", "getChar(Ljava/lang/Object;I)C", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.CHAR) {
                                 return elt;
                              } else if (type == Util.resolveClass("java.lang.Character")) {
                                 return elt["charValue()C"]();
                              } else {
                                 iae();
                              }
                           }
                           return myget(test, arg0, arg1);
                        });
   Util.register_native("java.lang.reflect.Array", "getShort(Ljava/lang/Object;I)S", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.SHORT) {
                                 return elt;
                              } else if (type == Util.resolveClass("java.lang.Short")) {
                                 return elt["shortValue()S"]();
                              } else {
                                 iae();
                              }
                           }
                           return myget(test, arg0, arg1);
                        });
   Util.register_native("java.lang.reflect.Array", "getInt(Ljava/lang/Object;I)I", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.INT) {
                                 return elt;
                              } else if (type == Util.resolveClass("java.lang.Integer")) {
                                 return elt["intValue()I"]();
                              } else {
                                 iae();
                              }
                           }
                           return myget(test, arg0, arg1);
                        });
   Util.register_native("java.lang.reflect.Array", "getLong(Ljava/lang/Object;I)J", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.LONG) {
                                 return elt;
                              } else if (type == Util.resolveClass("java.lang.Long")) {
                                 return elt["longValue()J"]();
                              } else {
                                 iae();
                              }
                           }
                           return myget(test, arg0, arg1);
                        });
   Util.register_native("java.lang.reflect.Array", "getFloat(Ljava/lang/Object;I)F", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.FLOAT) {
                                 return elt;
                              } else if (type == Util.resolveClass("java.lang.Float")) {
                                 return elt["floatValue()F"]();
                              } else {
                                 iae();
                              }
                           }
                           return myget(test, arg0, arg1);
                        });
   Util.register_native("java.lang.reflect.Array", "getDouble(Ljava/lang/Object;I)D", 
                        function(arg0, arg1) {
                           function test(type, elt) {
                              if (type == PrimitiveType.DOUBLE) {
                                 return elt;
                              } else if (type == Util.resolveClass("java.lang.Double")) {
                                 return elt["doubleValue()D"]();
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

   Util.register_native("java.lang.reflect.Array", "set(Ljava/lang/Object;ILjava/lang/Object;)V", 
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
   Util.register_native("java.lang.reflect.Array", "setBoolean(Ljava/lang/Object;IZ)V", 
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
   Util.register_native("java.lang.reflect.Array", "setByte(Ljava/lang/Object;IB)V", 
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
   Util.register_native("java.lang.reflect.Array", "setChar(Ljava/lang/Object;IC)V", 
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
   Util.register_native("java.lang.reflect.Array", "setShort(Ljava/lang/Object;IS)V", 
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
   Util.register_native("java.lang.reflect.Array", "setInt(Ljava/lang/Object;II)V", 
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
   Util.register_native("java.lang.reflect.Array", "setLong(Ljava/lang/Object;IJ)V", 
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
   Util.register_native("java.lang.reflect.Array", "setFloat(Ljava/lang/Object;IF)V", 
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
   Util.register_native("java.lang.reflect.Array", "setDouble(Ljava/lang/Object;ID)V", 
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


Util.register_native("java.lang.reflect.Array", "newArray(Ljava/lang/Class;I)Ljava/lang/Object;", 
                     function(clazz, length) {
                        length = length.toJS();
                        Util.assertWithException(clazz != null, Util.resolveClass("java.lang.NullPointerException"));
                        Util.assertWithException(clazz.pojo != PrimitiveType.VOID, Util.resolveClass("java.lang.IllegalArgumentException"));
                        Util.assertWithException(length >= 0, Util.resolveClass("java.lang.NegativeArraySizeException"));
                        return new ArrayObject(length, new ArrayType(clazz.pojo));
                     });
Util.register_native("java.lang.reflect.Array", "multiNewArray(Ljava/lang/Class;AI)Ljava/lang/Object;", 
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

Util.register_native("java.lang.Class", "isInstance(Ljava/lang/Object;)Z", 
                     function(arg0) {
                        Util.instance_of(arg0, this.pojo);
                     });
Util.register_native("java.lang.Class", "isAssignableFrom(Ljava/lang/Class;)Z", 
                     function(arg0) {
                        Util.assertWithException(arg0 != null, Util.resolveClass("java.lang.NullPointerException"));
                        return Util.isAssignableFrom(this.pojo, arg0.pojo) ? Integer.ONE : Integer.ZERO;
                     });
Util.register_native("java.lang.Class", "isInterface()Z", 
                     function() {
                        return this.pojo.isInterface() ? Integer.ONE : Integer.ZERO;
                     });
Util.register_native("java.lang.Class", "isArray()Z", 
                     function() {
                        return this.pojo.isArray() ? Integer.ONE : Integer.ZERO;
                     });
Util.register_native("java.lang.Class", "isPrimitive()Z", 
                     function() {
                        return this.pojo.isPrimitive() ? Integer.ONE : Integer.ZERO;
                     });
Util.register_native("java.lang.Class", "getName0()Ljava/lang/String;", 
                     function() {
                        return Util.js2java_string(this.pojo.classname);
                     });
Util.register_native("java.lang.Class", "getClassLoader0()Ljava/lang/ClassLoader;", 
                     function() {
                        return null;
                     });
Util.register_native("java.lang.Class", "getSuperclass()Ljava/lang/Class;", 
                     function() {
                        if (this.pojo.isPrimitive()) {
                           return null;
                        } else if (this.pojo.isArray()) {
                           return Util.getClass(Packages.java.lang.Object()); // do not need to resolve
                        } else {
                           return Util.getClass(this.pojo.superclass);
                        }
                     });
Util.register_native("java.lang.Class", "getInterfaces()[Ljava/lang/Class;", 
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
Util.register_native("java.lang.Class", "getComponentType()Ljava/lang/Class;", 
                     function() {
                        if (this.pojo.isArray()) {
                           return Util.getClass(this.pojo.elementType);
                        } else {
                           return null;
                        }
                     });
Util.register_native("java.lang.Class", "getModifiers()I", 
                     function() {
                        var modifiers = Util.resolveClass("java.lang.reflect.Modifier");
                        function getmods(type) {
                           if (type.isArray()) {
                              var inner = type;
                              while (inner.isArray()) {
                                 inner = inner.elementType;
                              }
                              var result = getmods(inner);
                              return result.or(modifiers["FINAL.I"]).and(modifiers["INTERFACE.I"].not());
                           } else if (type.isPrimitive()) {
                              return modifiers["PUBLIC.I"].or(modifiers["FINAL.I"]);
                           } else {
                              return new Integer(type.modifiers);
                           }
                        }

                        return getmods(this.pojo);
                     });
Util.register_native("java.lang.Class", "getSigners()[Ljava/lang/Object;", 
                     function() {
                        return null;
                     });
Util.register_native("java.lang.Class", "setSigners([Ljava/lang/Object;)V", 
                     function(arg0) {
                        // do nothing
                     });
Util.register_native("java.lang.Class", "getEnclosingMethod0()[Ljava/lang/Object;", 
                     function() {
                        return null;
                     });
Util.register_native("java.lang.Class", "getDeclaringClass()Ljava/lang/Class;", 
                     function() {
                        return null;
                     });
Util.register_native("java.lang.Class", "getProtectionDomain0()Ljava/security/ProtectionDomain;", 
                     function() {
                        return null; // TODO
                     });
Util.register_native("java.lang.Class", "setProtectionDomain0(Ljava/security/ProtectionDomain;)V", 
                     function(arg0) {
                        // do nothing
                     });
Util.register_native("java.lang.Class", "getGenericSignature()Ljava/lang/String;", 
                     function() {
                        return null;
                     });
Util.register_native("java.lang.Class", "getRawAnnotations()[B", 
                     function() {
                        return null;
                     });
Util.register_native("java.lang.Class", "getConstantPool()Lsun/reflect/ConstantPool;", 
                     function() {
                        return null;
                     });
Util.register_native("java.lang.Class", "getDeclaredFields0(Z)[Ljava/lang/reflect/Field;", 
                     function(arg0) {
                        return new ArrayObject(0, Util.resolveClass("java.lang.reflect.Field"));
                     });
Util.register_native("java.lang.Class", "getDeclaredMethods0(Z)[Ljava/lang/reflect/Method;", 
                     function(publicOnly) {
                        if (this.pojo.isPrimitive() || this.pojo.isArray()) {
                           return null;
                        }
                        var memberinfo = this.pojo.memberinfo;
                        var methods = [];
                        for (var membername in memberinfo) {
                           if (membername.indexOf("(") >= 0) {
                              // method
                              if (publicOnly.isTrue()) {
				     

                              }

				 

                           }
                        }
                     });
Util.register_native("java.lang.Class", "getDeclaredConstructors0(Z)[Ljava/lang/reflect/Constructor;", 
                     function(arg0) {
                        return null; // TODO
                     });
Util.register_native("java.lang.Class", "getDeclaredClasses0()[Ljava/lang/Class;", 
                     function() {
                        return null; // TODO
                     });
Util.register_native("java.lang.Class", "registerNatives()V", 
                     function() {
                        // do nothing
                     });
Util.register_native("java.lang.Class", "forName0(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", 
                     function(arg0, arg1, arg2) {
                        return Util.getClassByName(Util.java2js_string(arg0));
                     });
Util.register_native("java.lang.Class", "getPrimitiveClass(Ljava/lang/String;)Ljava/lang/Class;", 
                     function(arg0) {
                        var name = Util.java2js_string(arg0);
                        var clazz =  Util.getClassByName(name);
                        return clazz;
                     });
Util.register_native("java.lang.Class", "desiredAssertionStatus0(Ljava/lang/Class;)Z", 
                     function(arg0) {
                        return Integer.ZERO;
                     });
