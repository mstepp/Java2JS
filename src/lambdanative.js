Util.register_native("lambda.JSPrinter", "method_nativePrint_V_Ljava$Dlang$DStringE_", 
		     function(arg0) {
			 this.textarea.value += Util.java2js_string(arg0);
		     });

Util.register_native("lambda.Debug", "method_alert_V_Ljava$Dlang$DStringE_", 
		     function(arg0) {
			 alert(Util.java2js_string(arg0));
		     });

Util.invoke_native("lambda.Debug", "method_print_V_Ljava$Dlang$DStringE_", 
		   function(arg0) {
		       // TODO

		   });
