Util.register_native("java2js.Output", "println(Ljava/lang/String;)V", 
                     function(arg0) {
                        var textarea = document.getElementById(Util.java2js_string(this["id.Ljava/lang/String;"]));
                        textarea.value += Util.java2js_string(arg0) + "\n";
                     });
Util.register_native("java2js.Output", "println(D)V", 
                     function(arg0) {
                        var textarea = document.getElementById(Util.java2js_string(this["id.Ljava/lang/String;"]));
                        textarea.value += arg0 + "\n";
                     });
Util.register_native("java2js.Output", "println(I)V", 
                     function(arg0) {
                        var textarea = document.getElementById(Util.java2js_string(this["id.Ljava/lang/String;"]));
                        textarea.value += arg0 + "\n";
                     });
Util.register_native("java2js.Output", "println(J)V", 
                     function(arg0) {
                        var textarea = document.getElementById(Util.java2js_string(this["id.Ljava/lang/String;"]));
                        textarea.value += arg0 + "\n";
                     });
Util.register_native("java.lang.String", "intern()Ljava/lang/String;", 
                     function() {
                        return this;
                     });
Util.register_native("java.lang.System", "arraycopy(Ljava/lang/ObjectlILjava/lang/Object;II)V", 
                     function(src, srcPos, dest, destPos, length) {
                        length = length.toJS();
                        srcPos = srcPos.toJS();
                        destPos = destPos.toJS();
                        for (var i = 0; i < length; i++) {
                           dest.set(i + destPos, src.get(i + srcPos));
                        }
                     });
Util.register_native("java.lang.Throwable", "fillInStackTrace()Ljava/lang/Throwable;", 
                     function() {
                        return this;
                     });
Util.register_native("java.lang.Throwable", "getStackTraceDepth()I", 
                     function() {
                        return Integer.ZERO;
                     });
Util.register_native("java.lang.Throwable", "getStackTraceElement(I)Ljava/lang/StackTraceElement;", 
                     function(arg0) {
                        Util.assertWithException(false, Util.resolveClass("java.lang.IndexOutOfBoundsException"));
                     });

///////////////////////////////////////////////////////////////////////////

Util.register_native("java.lang.Float", "floatToRawIntBits(F)I", 
                     function(arg0) {
                        return Integer.ZERO;
                     });
Util.register_native("java.lang.Double", "doubleToRawLongBits(D)J", 
                     function(arg0) {
                        return Long.ZERO;
                     });
