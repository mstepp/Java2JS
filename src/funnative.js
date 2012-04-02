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
Util.register_native("heart.Animator", "clear()V",
                     function() {
                        this.context.clearRect(0,0,400,400);
                     });
Util.register_native("heart.Animator", "setColor(III)V", 
                     function(arg0, arg1, arg2) {
                        this.context.strokeStyle = "rgb(" + arg0.toJS() + "," + arg1.toJS() + "," + arg2.toJS() + ")";
                     });
Util.register_native("heart.Animator", "drawLine(IIII)V", 
                     function(arg0, arg1, arg2, arg3) {
                        this.context.beginPath();
                        this.context.moveTo(arg0.toJS(), arg1.toJS());
                        this.context.lineTo(arg2.toJS(), arg3.toJS());
                        this.context.closePath();
                        this.context.stroke();
                     });
Util.register_native("heart.Animator", "assignContextPojo()V", 
                     function() {
                        var id = Util.java2js_string(this["id.Ljava/lang/String;"]);
                        var canvas = document.getElementById(id);
                        this.context = canvas.getContext("2d");
                     });
Util.register_native("heart.BezierCurve", "continuation(ILheart/Animator;ILjava/awt/Point;)V", 
                     function(arg0, arg1, arg2, arg3) {
                        var that = this;
                        function cont() {
                           that["draw(Lheart/Animator;ILjava/awt/Point;)V"](arg1, arg2, arg3);
                        }
                        setTimeout(arg0.toJS(), cont);
                     });
