Util.register_native("heart.Animator", "assignContextPojo()V",
                     function() {
                        var id = Util.java2js_string(this["id.Ljava/lang/String;"]);
                        var canvas = document.getElementById(id);
                        this.context = canvas.getContext("2d");
                     });

Util.register_native("heart.Animator", "clear()V",
                     function() {
                        this.context.clearRect(0,0,500,500);
                     });

Util.register_native("heart.Animator", "setColor(III)V",
                     function(r, g, b) {
                        this.context.strokeStyle = "rgb(" + r.toJS() + "," + g.toJS() + "," + b.toJS() + ")";
                     });

Util.register_native("heart.Animator", "drawLine(IIII)V",
                     function(x1, y1, x2, y2) {
                        this.context.beginPath();
                        this.context.moveTo(x1.toJS(), y1.toJS());
                        this.context.lineTo(x2.toJS(), y2.toJS());
                        this.context.stroke();
                     });

Util.register_native("heart.Animator", "setTimeout(Ljava/lang/Runnable;I)V",
                     function(runnable, timeout) {
                        window.setTimeout(function() {
                              runnable["run()V"]();
                           }, timeout.toJS());
                     });
