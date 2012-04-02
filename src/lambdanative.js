Util.register_native("lambda.JSPrinter", "nativePrint(Ljava/lang/String;)V", 
                     function(arg0) {
                        this.textarea.value += Util.java2js_string(arg0);
                     });

Util.register_native("lambda.Debug", "alert(Ljava/lang/String;)V", 
                     function(arg0) {
                        alert(Util.java2js_string(arg0));
                     });

Util.invoke_native("lambda.Debug", "print(Ljava/lang/String;)V", 
                   function(arg0) {
                      // TODO

                   });
