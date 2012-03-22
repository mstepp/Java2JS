Util.add_package(["java", "lang"]);
Packages.java.lang.Object = 
   (function() {
      var cached = null;
      function create() {
         if (cached == null) {
            cached = Util.defineClass({
                  classname : "java.lang.Object",
                  modifiers : 0x21,
                  isInterface : false,
                  superclass : null,
                  interfaces : [],
                  fields : {},
                  static_fields : {},
                  memberinfo : {
                     method_getClass_Ljava$Dlang$DClassE__ : 0x111,
                     method_hashCode_I__ : 0x101,
                     method_notify_V__ : 0x111,
                     method_equals_Z_Ljava$Dlang$DObjectE_ : 0x1,
                     method_wait_V_JI_ : 0x11,
                     method_notify$All_V__ : 0x111,
                     method_finalize_V__ : 0x4,
                     clinit : 0x8,
                     method_wait_V__ : 0x11,
                     method_toString_Ljava$Dlang$DStringE__ : 0x1,
                     init__ : 0x1,
                     method_registerNatives_V__ : 0x10a,
                     method_clone_Ljava$Dlang$DObjectE__ : 0x104,
                     method_wait_V_J_ : 0x111
                  },
                  methods : {
                     init__ : 
                     function(args, cont, exc) {
                        this.hashCode = (Math.random()*0xFFFFFFFF) | 0;
                        return cont(undefined);
                     },

                     method_equals_Z_Ljava$Dlang$DObjectE_ : 
                     function(args, cont, exc) {
                        return cont((this == args[0]) ? Integer.ONE : Integer.ZERO);
                     },
                          
                     method_hashCode_I__ :
                     function(args, cont, exc) {
                        return cont(new Integer(this.hashcode));
                     },
   
                     method_getClass_Ljava$Dlang$DClassE__ : 
                     function(args, cont, exc) {
                        return cont(Util.getClass(this.thisclass));
                     },

                     method_clone_Ljava$Dlang$DObjectE__ :
                     function(args, cont, exc) {
                        var frame = new Frame([this], 2);
                        function cont2(obj) {
                           function cont3() {
                              return cont(obj);
                           }
                           return frame.invokespecial([], new ClassTD("java.lang.Object"), "java.lang.Object", "init__", cont3, exc);
                        }
                        return Packages.java.lang.Object().newInstance(cont2, exc);
                     },

                     method_toString_Ljava$Dlang$DString__ : 
                     function(args, cont, exc) {
                        return cont(Util.js2java_string("@" + this.hashcode, cont, exc));
                     },

                     method_notify_V__ : 
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     method_notify$All_V__ : 
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },
   
                     method_wait_V_JI_ :
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     method_wait_V_J_ :
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     method_wait_V__ :
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     method_finalize_V__ : 
                     function(args, cont, exc) {
                        // does nothing
                        return cont(undefined);
                     }
                  },
                  static_methods : {},
               });
            cached.initialized = true;
         }
         return cached;
      }
      return create;
   })();
