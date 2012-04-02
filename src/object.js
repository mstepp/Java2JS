Util.add_package(["java", "lang"]);
Packages.java.lang.Object = 
   (function() {
      var cached = null;
      function create() {
         if (cached == null) {
            cached = Util.defineClass({
                  classname : "java.lang.Object",
                  superclass : null,
                  interfaces : [],
                  fields : {},
                  static_fields : {},
                  methods : {
                     "<init>()V" : 
                     function() {
                        this.hashCode = (Math.random()*0xFFFFFFFF) | 0;
                     },

                     "equals(Ljava/lang/Object;)Z" : 
                     function(arg0) {
                        return (this == arg0) ? Integer.ONE : Integer.ZERO;
                     },
                          
                     "hashCode()I" :
                     function() {
                        return new Integer(this.hashcode);
                     },
   
                     "getClass()Ljava/lang/Class;" : 
                     function() {
                        return Util.getClass(this.thisclass);
                     },

                     "clone()Ljava/lang/Object;" :
                     function() {
                        return Packages.java.lang.Object().newInstance();
                     },

                     "toString()Ljava/lang/String;" : 
                     function() {
                        return Util.js2java_string("@" + this.hashcode);
                     },

                     "notify()V" : 
                     function() {
                        // this does nothing
                        // TODO throw exception?
                     },

                     "notifyAll()V" : 
                     function() {
                        // this does nothing
                        // TODO throw exception?
                     },
   
                     "wait(JI)V" :
                     function(arg0, arg1) {
                        // this does nothing
                        // TODO throw exception?
                     },

                     "wait(J)V" :
                     function(arg0) {
                        // this does nothing
                        // TODO throw exception?
                     },

                     "wait()V" :
                     function() {
                        // this does nothing
                        // TODO throw exception?
                     },

                     "finalize()V" : 
                     function() {
                        // does nothing
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
