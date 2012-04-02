Util.register_native("bf.JSIO", "write(I)V", 
                     function(arg0) {
                        var output = Util.java2js_string(this["outputID.Ljava/lang/String;"]);
                        document.getElementById(output).value += String.fromCharCode(arg0.toJS());
                     });
