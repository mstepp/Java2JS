var Packages = {};

var Util = 
   (function() {
      var classloader = null;

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

      function js2java_string(str) {
         if (str == null) 
            return null;
         var bytearray = new ArrayObject(str.length, new ArrayType(PrimitiveType.CHAR));
         for (var i = 0; i < str.length; i++) {
            bytearray.set(i, new Integer(str.charCodeAt(i) & 0xFFFF));
         }
         var result = Util.resolveClass("java.lang.String").newInstance();
         result.init_AC_(bytearray);
         return result;
      }

      function java2js_string(str) {
         if (str == null)
            return null;
         var result = "";
         var length = str.method_length_I__().toJS();
         for (var i = 0; i < length; i++) {
            var c = str.method_char$At_C_I_(new Integer(i)).toJS();
            result += String.fromCharCode(c);
         }
         return result;
      }

      function multianewarray(dims, arraytype) {
         // dims are Integers
         function helper(index, type) {
            if (index==dims.length) {
               if (type == PrimitiveType.INT ||
                   type == PrimitiveType.SHORT ||
                   type == PrimitiveType.BOOLEAN ||
                   type == PrimitiveType.BYTE ||
                   type == PrimitiveType.CHAR) {
                  return Integer.ZERO;
               } else if (type == PrimitiveType.LONG) {
                  return Long.ZERO;
               } else if (type == PrimitiveType.DOUBLE ||
                          type == PrimitiveType.FLOAT) {
                  return 0.0;
               } else {
                  return null;
               }
            }
            if (!type.isArray()) {
               throw "multianewarray type is not an array: " + type;
            }
            var dim = dims[index].toJS();
            if (dim < 0) {
               var exception = Util.resolveClass("java.lang.NegativeArraySizeException").newInstance();
               exception.init__();
               throw exception;
            }
            var array = new ArrayObject(dim, type);
            for (var i = 0; i < dim; i++) {
               array.set(i, helper(index+1, type.elementType));
            }
            return array;
         }
         return helper(0, arraytype);
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
      function invoke_native(obj, classname, methodname, args) {
         if (classname in native_methods) {
            var classdata = native_methods[classname];
            if (methodname in classdata) {
               var func = classdata[methodname];
               return func.apply(obj, args);
            }
         }

         var exception = Util.resolveClass("java.lang.UnsatisfiedLinkError").newInstance();
         exception.init_Ljava$Dlang$DStringE_(Util.js2java_string(classname + "." + methodname));
         throw exception;
      }

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

      // object, [{type:class_or_null, index:block_index}]
      function catch_exception(exception, catchtypes) {
         if (!((typeof exception) == "object" && "thisclass" in exception)) {
            throw "JavaScript exception: " + exception;
         }

         for (var i = 0; i < catchtypes.length; i++) {
            var type = catchtypes[i].type;
            var index = catchtypes[i].index;
            if (type == null || instance_of(exception, type)) {
               return index;
            }
         }
         throw exception;
      }

      // initialize supertypes and call clinit
      function initialize(type) {
         if (type.isPrimitive())
            return;
         if (type.initialized)
            return;
         type.initialized = true;
         
         Util.initialize(type.superclass);
         for (var i = 0; i < type.interfaces.length; i++) {
            Util.initialize(type.interfaces[i]);
         }
         if (type.isArray()) {
            initialize(type.elementType);
         }

         if ("clinit" in type) {
            type.clinit();
         }
      }

      function FALSE() {
         return false;
      }

      function TRUE() {
         return true;
      }

      function assertWithException(condition, exceptionType) {
         if (!condition) {
            var exception = exceptionType.newInstance();
            exception.init__();
            throw exception;
         }
      }

      // message should be a JS string
      function assertWithMessageException(condition, message, exceptionType) {
         if (!condition) {
            var exception = exceptionType.newInstance();
            exception.init_Ljava$Dlang$DStringE_(Util.js2java_string(message));
            throw exception;
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
         type.newInstance = function() {
            Util.initialize(type);
            return new type();
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

      function resolveClass(classname) {
         function barf() {
            throw "Cannot find class: " + classname;
            //		var exception = Util.resolveClass("java.lang.NoClassDefFoundError");
            //		exception.init__();
            //		throw exception;
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
         if (info == null && classloader != null) {
            // try to load from classloader
            function callback() {
               var info = lookupClass(classname);
               if (info == null) {
                  barf();
               }
               // TODO!!!!! make this synchronous!!!

            }



            classloader.loadClass(classname);
            info = lookupClass(classname);
         }
         if (info == null) {
            barf();
         } else {
            return info;
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
         setClassLoader : setClassLoader
      };
      return result;
   })();
