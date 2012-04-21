Util.register_native("java.lang.StrictMath", "sqrt(D)D",
                     function(value) {
                        return Math.sqrt(value);
                     });

Util.register_native("java.lang.StrictMath", "cos(D)D",
                     function(value) {
                        return Math.cos(value);
                     });

Util.register_native("java.lang.StrictMath", "sin(D)D",
                     function(value) {
                        return Math.sin(value);
                     });

Util.register_native("tetris3d.Tetris", "random()D",
                     function() {
                        return Math.random();
                     });

Util.register_native("tetris3d.Tetris", "currentTimeMillis()J",
                     function() {
                        return Util.d2l(new Date().getTime());
                     });
