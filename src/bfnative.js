Util.register_native("bf.JSIO", "method_write_V_I_", 
		     function(arg0) {
			 var output = Util.java2js_string(this.field_outputID_Ljava$Dlang$DStringE);
			 document.getElementById(output).value += String.fromCharCode(arg0.toJS());
		     });
