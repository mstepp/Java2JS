var Packages = {};

var Util = 
   (function() {
      var classloader = null;

      // returns an iterator function that will return TDs until empty, then null
      function typeParser(str) {
         var index = 0;
         function parse() {
            if (index >= str.length)
               return null;
            var c = str.charAt(index);
            if (c == "B") {
               index++;
               return PrimitiveTD.BYTE;
            } else if (c == "C") {
               index++;
               return PrimitiveTD.CHAR;
            } else if (c == "D") {
               index++;
               return PrimitiveTD.DOUBLE;
            } else if (c == "F") {
               index++;
               return PrimitiveTD.FLOAT;
            } else if (c == "I") {
               index++;
               return PrimitiveTD.INT;
            } else if (c == "J") {
               index++;
               return PrimitiveTD.LONG;
            } else if (c == "S") {
               index++;
               return PrimitiveTD.SHORT;
            } else if (c == "Z") {
               index++;
               return PrimitiveTD.BOOLEAN;
            } else if (c == "V") {
               index++;
               return PrimitiveTD.VOID;
            } else if (c == "L") {
               // class
               var end = str.indexOf(";", index+1);
               if (end < 0)
                  throw "Expecting ; for class type";
               var classname = str.substring(index+1, end);
               classname = classname.replace(/\//gi, ".");
               index = end+1;
               return new ClassTD(classname);
            } else if (c == '[') {
               // array
               index++;
               var element = parse();
               if (element == null)
                  throw "Cannot parse element type for array";
               return new ArrayTD(element);
            } else {
               throw "Invalid character for signature: " + c;
            }
         }
         return parse;
      }

      // returns TD
      function parseFieldSignature(signature) {
         var parser = typeParser(signature);
         var type = parser();
         if (type == null)
            throw "Cannot parse type from field signature";
         if (!type.isParameterType())
            throw "Field type must be a parameter type";
         return type;
      }

      // returns {params:[TD], return:TD}
      function parseMethodSignature(signature) {
         if (signature.length == 0 || signature.charAt(0) != "(")
            throw "Expecting '('";
         var index = signature.indexOf(")");
         if (index < 0)
            throw "Expecting ')'";
         var paramSigs = signature.substring(1, index);
         var returnSig = signature.substring(index + 1);
         var paramTDs = [];
         var parser = typeParser(paramSigs);
         var type;
         while ((type = parser()) != null) {
            if (!type.isParameterType())
               throw "Parameter type cannot be void";
            paramTDs.push(type);
         }
         parser = typeParser(returnSig);
         returnType = parser();
         if (returnType == null)
            throw "Cannot parse return type";
         if (parser() != null)
            throw "Extra input at end of signature string";
         return {"params":paramTDs, "return":returnType};
      }

      function add_package(pieces) {
         // should not include the unqual classname
         function helper(curr, name) {
            if (name in curr) {
               return curr[name];
            } else {
               var pack = {};
               curr[name] = pack;
               return pack;
            }
         }

         var parent = Packages;
         for (var i = 0; i < pieces.length; i++) {
            parent = helper(parent, pieces[i]);
         }
      }

      // cont takes a java string
      function js2java_string(str, cont, exc) {
         return function() {
            if (str == null) 
               return cont(null);

            function cont4(obj) {
               return cont(result);
            }
            function cont3(obj) {
               var chararray = new ArrayObject(str.length, new ArrayType(PrimitiveType.CHAR));
               for (var i = 0; i < str.length; i++) {
                  chararray.set(i, new Integer(str.charCodeAt(i) & 0xFFFF));
               }
               return obj.init_AC_([chararray], function() {return cont4(obj);}, exc);
            }
            function cont2(type) {
               return type.newInstance(cont3, exc);
            }
            result Util.resolveClass("java.lang.String", cont2, exc);
         };
      }

      // cont takes a js string
      function java2js_string(str, cont, exc) {
         return function() {
            if (str == null)
               return cont(null);

            function cont2(length, i, result) {
               if (i < length) {
                  function cont3(c) {
                     return cont2(length, i+1, result + String.fromCharCode(c.toJS()));
                  }
                  return str.method_char$At_C_I_([new Integer(i)], cont3, exc);
               } else {
                  return cont(result);
               }
            }
            function cont1(length) {
               return cont2(length.toJS(), 0, "");
            }
            return str.method_length_I__([], cont1, exc);
         };
      }

      // exc expects an exception object
      function resolveAndThrowString(classname, jsstr, exc) {
         function cont1(javastr) {
            function cont2(type) {
               function cont3(obj) {
                  function cont4() {
                     return exc(obj);
                  }
                  return obj.init_Ljava$Dlang$DStringE_([javastr], cont4, exc);
               }
               return type.newInstance(cont3, exc);
            }
            return Util.resolveClass(classname, cont2, exc);
         }
         return js2java_string(jsstr, cont1, exc);
      }

      // exc expects an exception object
      function resolveAndThrow(classname, exc) {
         function cont2(type) {
            function cont3(obj) {
               function cont4() {
                  return exc(obj);
               }
               return obj.init__([], cont4, exc);
            }
            return type.newInstance(cont3, exc);
         }
         return Util.resolveClass(classname, cont2, exc);
      }

      // cont takes an array
      function multianewarray(dims, arraytype, cont, exc) {
         return function() {
            // dims are Integers
            function helper(index, type, mycont, exc) {
               if (index==dims.length) {
                  if (type == PrimitiveType.INT ||
                      type == PrimitiveType.SHORT ||
                      type == PrimitiveType.BOOLEAN ||
                      type == PrimitiveType.BYTE ||
                      type == PrimitiveType.CHAR) {
                     return mycont(Integer.ZERO);
                  } else if (type == PrimitiveType.LONG) {
                     return mycont(Long.ZERO);
                  } else if (type == PrimitiveType.DOUBLE ||
                             type == PrimitiveType.FLOAT) {
                     return mycont(0.0);
                  } else {
                     return mycont(null);
                  }
               }
               if (!type.isArray()) {
                  throw "multianewarray type is not an array: " + type;
               }
               var dim = dims[index].toJS();
               if (dim < 0) {
                  return resolveAndThrow("java.lang.NegativeArraySizeException", exc);
               }

               var array = new ArrayObject(dim, type);
               function cont2(i) {
                  if (i < dim) {
                     function cont3(recurse) {
                        array.set(i, recurse);
                        return cont2(i+1);
                     }
                     return helper(index+1, type.elementType, cont3, exc);
                  } else {
                     return mycont(array);
                  }
               }
               return cont2(0);
            }
            return helper(0, arraytype, cont, exc);
         };
      }

      // assumes neither type is null
      function isAssignableFrom(toType, fromType) {
         if (toType.isPrimitive() && fromType.isPrimitive()) {
            if (toType.which == PrimitiveType.VOID.which || fromType.which == PrimitiveType.VOID.which)
               return false;

            if (toType.which == fromType.which)
               return true;

            switch (fromType.which) {
            case PrimitiveType.BYTE.which:
               switch (toType.which) {
               case PrimitiveType.SHORT.which:
               case PrimitiveType.INT.which:
               case PrimitiveType.LONG.which:
               case PrimitiveType.FLOAT.which:
               case PrimitiveType.DOUBLE.which:
                  return true;
               default:
                  return false;
               }

            case PrimitiveType.SHORT.which:
               switch (toType.which) {
               case PrimitiveType.INT.which:
               case PrimitiveType.LONG.which:
               case PrimitiveType.FLOAT.which:
               case PrimitiveType.DOUBLE.which:
                  return true;
               default:
                  return false;
               }

            case PrimitiveType.CHAR.which:
               switch (toType.which) {
               case PrimitiveType.INT.which:
               case PrimitiveType.LONG.which:
               case PrimitiveType.FLOAT.which:
               case PrimitiveType.DOUBLE.which:
                  return true;
               default:
                  return false;
               }

            case PrimitiveType.INT.which:
               switch (this.toType.which) {
               case PrimitiveType.LONG.which:
               case PrimitiveType.FLOAT.which:
               case PrimitiveType.DOUBLE.which:
                  return true;
               default:
                  return false;
               }
		    
            case PrimitiveType.LONG.which:
               switch (this.toType.which) {
               case PrimitiveType.FLOAT.which:
               case PrimitiveType.DOUBLE.which:
                  return true;
               default:
                  return false;
               }
		    
            case PrimitiveType.FLOAT.which:
               switch (this.toType.which) {
               case PrimitiveType.DOUBLE.which:
                  return true;
               default:
                  return false;
               }

            default:
               return toType.which == fromType.which;
            }
         } else if (toType.isPrimitive() || fromType.isPrimitive()) {
            return false;
         } else {
            // reference types
            return type_extends(fromType, toType);
         }
      }

      // primitives must be equal
      function type_extends(type, target) {
         if (type.isPrimitive() && target.isPrimitive()) {
            if (type.which == PrimitiveType.VOID.which || target.which == PrimitiveType.VOID.which)
               return false;
            else
               return (type.which == target.which);
         } else if (type.isPrimitive() || target.isPrimitive()) {
            return false;
         } else if (target.isArray() && type.isArray()) {
            return type_extends(type.elementType, target.elementType);
         } else if (target.isArray() && !type.isArray()) {
            return false;
         } else if (type.isArray() && !target.isArray()) {
            return target == Packages.java.lang.Object(); // don't need to resolve
         } else {
            // both objects
            if (type == target) {
               return true;
            }
            if (type.superclass) {
               if (type_extends(type.superclass, target))
                  return true;
            }
            for (var i = 0; i < type.interfaces.length; i++) {
               if (type_extends(type.interfaces[i], target))
                  return true;
            }
            return false;
         }
      }
	
      function instance_of(obj, target) {
         if (obj == null)
            return true;
         return type_extends(obj.thisclass, target);
      }

      var native_methods = {};
      function invoke_native(obj, classname, methodname, args, cont, exc) {
         if (classname in native_methods) {
            var classdata = native_methods[classname];
            if (methodname in classdata) {
               var func = classdata[methodname];
               return func.apply(obj, [args, cont, exc]);
            }
         }
         return resolveAndThrowString("java.lang.UnsatisfiedLinkError", classname + "." + methodname, exc);
      }

      // func should take ([args], cont, exc)
      function register_native(classname, methodname, func) {
         var classdata;
         if (classname in native_methods) {
            classdata = native_methods[classname];
         } else {
            classdata = {};
            native_methods[classname] = classdata;
         }
         if (methodname in classdata) {
            throw "Native method " + classname + "." + methodname + " already registered!";
         }
         classdata[methodname] = func;
      }

      function d2l(value) {
         var negative = false;
         if (value < 0) {
            value = -value;
            negative = true;
         }
         var low = (value%2147483648.0) & 0xFFFFFFFF;
         var high = ((value/2147483648.0)%2147483648.0) & 0xFFFFFFFF;
         var l = new Long(low, high);
         return negative ? l.negate() : l;
      }

      function d2i(value) {
         return new Integer(value % 4294967296.0);
      }

      function dcmpg(lhs, rhs) {
         if (isNaN(lhs) || isNaN(rhs)) {
            return Integer.ONE;
         } else if (lhs > rhs) {
            return Integer.ONE;
         } else if (lhs < rhs) {
            return Integer.NEGATIVE_ONE;
         } else {
            return Integer.ZERO;
         }
      }

      function dcmpl(lhs, rhs) {
         if (isNaN(lhs) || isNaN(rhs)) {
            return Integer.NEGATIVE_ONE;
         } else if (lhs > rhs) {
            return Integer.ONE;
         } else if (lhs < rhs) {
            return Integer.NEGATIVE_ONE;
         } else {
            return Integer.ZERO;
         }
      }

      // object, [{type:class_or_null, handler:exc}], exc
      function catch_exception(exception, catchtypes, exc) {
         if (!((typeof exception) == "object" && "thisclass" in exception)) {
            throw "JavaScript exception: " + exception;
         }
         for (var i = 0; i < catchtypes.length; i++) {
            var type = catchtypes[i].type;
            var handler = catchtypes[i].handler;
            if (type == null || instance_of(exception, type)) {
               return handler(exception);
            }
         }
         return exc(exception);
      }

      // initialize supertypes and call clinit
      function initialize(type, cont, exc) {
         if (type.isPrimitive())
            return cont;
         if (type.initialized)
            return cont;
         type.initialized = true;
         
         function cont2() {
            var length = type.interfaces.length;
            function cont5() {
               if ("clinit" in type) {
                  return type.clinit([], cont, exc);
               } else {
                  return cont;
               }
            }
            function cont4() {
               if (type.isArray()) {
                  return initialize(type.elementType, cont5, exc);
               } else {
                  return cont5;
               }
            }
            function cont3(i) {
               if (i < length) {
                  return initialize(type.interfaces[i], cont4, exc);
               } else {
                  return cont4;
               }
            }
            return cont3(0);
         }
         return initialize(type.superclass, cont2, exc);
      }

      function FALSE() {
         return false;
      }

      function TRUE() {
         return true;
      }

      function assertWithException(condition, exceptionName, cont, exc) {
         if (!condition) {
            return resolveAndThrow(exceptionName, exc);
         } else {
            return cont;
         }
      }

      // message should be a JS string
      function assertWithMessageException(condition, message, exceptionName, cont, exc) {
         if (!condition) {
            return resolveAndThrowString(exceptionName, message, exc);
         } else {
            return cont;
         }
      }

      ////////////////////////////////////

      function die(msg) {
         throw msg;
      }

      /*
        info = {
        classname : string,
        superclass : type or null,
        interfaces : [type],
        fields : {
        name : initvalue
        },
        static_fields : {
        name : initvalue,
        ...
        },
        methods : {
        name : func,
        // includes <init>
        ...
        },
        static_methods : {
        name : func
        // includes <clinit>
        ...
        }
        }
      */
      function defineClass(info) {
         var superclass = info.superclass; // can be null
         var interfaces = info.interfaces || die("No interfaces");
         var fields = info.fields || die("No fields");
         var static_fields = info.static_fields || die("No static fields");
         var methods = info.methods || die("No methods");
         var static_methods = info.static_methods || die("No static methods");
         var classname = info.classname || die("No name");
         var isInterface = info.isInterface;
         var modifiers = info.modifiers;
         var memberinfo = info.memberinfo;

         function type() {
            // this should NOT initialize the class (but newInstance does)
            // initialize nonstatic fields
            for (var fieldname in fields) {
               this[fieldname] = fields[fieldname];
            }
         }

         if (superclass) {
            // this should NOT initialize superclass
            type.prototype = new superclass();
         }
         type.prototype.thisclass = type;
         type.isInterface = isInterface ? Util.TRUE : Util.FALSE;
         type.superclass = superclass;
         type.classname = classname;
         type.newInstance = function(cont, exc) {
            function cont2() {
               return cont(new type());
            }
            return Util.initialize(type, cont2, exc);
         };
         type.modifiers = modifiers;
         type.memberinfo = memberinfo;
         type.prototype.toString = function() {
            return "<instance of " + classname + ">";
         };
         type.interfaces = interfaces;
         type.initialized = false;
         type.isArray = FALSE;
         type.isPrimitive = FALSE;
         type.toString = function() {
            return "<type " + classname + ">";
         };

         // initialize static fields
         for (var fieldname in static_fields) {
            type[fieldname] = static_fields[fieldname];
         }

         // initialize methods
         for (var methodname in methods) {
            type.prototype[methodname] = methods[methodname];
         }

         // initialize static methods
         for (var methodname in static_methods) {
            type[methodname] = static_methods[methodname];
         }

         return type;
      }

      ///////////// CPS UP TO HERE /////////////////

      // on failure, throws Java exceptions
      function getClassByName(name) {
         var type = Util.nameToType(name);
         if (type == null) {
            Util.assertWithMessageException(false, name, Util.resolveClass("java.lang.ClassNotFoundException"));
         }

         try {
            return Util.getClass(type);
         } catch (exception) {
            if (!((typeof exception) == "object" && ("thisclass" in exception))) {
               throw "JavaScript exception: " + exception;
            }
            var result = Util.resolveClass("java.lang.ExceptionInInitializerError").newInstance();
            result.init_Ljava$Dlang$DThrowableE_(exception);
            throw result;
         }
      }

      var getClass_cache = {};
      function getClass(type) {
         if (type.classname in getClass_cache) {
            return getClass_cache[type.classname];
         }

         var result = Util.resolveClass("java.lang.Class").newInstance();
         result.pojo = type;
         result.init__();

         getClass_cache[type.classname] = result;

         return result;
      }

      function nameToType(name) {
         var length = name.length;
         if (length > 2 && name.charAt(length-1)=="]" && name.charAt(length-2)=="[") {
            var inner = nameToType(name.substring(0, length-2));
            return new ArrayType(inner);
         }
	    
         if (name == "boolean") {
            return PrimitiveType.BOOLEAN;
         } else if (name == "byte") {
            return PrimitiveType.BYTE;
         } else if (name == "char") {
            return PrimitiveType.CHAR;
         } else if (name == "double") {
            return PrimitiveType.DOUBLE;
         } else if (name == "float") {
            return PrimitiveType.FLOAT;
         } else if (name == "int") {
            return PrimitiveType.INT;
         } else if (name == "long") {
            return PrimitiveType.LONG;
         } else if (name == "short") {
            return PrimitiveType.SHORT;
         } else if (name == "void") {
            return PrimitiveType.VOID;
         } else {
            // object
            var pieces = name.split(".");
            var parent = Packages;
            for (var i = 0; i < pieces.length; i++) {
               if (pieces[i] in parent) {
                  parent = parent[pieces[i]];
               } else {
                  return null;
               }
            }
            return parent();
         }
      }

      function resolveClass(classname, cont, exc) {
         function barf() {
            throw "Cannot find class: " + classname;
         }

         function lookupClass(name) {
            var pieces = name.split(".");
            var parent = Packages;
            for (var i = 0; i < pieces.length; i++) {
               if (!(pieces[i] in parent)) {
                  return null;
               } else {
                  parent = parent[pieces[i]];
               }
            }
            if (!(parent instanceof Function)) {
               return null;
            } else {
               return parent();
            }
         }

         var info = lookupClass(classname);
         if (info != null) {
            return cont(info);
         } else if (classloader != null) {
            // try to load from classloader
            function cont2() {
               info = lookupClass(classname);
               if (info == null) {
                  barf();
               } else {
                  return cont(info);
               }
            }
            classloader.loadClass(classname, cont2);
            // cut point!
            return null;
         } else {
            barf();
         }
      }
      
      function setClassLoader(cl) {
         classloader = cl;
      }

      ////////////////////////////////////
      
      var result = {
         add_package : add_package,
         multianewarray : multianewarray,
         instance_of : instance_of,
         invoke_native : invoke_native,
         register_native : register_native,
         d2l : d2l,
         d2i : d2i,
         dcmpg : dcmpg,
         dcmpl : dcmpl,
         catch_exception : catch_exception,
         initialize : initialize,
         js2java_string : js2java_string,
         java2js_string : java2js_string,
         FALSE : FALSE,
         TRUE : TRUE,
         assertWithException : assertWithException,
         defineClass : defineClass,
         isAssignableFrom : isAssignableFrom,
         getClass : getClass,
         getClassByName : getClassByName,
         nameToType : nameToType,
         resolveClass : resolveClass,
         setClassLoader : setClassLoader,
         resolveAndThrow : resolveAndThrow
      };
      return result;
   })();
