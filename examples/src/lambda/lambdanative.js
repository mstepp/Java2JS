Util.register_native("lambda.JSPrinter", "nativePrint(Ljava/lang/String;)V", 
                     function(arg0) {
                        this.textarea.value += Util.java2js_string(arg0);
                     });

Util.register_native("sun.misc.Unsafe", "registerNatives()V",
                     function() {});
