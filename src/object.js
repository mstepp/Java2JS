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
			    init__ : 
			    function() {
				this.hashCode = (Math.random()*0xFFFFFFFF) | 0;
			    },

			    method_equals_Z_Ljava$Dlang$DObjectE_ : 
			    function(arg0) {
                                return (this == arg0) ? Integer.ONE : Integer.ZERO;
			    },
                          
			    method_hashCode_I__ :
			    function() {
                                return new Integer(this.hashcode);
			    },
   
			    method_getClass_Ljava$Dlang$DClassE__ : 
			    function() {
				return Util.getClass(this.thisclass);
			    },

			    method_clone_Ljava$Dlang$DObjectE__ :
			    function() {
				return Packages.java.lang.Object().newInstance();
			    },

			    method_toString_Ljava$Dlang$DString__ : 
			    function() {
                                return Util.js2java_string("@" + this.hashcode);
			    },

			    method_notify_V__ : 
			    function() {
                                // this does nothing
                                // TODO throw exception?
			    },

			    method_notify$All_V__ : 
			    function() {
                                // this does nothing
                                // TODO throw exception?
			    },
   
			    method_wait_V_JI_ :
			    function(arg0, arg1) {
                                // this does nothing
                                // TODO throw exception?
			    },

			    method_wait_V_J_ :
			    function(arg0) {
                                // this does nothing
                                // TODO throw exception?
			    },

			    method_wait_V__ :
			    function() {
                                // this does nothing
                                // TODO throw exception?
			    },

			    method_finalize_V__ : 
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
