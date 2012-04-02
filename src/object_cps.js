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
                     "getClass()Ljava/lang/Class;" : 0x111,
                     "hashCode()I" : 0x101,
                     "notify()V" : 0x111,
                     "equals(Ljava/lang/Object;)Z" : 0x1,
                     "wait(JI)V" : 0x11,
                     "notifyAll()V" : 0x111,
                     "finalize()V" : 0x4,
                     "<clinit>()V" : 0x8,
                     "wait()V" : 0x11,
                     "toString()Ljava/lang/String;" : 0x1,
                     "<init>()V" : 0x1,
                     "registerNatives()V" : 0x10a,
                     "clone()Ljava/lang/Object;" : 0x104,
                     "wait(J)V" : 0x111
                  },
                  methods : {
                     "<init>()V" : 
                     function(args, cont, exc) {
                        this.hashCode = (Math.random()*0xFFFFFFFF) | 0;
                        return cont(undefined);
                     },

                     "equals(Ljava/lang/Object;)Z" : 
                     function(args, cont, exc) {
                        return cont((this == args[0]) ? Integer.ONE : Integer.ZERO);
                     },
                          
                     "hashCode()I" :
                     function(args, cont, exc) {
                        return cont(new Integer(this.hashcode));
                     },
   
                     "getClass()Ljava/lang/Class;" : 
                     function(args, cont, exc) {
                        return cont(Util.getClass(this.thisclass));
                     },

                     "clone()Ljava/lang/Object;" :
                     function(args, cont, exc) {
                        var frame = new Frame([this], 2);
                        function cont2(obj) {
                           function cont3() {
                              return cont(obj);
                           }
                           return frame.invokespecial([], "java.lang.Object", "<init>()V", cont3, exc);
                        }
                        return Packages.java.lang.Object().newInstance(cont2, exc);
                     },

                     "toString()Ljava/lang/String;" : 
                     function(args, cont, exc) {
                        return cont(Util.js2java_string("@" + this.hashcode, cont, exc));
                     },

                     "notify()V" : 
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     "notifyAll()V" : 
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },
   
                     "wait(JI)V" :
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     "wait(J)V" :
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     "wait()V" :
                     function(args, cont, exc) {
                        // this does nothing
                        // TODO throw exception?
                        return cont(undefined);
                     },

                     "finalize()V" : 
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
