Util.register_native("java2js.Output", "method_println_V_Ljava$Dlang$DStringE_", 
		     function(arg0) {
			 var textarea = document.getElementById(Util.java2js_string(this.field_id_Ljava$Dlang$DStringE));
			 textarea.value += Util.java2js_string(arg0) + "\n";
		     });
Util.register_native("java2js.Output", "method_println_V_D_", 
		     function(arg0) {
			 var textarea = document.getElementById(Util.java2js_string(this.field_id_Ljava$Dlang$DStringE));
			 textarea.value += arg0 + "\n";
		     });
Util.register_native("java2js.Output", "method_println_V_I_", 
		     function(arg0) {
			 var textarea = document.getElementById(Util.java2js_string(this.field_id_Ljava$Dlang$DStringE));
			 textarea.value += arg0 + "\n";
		     });
Util.register_native("java2js.Output", "method_println_V_J_", 
		     function(arg0) {
			 var textarea = document.getElementById(Util.java2js_string(this.field_id_Ljava$Dlang$DStringE));
			 textarea.value += arg0 + "\n";
		     });
Util.register_native("heart.Animator", "method_clear_V__",
		     function() {
			 this.context.clearRect(0,0,400,400);
		     });
Util.register_native("heart.Animator", "method_setColor_V_III_", 
		     function(arg0, arg1, arg2) {
			 this.context.strokeStyle = "rgb(" + arg0.toJS() + "," + arg1.toJS() + "," + arg2.toJS() + ")";
		     });
Util.register_native("heart.Animator", "method_draw$Line_V_IIII_", 
		     function(arg0, arg1, arg2, arg3) {
			 this.context.beginPath();
			 this.context.moveTo(arg0.toJS(), arg1.toJS());
			 this.context.lineTo(arg2.toJS(), arg3.toJS());
			 this.context.closePath();
			 this.context.stroke();
		     });
Util.register_native("heart.Animator", "method_assignContextPojo_V__", 
		     function() {
			 var id = Util.java2js_string(this.field_id_Ljava$Dlang$DStringE);
			 var canvas = document.getElementById(id);
			 this.context = canvas.getContext("2d");
		     });
Util.register_native("heart.BezierCurve", "method_continuation_V_ILheart$D$AnimatorEILjava$Dawt$DPointE_", 
		     function(arg0, arg1, arg2, arg3) {
			 var that = this;
			 function cont() {
			     that.method_draw_V_Lheart$D$AnimatorEILjava$Dawt$DPointE_(arg1, arg2, arg3);
			 }
			 setTimeout(arg0.toJS(), cont);
		     });
