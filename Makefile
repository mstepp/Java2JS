SHELL := /bin/bash
JAVA_SRC := $(wildcard src/java2js/*.java)
JAVA_CLASS := $(patsubst src/%.java,bin/%.class,$(JAVA_SRC))
HEART_SRC := $(wildcard examples/src/heart/*.java)
FACTORIAL_SRC := $(wildcard examples/src/factorial/*.java)
ARITHMETIC_SRC := $(wildcard examples/src/arithmetic/*.java)
LAMBDA_SRC := $(wildcard examples/src/lambda/*.java)
BF_SRC := $(wildcard examples/src/bf/*.java)

.SECONDARY:

.PHONY:	java all

all:	java

java:	$(JAVA_CLASS)

bin/%.class:	src/%.java
	javac -Xlint -cp bin/:bcel-5.2.jar -d bin/ -sourcepath src/ $< ;

js/%.js:
	CLASSFILE='$@' ; \
	CLASSFILE="$${CLASSFILE#js/}" ; \
	CLASSFILE="$${CLASSFILE%.js}" ; \
	CLASSFILE="$${CLASSFILE//\$$/\$$\$$}" ; \
	mkdir -p `dirname '$@'` ; \
	$(MAKE) -s "CLASS=$${CLASSFILE}.class" compile > '$@' ; \
	chmod -R a+r '$@' ;

# needs CLASS=java/lang/String.class
compile:	java
	java -cp bcel-5.2.jar:bin/ java2js.CompileByName '$(CLASS)' ;

# needs FOLDER
compile_example:	java
	javac -cp bin/:bcel-5.2.jar -d bin/ -sourcepath src/:examples/src/$(FOLDER)/ examples/src/$(FOLDER)/*.java ;

# needs FILE, OUTPUT
compile_package:
	for i in `cat $(FILE)` ; do \
		i="$${i//\.//}" ; \
		i="js/$$i.js" ; \
		$(MAKE) -s "$$i" ; \
		echo "    <script type=\"text/javascript\" src=\"../$$i\"></script>" >> $(OUTPUT) ; \
	done ;

# needs OUTPUT
testheader:
	echo '<html xmlns="http://www.w3.org/1999/xhtml">' > "$(OUTPUT)" ;
	echo '  <head>' >> "$(OUTPUT)" ;
	echo '    <title>Testing</title>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/util.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/native.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/reflect_native.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/int.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/long.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/object.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/arrayobject.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../src/classloader.js"></script>' >> "$(OUTPUT)" ;

# needs OUTPUT
testfooter:
	echo '  </head>' >> "$(OUTPUT)" ;
	echo '  <body>' >> "$(OUTPUT)" ;
	echo '    Testing...' >> "$(OUTPUT)" ;
	echo '    <br/><textarea id="stdout" rows="10" cols="50"></textarea>' >> "$(OUTPUT)" ;
	echo '    <br/><textarea id="stderr" rows="10" cols="50"></textarea>' >> "$(OUTPUT)" ;
	echo '  </body>' >> "$(OUTPUT)" ;
	echo '</html>' >> "$(OUTPUT)" ;
	chmod a+r "$(OUTPUT)" ;

compile_arithmetic:
	$(MAKE) FOLDER=arithmetic compile_example ;

test_arithmetic_static:	compile_arithmetic
	$(MAKE) -s OUTPUT=bin/test_arithmetic.html testheader ;
	$(MAKE) -s FILE=examples/src/arithmetic/package OUTPUT=bin/test_arithmetic.html compile_package ;
	echo '    <script type="text/javascript">' >> bin/test_arithmetic.html ;
	echo '       window.onload = function() {' >> bin/test_arithmetic.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> bin/test_arithmetic.html ;
	echo '         Util.resolveClass("arithmetic.Arithmetic")["main([Ljava/lang/String;)V"](args);' >> bin/test_arithmetic.html ;
	echo '       };' >> bin/test_arithmetic.html ;
	echo '    </script>' >> bin/test_arithmetic.html ;
	$(MAKE) -s OUTPUT=bin/test_arithmetic.html testfooter ;

test_arithmetic_dynamic:	compile_arithmetic
	$(MAKE) -s OUTPUT=bin/test_arithmetic.html testheader ;
	echo '    <script type="text/javascript">' >> bin/test_arithmetic.html ;
	echo '       var stderr = null;' >> bin/test_arithmetic.html ;
	echo '       function logger(str) {' >> bin/test_arithmetic.html ;
	echo '          stderr.value += str + "\n";' >> bin/test_arithmetic.html ;
	echo '       }' >> bin/test_arithmetic.html ;
	echo '       window.onload = function() {' >> bin/test_arithmetic.html ;
	echo '         stderr = document.getElementById("stderr");' >> bin/test_arithmetic.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> bin/test_arithmetic.html ;
	echo '         classloader.setDocument(document);' >> bin/test_arithmetic.html ;
	echo '         classloader.setLogger(logger);' >> bin/test_arithmetic.html ;
	echo '         Util.setClassLoader(classloader);' >> bin/test_arithmetic.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> bin/test_arithmetic.html ;
	echo '         Util.resolveClass("arithmetic.Arithmetic")["main([Ljava/lang/String;)V"](args);' >> bin/test_arithmetic.html ;
	echo '       };' >> bin/test_arithmetic.html ;
	echo '    </script>' >> bin/test_arithmetic.html ;
	$(MAKE) -s OUTPUT=bin/test_arithmetic.html testfooter ;

compile_factorial:
	$(MAKE) FOLDER=factorial compile_example ;

test_factorial_static:	compile_factorial
	$(MAKE) -s OUTPUT=bin/test_factorial.html testheader ;
	$(MAKE) -s OUTPUT=bin/test_factorial.html FILE=examples/src/factorial/package compile_package ;
	echo '    <script type="text/javascript">' >> bin/test_factorial.html ;
	echo '       window.onload = function() {' >> bin/test_factorial.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> bin/test_factorial.html ;
	echo '         Util.resolveClass("factorial.Factorial")["main([Ljava/lang/String;)V"](args);' >> bin/test_factorial.html ;
	echo '       };' >> bin/test_factorial.html ;
	echo '    </script>' >> bin/test_factorial.html ;
	$(MAKE) -s OUTPUT=bin/test_factorial.html testfooter ;

test_factorial_dynamic:	compile_factorial
	$(MAKE) -s OUTPUT=bin/test_factorial.html testheader ;
	echo '    <script type="text/javascript">' >> bin/test_factorial.html ;
	echo '       var stderr = null;' >> bin/test_factorial.html ;
	echo '       function logger(str) {' >> bin/test_factorial.html ;
	echo '          stderr.value += str + "\n";' >> bin/test_factorial.html ;
	echo '       }' >> bin/test_factorial.html ;
	echo '       window.onload = function() {' >> bin/test_factorial.html ;
	echo '         stderr = document.getElementById("stderr");' >> bin/test_factorial.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> bin/test_factorial.html ;
	echo '         classloader.setDocument(document);' >> bin/test_factorial.html ;
	echo '         classloader.setLogger(logger);' >> bin/test_factorial.html ;
	echo '         Util.setClassLoader(classloader);' >> bin/test_factorial.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> bin/test_factorial.html ;
	echo '         Util.resolveClass("factorial.Factorial")["main([Ljava/lang/String;)V"](args);' >> bin/test_factorial.html ;
	echo '       };' >> bin/test_factorial.html ;
	echo '    </script>' >> bin/test_factorial.html ;
	$(MAKE) -s OUTPUT=bin/test_factorial.html testfooter ;

compile_heart:
	$(MAKE) FOLDER=heart compile_example ;

test_heart_static:	compile_heart
	$(MAKE) -s OUTPUT=bin/test_heart.html testheader ;
	$(MAKE) -s FILE=examples/src/heart/package OUTPUT=bin/test_heart.html compile_package ;
	echo '    <script type="text/javascript" src="../examples/src/heart/heartnative.js"></script>' >> bin/test_heart.html ;
	echo '    <script type="text/javascript">' >> bin/test_heart.html ;
	echo '       window.onload = function() {' >> bin/test_heart.html ;
	echo '         var animator = Util.resolveClass("heart.Animator").newInstance();' >> bin/test_heart.html ;
	echo '         animator["<init>(Ljava/lang/String;)V"](Util.js2java_string("canvas"));' >> bin/test_heart.html ;
	echo '         var frac = Util.resolveClass("heart.CFractal").newInstance();' >> bin/test_heart.html ;
	echo '         frac["<init>(Lheart/Animator;)V"](animator);' >> bin/test_heart.html ;
	echo '         frac["run(I)V"](new Integer(10));' >> bin/test_heart.html ;
	echo '       };' >> bin/test_heart.html ;
	echo '    </script>' >> bin/test_heart.html ;
	echo '  </head>' >> bin/test_heart.html ;
	echo '  <body>' >> bin/test_heart.html ;
	echo '    <div style="width:600px; margin-left:auto; margin-right:auto; position:relative; text-align:center;">' >> bin/test_heart.html ;
	echo '      <canvas id="canvas" width="500" height="500" style="border:1px solid black;"></canvas>' >> bin/test_heart.html ;
	echo '    </div>' >> bin/test_heart.html ;
	echo '  </body>' >> bin/test_heart.html ;
	echo '</html>' >> bin/test_heart.html ;

test_heart_dynamic:	compile_heart
	$(MAKE) -s OUTPUT=bin/test_heart.html testheader ;
	echo '    <script type="text/javascript" src="../examples/src/heart/heartnative.js"></script>' >> bin/test_heart.html ;
	echo '    <script type="text/javascript">' >> bin/test_heart.html ;
	echo '       var stderr = null;' >> bin/test_heart.html ;
	echo '       function logger(str) {' >> bin/test_heart.html ;
	echo '          stderr.value += str + "\n";' >> bin/test_heart.html ;
	echo '       }' >> bin/test_heart.html ;
	echo '       window.onload = function() {' >> bin/test_heart.html ;
	echo '         stderr = document.getElementById("stderr");' >> bin/test_heart.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> bin/test_heart.html ;
	echo '         classloader.setDocument(document);' >> bin/test_heart.html ;
	echo '         classloader.setLogger(logger);' >> bin/test_heart.html ;
	echo '         Util.setClassLoader(classloader);' >> bin/test_heart.html ;
	echo '         var animator = Util.resolveClass("heart.Animator").newInstance();' >> bin/test_heart.html ;
	echo '         animator["<init>(Ljava/lang/String;)V"](Util.js2java_string("canvas"));' >> bin/test_heart.html ;
	echo '         var frac = Util.resolveClass("heart.CFractal").newInstance();' >> bin/test_heart.html ;
	echo '         frac["<init>(Lheart/Animator;)V"](animator);' >> bin/test_heart.html ;
	echo '         frac["run(I)V"](new Integer(10));' >> bin/test_heart.html ;
	echo '       };' >> bin/test_heart.html ;
	echo '    </script>' >> bin/test_heart.html ;
	echo '  </head>' >> bin/test_heart.html ;
	echo '  <body>' >> bin/test_heart.html ;
	echo '    <canvas id="canvas" width="500" height="500" style="border:1px solid black;"></canvas>' >> bin/test_heart.html ;
	echo '    <textarea id="stderr" rows="20" cols="50"></textarea>' >> bin/test_heart.html ;
	echo '  </body>' >> bin/test_heart.html ;
	echo '</html>' >> bin/test_heart.html ;

compile_bf:
	$(MAKE) FOLDER=bf compile_example ;

test_bf:	compile_bf
	$(MAKE) -s OUTPUT=bin/test_bf.html testheader ;
	echo '    <script type="text/javascript" src="../examples/src/bf/bfnative.js"></script>' >> bin/test_bf.html ;
	echo '    <script type="text/javascript">' >> bin/test_bf.html ;
	echo '       var stderr = null;' >> bin/test_bf.html ;
	echo '       function logger(str) {' >> bin/test_bf.html ;
	echo '          stderr.value += str + "\n";' >> bin/test_bf.html ;
	echo '       }' >> bin/test_bf.html ;
	echo '       window.onload = function() {' >> bin/test_bf.html ;
	echo '         stderr = document.getElementById("stderr");' >> bin/test_bf.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> bin/test_bf.html ;
	echo '         classloader.setDocument(document);' >> bin/test_bf.html ;
	echo '         classloader.setLogger(logger);' >> bin/test_bf.html ;
	echo '         Util.setClassLoader(classloader);' >> bin/test_bf.html ;
	echo '         document.getElementById("button").onclick = function() {' >> bin/test_bf.html ;
	echo '           var program = document.getElementById("program").value;' >> bin/test_bf.html ;
	echo '           program = Util.resolveClass("bf.Parser")["parse(Ljava/lang/String;)Lbf/Command;"](Util.js2java_string(program));' >> bin/test_bf.html ;
	echo '           var jsio = Util.resolveClass("bf.JSIO").newInstance();' >> bin/test_bf.html ;
	echo '           var input = document.getElementById("input").value;' >> bin/test_bf.html ;
	echo '           jsio["<init>(Ljava/lang/String;Ljava/lang/String;)V"](Util.js2java_string(input), Util.js2java_string("output"));' >> bin/test_bf.html ;
	echo '           var state = Util.resolveClass("bf.State").newInstance();' >> bin/test_bf.html ;
	echo '           state["<init>(Lbf/IO;I)V"](jsio, new Integer(1000));' >> bin/test_bf.html ;
	echo '           var interpreter = Util.resolveClass("bf.Interpreter").newInstance();' >> bin/test_bf.html ;
	echo '           interpreter["<init>(Lbf/Command;)V"](program);' >> bin/test_bf.html ;
	echo '           interpreter["interpret(Lbf/State;)V"](state);' >> bin/test_bf.html ;
	echo '         };' >> bin/test_bf.html ;
	echo '       };' >> bin/test_bf.html ;
	echo '    </script>' >> bin/test_bf.html ;
	echo '  </head>' >> bin/test_bf.html ;
	echo '  <body>' >> bin/test_bf.html ;
	echo '    <div><textarea id="program" rows="10" cols="50"></textarea></div>' >> bin/test_bf.html ;
	echo '    <div><textarea id="input" rows="5" cols="50"></textarea></div>' >> bin/test_bf.html ;
	echo '    <div><textarea id="output" rows="5" cols="50"></textarea></div>' >> bin/test_bf.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> bin/test_bf.html ;
	echo '    <div><textarea id="stderr" rows="20" cols="50"></textarea></div>' >> bin/test_bf.html ;
	echo '  </body>' >> bin/test_bf.html ;
	echo '</html>' >> bin/test_bf.html ;

compile_lambda:
	$(MAKE) FOLDER=lambda compile_example ;

test_lambda_static:	compile_lambda
	$(MAKE) -s OUTPUT=bin/test_lambda.html testheader ;
	$(MAKE) -s OUTPUT=bin/test_lambda.html FILE=examples/src/lambda/package compile_package ;
	echo '    <script type="text/javascript" src="../examples/src/lambda/lambdanative.js"></script>' >> bin/test_lambda.html ;
	echo '    <script type="text/javascript">' >> bin/test_lambda.html ;
	echo '      window.onload = function() {' >> bin/test_lambda.html ;
	echo '        var button = document.getElementById("button");' >> bin/test_lambda.html ;
	echo '        var input = document.getElementById("input");' >> bin/test_lambda.html ;
	echo '        var output = document.getElementById("output");' >> bin/test_lambda.html ;
	echo '        var printer = Util.resolveClass("lambda.JSPrinter").newInstance();' >> bin/test_lambda.html ;
	echo '        printer["<init>()V"]();' >> bin/test_lambda.html ;
	echo '        printer.textarea = output;' >> bin/test_lambda.html ;
	echo '        button.onclick = function() {' >> bin/test_lambda.html ;
	echo '          var inputtxt = Util.js2java_string(input.value);' >> bin/test_lambda.html ;
	echo '          var expr = Util.resolveClass("lambda.Parser")["parse(Ljava/lang/String;)Llambda/Expression;"](inputtxt);' >> bin/test_lambda.html ;
	echo '          while (expr["eval()Z"]().isTrue()) {}' >> bin/test_lambda.html ;
	echo '          expr["dump(Llambda/Printer;)V"](printer);' >> bin/test_lambda.html ;
	echo '        };' >> bin/test_lambda.html ;
	echo '      };' >> bin/test_lambda.html ;
	echo '    </script>' >> bin/test_lambda.html ;
	echo '  </head>' >> bin/test_lambda.html ;
	echo '  <body>' >> bin/test_lambda.html ;
	echo '    <div><textarea id="input" rows="5" cols="50"></textarea></div>' >> bin/test_lambda.html ;
	echo '    <div><textarea id="output" rows="5" cols="50"></textarea></div>' >> bin/test_lambda.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> bin/test_lambda.html ;
	echo '  </body>' >> bin/test_lambda.html ;
	echo '</html>' >> bin/test_lambda.html ;

test_lambda_dynamic:	compile_lambda
	$(MAKE) -s OUTPUT=bin/test_lambda.html testheader ;
	echo '    <script type="text/javascript" src="../examples/src/lambda/lambdanative.js"></script>' >> bin/test_lambda.html ;
	echo '    <script type="text/javascript">' >> bin/test_lambda.html ;
	echo '      var stderr = null;' >> bin/test_lambda.html ;
	echo '      function logger(str) {' >> bin/test_lambda.html ;
	echo '        stderr.value += str + "\n";' >> bin/test_lambda.html ;
	echo '      }' >> bin/test_lambda.html ;
	echo '      window.onload = function() {' >> bin/test_lambda.html ;
	echo '        stderr = document.getElementById("stderr");' >> bin/test_lambda.html ;
	echo '        var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> bin/test_lambda.html ;
	echo '        classloader.setDocument(document);' >> bin/test_lambda.html ;
	echo '        classloader.setLogger(logger);' >> bin/test_lambda.html ;
	echo '        Util.setClassLoader(classloader);' >> bin/test_lambda.html ;
	echo '        var button = document.getElementById("button");' >> bin/test_lambda.html ;
	echo '        var input = document.getElementById("input");' >> bin/test_lambda.html ;
	echo '        var output = document.getElementById("output");' >> bin/test_lambda.html ;
	echo '        var printer = Util.resolveClass("lambda.JSPrinter").newInstance();' >> bin/test_lambda.html ;
	echo '        printer["<init>()V"]();' >> bin/test_lambda.html ;
	echo '        printer.textarea = output;' >> bin/test_lambda.html ;
	echo '        button.onclick = function() {' >> bin/test_lambda.html ;
	echo '          var inputtxt = Util.js2java_string(input.value);' >> bin/test_lambda.html ;
	echo '          try {' >> bin/test_lambda.html ;
	echo '            var expr = Util.resolveClass("lambda.Parser")["parse(Ljava/lang/String;)Llambda/Expression;"](inputtxt);' >> bin/test_lambda.html ;
	echo '            while (expr["eval()Z"]().isTrue()) {}' >> bin/test_lambda.html ;
	echo '            expr["dump(Llambda/Printer;)V"](printer);' >> bin/test_lambda.html ;
	echo '          } catch (e) {' >> bin/test_lambda.html ;
	echo '            if ("toString()Ljava/lang/String;" in e)' >> bin/test_lambda.html ;
	echo '              alert(Util.java2js_string(e["toString()Ljava/lang/String;"]()));' >> bin/test_lambda.html ;
	echo '            else' >> bin/test_lambda.html ;
	echo '              alert(e);' >> bin/test_lambda.html ;
	echo '          }' >> bin/test_lambda.html ;
	echo '        };' >> bin/test_lambda.html ;
	echo '      };' >> bin/test_lambda.html ;
	echo '    </script>' >> bin/test_lambda.html ;
	echo '  </head>' >> bin/test_lambda.html ;
	echo '  <body>' >> bin/test_lambda.html ;
	echo '    <div><textarea id="input" rows="5" cols="50"></textarea></div>' >> bin/test_lambda.html ;
	echo '    <div><textarea id="output" rows="5" cols="50"></textarea></div>' >> bin/test_lambda.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> bin/test_lambda.html ;
	echo '    <div><textarea id="stderr" rows="5" cols="50"></textarea></div>' >> bin/test_lambda.html ;
	echo '  </body>' >> bin/test_lambda.html ;
	echo '</html>' >> bin/test_lambda.html ;

clean:
	rm -rf bin/* src/*~ src/java2js/*~ ;
