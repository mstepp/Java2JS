Util.register_native("java.lang.String", "method_intern_Ljava$Dlang$DStringE__", 
		     function() {
			 return this;
		     });
Util.register_native("java.lang.System", "method_arraycopy_V_Ljava$Dlang$DObjectEILjava$Dlang$DObjectEII_", 
		     function(src, srcPos, dest, destPos, length) {
			 length = length.toJS();
			 srcPos = srcPos.toJS();
			 destPos = destPos.toJS();
			 for (var i = 0; i < length; i++) {
			     dest.set(i + destPos, src.get(i + srcPos));
			 }
		     });
Util.register_native("java.lang.Throwable", "method_fillInStackTrace_Ljava$Dlang$DThrowableE__", 
		     function() {
			 return this;
		     });
Util.register_native("java.lang.Throwable", "method_getStackTraceDepth_I__", 
		     function() {
			 return Integer.ZERO;
		     });
Util.register_native("java.lang.Throwable", "method_getStackTrace$Element_Ljava$Dlang$DStackTrace$ElementE_I_", 
		     function(arg0) {
			 Util.assertWithException(false, Util.resolveClass("java.lang.IndexOutOfBoundsException"));
		     });

///////////////////////////////////////////////////////////////////////////

Util.register_native("java.lang.Float", "method_floatToRawIntBits_I_F_", 
		     function(arg0) {
			 return Integer.ZERO;
		     });
Util.register_native("java.lang.Double", "method_doubleToRaw$LongBits_J_D_", 
		     function(arg0) {
			 return Long.ZERO;
		     });
