function pojo2JSObject(pojo) {
   if (pojo != null && (typeof pojo) != "undefined") {
      var result = Packages.java2js.JSObject().newInstance();
      result["<init>()V"]();
      result.pojo = pojo;
      return result;
   } else {
      return null;
   }
}

function pojo2Element(pojo) {
   if (pojo != null && (typeof pojo) != "undefined") {
      var element = Packages.java2js.Element().newInstance();
      element["<init>()V"]();
      element.pojo = pojo;
      return element;
   } else {
      return null;
   }
}

function function2pojo(f) {
   var pojo = function() {
      var args = new ArrayObject(arguments.length, new ArrayType(Packages.java2js.JSObject()));
      for (var i = 0; i < arguments.length; i++) {
         args.set(i, pojo2JSObject(arguments[i]));
      }
      var result = f["apply(Ljava2js/JSObject;[Ljava2js/JSObject;)Ljava2js/JSObject;"](pojo2JSObject(this), args);
      if (result != null) {
         return result.pojo;
      } else {
         return null;
      }
   };
   return pojo;
}

function nativeFunction(f) {
   var result = Packages.java2js.NativeJSFunction().newInstance();
   result["<init>()V"]();
   result.pojo = f;
   return result;
}

Util.register_native("java2js.JSObject", "setProperty(Ljava/lang/String;Ljava2js/JSObject;)V",
                     function(name, value) {
                        this.pojo[Util.java2js_string(name)] = value.pojo;
                     });

Util.register_native("java2js.JSObject", "getProperty(Ljava/lang/String;)Ljava2js/JSObject;",
                     function(name) {
                        var prop = this.pojo[Util.java2js_string(name)];
                        return pojo2JSObject(prop);
                     });

Util.register_native("java2js.JSObject", "invokeMethod(Ljava/lang/String;[Ljava2js/JSObject;)Ljava2js/JSObject;",
                     function(name, params) {
                        var args = [];
                        for (var i = 0; i < params.length; i++) {
                           args.push(params.get(i).pojo);
                        }
                        var resultpojo = this.pojo[Util.java2js_string(name)].apply(this.pojo, args);
                        return pojo2JSObject(resultpojo);
                     });

Util.register_native("java2js.JSObject", "asArray()[Ljava2js/JSObject;",
                     function() {
                        if (this.pojo instanceof Array) {
                           var result = new ArrayObject(this.pojo.length, new ArrayType(Packages.java2js.JSObject()));
                           for (var i = 0; i < this.pojo.length; i++) {
                              result.set(i, this.pojo[i]);
                           }
                           return result;
                        } else {
                           return null;
                        }
                     });

Util.register_native("java2js.JSObject", "asNumber()Ljava/lang/Double;",
                     function() {
                        if ((typeof this.pojo).toLowerCase() == "number") {
                           var result = Packages.java.lang.Double().newInstance();
                           result["<init>(D)V"](this.pojo);
                           return result;
                        } else {
                           return null;
                        }
                     });

Util.register_native("java2js.JSObject", "asString()Ljava/lang/String;",
                     function() {
                        if ((typeof this.pojo).toLowerCase() == "string") {
                           return Util.js2java_string(this.pojo);
                        } else {
                           return null;
                        }
                     });

Util.register_native("java2js.JSObject", "asElement()Ljava2js/Element;",
                     function() {
                        if ("appendChild" in this.pojo) {
                           return pojo2Element(this.pojo);
                        } else {
                           return null;
                        }
                     });

Util.register_native("java2js.JSObject", "asBoolean()Z",
                     function() {
                        return this.pojo ? Integer.ONE : Integer.ZERO;
                     });

Util.register_native("java2js.JSObject", "empty()Ljava2js/JSObject;",
                     function() {
                        return pojo2JSObject({});
                     });

Util.register_native("java2js.JSObject", "fromString(Ljava/lang/String;)Ljava2js/JSObject;",
                     function(str) {
                        return pojo2JSObject(Util.java2js_string(str));
                     });

Util.register_native("java2js.JSObject", "fromArray([Ljava2js/JSObject;)Ljava2js/JSObject;",
                     function(array) {
                        var result = [];
                        for (var i = 0; i < array.length; i++) {
                           result.push(array.get(i));
                        }
                        return pojo2JSObject(result);
                     });

Util.register_native("java2js.JSObject", "fromNumber(D)Ljava2js/JSObject;",
                     function(d) {
                        return pojo2JSObject(d);
                     });

Util.register_native("java2js.JSObject", "fromBoolean(Z)Ljava2js/JSObject;",
                     function(b) {
                        return pojo2JSObject(!b.isFalse());
                     });

Util.register_native("java2js.JSObject", "fromFunction(Ljava2js/JSFunction;)Ljava2js/JSObject;",
                     function(f) {
                        var result = Packages.java2js.JSFunction().newInstance();
                        result["<init>()V"]();
                        result.pojo = function2pojo(f);
                        return result;
                     });

Util.register_native("java2js.Element", "getElementById(Ljava/lang/String;)Ljava2js/Element;",
                     function(id) {
                        var pojo = document.getElementById(Util.java2js_string(id));
                        if (pojo) {
                           return pojo2Element(pojo);
                        } else {
                           return null;
                        }
                     });

Util.register_native("java2js.Element", "addEventHandler(Ljava/lang/String;Ljava2js/EventCallback;)V",
                     function(eventName, callback) {
                        this.pojo[Util.java2js_string(eventName)] = function(e) {
                           var result = callback["onevent(Ljava2js/Element;Ljava2js/JSObject;)Z"](this, pojo2JSObject(e));
                           return result ? Integer.ONE : Integer.ZERO;
                        };
                     });

Util.register_native("java2js.Element", "registerNatives()V",
                     function() {
                        var doc = pojo2Element(document);
                        var win = pojo2Element(window);
                        Packages.java2js.Element()["DOCUMENT.Ljava2js/Element;"] = doc;
                        Packages.java2js.Element()["WINDOW.Ljava2js/Element;"] = win;
                     });

Util.register_native("java2js.JSObject", "setTimeout(Ljava2js/JSFunction;I)V",
                     function(func, ms) {
                        var pojo = function2pojo(func);
                        setTimeout(pojo, ms.toJS());
                     });

Util.register_native("java2js.NativeJSFunction", "apply(Ljava2js/JSObject;[Ljava2js/JSObject;)Ljava2js/JSObject;",
                     function(target, args) {
                        var newargs = [];
                        for (var i = 0; i < args.length; i++) {
                           newargs.push(args.get(i).pojo);
                        }
                        var newtarget = target ? target.pojo : null;
                        var result = this.pojo.apply(newtarget, newargs);
                        return pojo2JSObject(result);
                     });

Util.register_native("java.lang.StrictMath", "sqrt(D)D",
                     function(value) {
                        return Math.sqrt(value);
                     });

Util.register_native("java.lang.StrictMath", "cos(D)D",
                     function(value) {
                        return Math.cos(value);
                     });

Util.register_native("java.lang.StrictMath", "sin(D)D",
                     function(value) {
                        return Math.sin(value);
                     });

