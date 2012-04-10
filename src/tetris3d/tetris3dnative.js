function pojo2JSObject(pojo) {
   if (pojo != null && (typeof pojo) != "undefined") {
      var result = Packages.tetris3d.JSObject().newInstance();
      result["<init>()V"]();
      result.pojo = pojo;
      return result;
   } else {
      return null;
   }
}

function pojo2Element(pojo) {
   if (pojo != null && (typeof pojo) != "undefined") {
      var element = Packages.tetris3d.Element().newInstance();
      element["<init>()V"]();
      element.pojo = pojo;
      return element;
   } else {
      return null;
   }
}

function function2pojo(f) {
   var pojo = function() {
      var args = new ArrayObject(arguments.length, new ArrayType(Packages.tetris3d.JSObject()));
      for (var i = 0; i < arguments.length; i++) {
         args.set(i, pojo2JSObject(arguments[i]));
      }
      var result = f["apply(Ltetris3d/JSObject;[Ltetris3d/JSObject;)Ltetris3d/JSObject;"](pojo2JSObject(this), args);
      if (result != null) {
         return result.pojo;
      } else {
         return null;
      }
   };
   return pojo;
}

function nativeFunction(f) {
   var result = Packages.tetris3d.NativeFunction().newInstance();
   result["<init>()V"]();
   result.pojo = f;
   return result;
}

Util.register_native("tetris3d.JSObject", "setProperty(Ljava/lang/String;Ltetris3d/JSObject;)V",
                     function(name, value) {
                        this.pojo[Util.java2js_string(name)] = value.pojo;
                     });

Util.register_native("tetris3d.JSObject", "getProperty(Ljava/lang/String;)Ltetris3d/JSObject;",
                     function(name) {
                        var prop = this.pojo[Util.java2js_string(name)];
                        return pojo2JSObject(prop);
                     });

Util.register_native("tetris3d.JSObject", "invokeMethod(Ljava/lang/String;[Ltetris3d/JSObject;)Ltetris3d/JSObject;",
                     function(name, params) {
                        var args = [];
                        for (var i = 0; i < params.length; i++) {
                           args.push(params.get(i).pojo);
                        }
                        var resultpojo = this.pojo[Util.java2js_string(name)].apply(this.pojo, args);
                        return pojo2JSObject(resultpojo);
                     });

Util.register_native("tetris3d.JSObject", "asNumber()Ljava/lang/Double;",
                     function() {
                        if ((typeof this.pojo).toLowerCase() == "number") {
                           var result = Packages.java.lang.Double().newInstance();
                           result["<init>(D)V"](this.pojo);
                           return result;
                        } else {
                           return null;
                        }
                     });

Util.register_native("tetris3d.JSObject", "asString()Ljava/lang/String;",
                     function() {
                        if ((typeof this.pojo).toLowerCase() == "string") {
                           return Util.js2java_string(this.pojo);
                        } else {
                           return null;
                        }
                     });

Util.register_native("tetris3d.JSObject", "asElement()Ltetris3d/Element;",
                     function() {
                        if ("appendChild" in this.pojo) {
                           return pojo2Element(this.pojo);
                        } else {
                           return null;
                        }
                     });

Util.register_native("tetris3d.JSObject", "asBoolean()Z",
                     function() {
                        return this.pojo ? Integer.ONE : Integer.ZERO;
                     });

Util.register_native("tetris3d.JSObject", "fromString(Ljava/lang/String;)Ltetris3d/JSObject;",
                     function(str) {
                        return pojo2JSObject(Util.java2js_string(str));
                     });

Util.register_native("tetris3d.JSObject", "fromNumber(D)Ltetris3d/JSObject;",
                     function(d) {
                        return pojo2JSObject(d);
                     });

Util.register_native("tetris3d.JSObject", "fromBoolean(Z)Ltetris3d/JSObject;",
                     function(b) {
                        return pojo2JSObject(!b.isFalse());
                     });

Util.register_native("tetris3d.JSObject", "fromFunction(Ltetris3d/Function;)Ltetris3d/JSObject;",
                     function(f) {
                        var result = Packages.tetris3d.Function().newInstance();
                        result["<init>()V"]();
                        result.pojo = function2pojo(f);
                        return result;
                     });

Util.register_native("tetris3d.Element", "getElementById(Ljava/lang/String;)Ltetris3d/Element;",
                     function(id) {
                        var pojo = document.getElementById(Util.java2js_string(id));
                        if (pojo) {
                           return pojo2Element(pojo);
                        } else {
                           return null;
                        }
                     });

Util.register_native("tetris3d.Element", "addEventHandler(Ljava/lang/String;Ltetris3d/EventCallback;)V",
                     function(eventName, callback) {
                        this.pojo[Util.java2js_string(eventName)] = function(e) {
                           var result = callback["onevent(Ltetris3d/Element;Ltetris3d/JSObject;)Z"](this, pojo2JSObject(e));
                           return result ? Integer.ONE : Integer.ZERO;
                        };
                     });

Util.register_native("tetris3d.Element", "registerNatives()V",
                     function() {
                        var doc = pojo2Element(document);
                        var win = pojo2Element(window);
                        Packages.tetris3d.Element()["DOCUMENT.Ltetris3d/Element;"] = doc;
                        Packages.tetris3d.Element()["WINDOW.Ltetris3d/Element;"] = win;
                     });

Util.register_native("tetris3d.JSObject", "setTimeout(Ltetris3d/Function;I)V",
                     function(func, ms) {
                        var pojo = function2pojo(func);
                        setTimeout(pojo, ms.toJS());
                     });

Util.register_native("tetris3d.NativeFunction", "apply(Ltetris3d/JSObject;[Ltetris3d/JSObject;)Ltetris3d/JSObject;",
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

Util.register_native("tetris3d.Tetris", "random()D",
                     function() {
                        return Math.random();
                     });

Util.register_native("tetris3d.Tetris", "currentTimeMillis()J",
                     function() {
                        return Util.d2l(new Date().getTime());
                     });
