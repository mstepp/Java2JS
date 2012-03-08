SHELL := /bin/bash
JAVA_SRC := $(wildcard src/java2js/*.java src/java2js/test/*.java src/heart/*.java src/bf/*.java src/tetris/*.java src/lambda/*.java)
JAVA_CLASS := $(patsubst src/%.java,bin/%.class,$(JAVA_SRC))
JS_LIBRARY := int.js arrayobject.js util.js long.js
SPECIAL_CLASSES := 'java/lang/String$$CaseInsensitiveComparator'

.PHONY:	java all test1 compile_heart test_heart

all:	java

java:	$(JAVA_CLASS)

bin/%.class:	src/%.java
	javac -Xlint -cp bin:bcel-5.2.jar -d bin/ -sourcepath src/ $< ;

js/%.js:	$(JAVA_CLASS)
	CLASSNAME='$@' ; \
	CLASSNAME="$${CLASSNAME%.js}" ; \
	CLASSNAME="$${CLASSNAME#js/}" ; \
	CLASSNAME="$${CLASSNAME//\$$/\$$\$$}" ; \
	mkdir -m 0777 -p `dirname '$@'` ; \
	$(MAKE) -s SHELL=$(SHELL) "CLASS=$$CLASSNAME.class" compile > '$@' ; \
	chmod a+r '$@' ;

# needs LISTFILE
compilefile:
	for classname in `cat $(LISTFILE)` ; do \
		$(MAKE) "js/$$classname.js" ; \
	done ;

compileall:
	for i in packages/*.package ; do \
		$(MAKE) -s 'LISTFILE=$$i' compilefile ; \
	done ;

# needs CLASS
compile:	java
	java -cp bcel-5.2.jar:bin/ java2js.CompileByName '$(CLASS)' ;

compilebasic:	java
	$(MAKE) -s LISTFILE=packages/basic compilefile ;
	chmod -R a+r js/ ;
	find js -type d -exec chmod a+rx {} \;

# needs LISTFILE and OUTPUT
testheader:
	echo '<html xmlns="http://www.w3.org/1999/xhtml">' > "$(OUTPUT)" ;
	echo '  <head>' >> "$(OUTPUT)" ;
	echo '    <title>Testing</title>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="src/util.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="src/native.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="src/reflect_native.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="src/int.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="src/long.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="src/object.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="src/arrayobject.js"></script>' >> "$(OUTPUT)" ;
	for classname in `cat $(LISTFILE)` ; do \
		echo "    <script type=\"text/javascript\" src=\"js/$$classname.js\"></script>" ; \
	done >> "$(OUTPUT)" ;

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
	$(MAKE) -s LISTFILE=packages/arithmetic compilefile ;

test_arithmetic:
	$(MAKE) -s OUTPUT=test.html LISTFILE=packages/arithmetic testheader ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> test.html ;
	echo '         Util.resolveClass("java2js.test.TestJS").method_main_V_ALjava$$Dlang$$DStringE_(args);' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	$(MAKE) -s OUTPUT=test.html testfooter ;



compile_factorial:
	$(MAKE) -s LISTFILE=packages/factorial compilefile ;
	chmod -R a+r js/ ;
	find js/ -type d -exec chmod a+rx {} \;

test_factorial:
	$(MAKE) -s OUTPUT=test.html LISTFILE=packages/factorial testheader ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> test.html ;
	echo '         Util.resolveClass("java2js.test.Factorial").method_main_V_ALjava$$Dlang$$DStringE_(args);' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	$(MAKE) -s OUTPUT=test.html testfooter ;


compile_heart:
	$(MAKE) -s LISTFILE=packages/heart compilefile ;
	chmod -R a+r js/ ;
	find js/ -type d -exec chmod a+rx {} \;

test_heart:
	$(MAKE) -s OUTPUT=test.html LISTFILE=packages/heart testheader ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         try {' >> test.html ;
	echo '           var animator = Util.resolveClass("heart.Animator").newInstance();' >> test.html ;
	echo '           animator.init_Ljava$$Dlang$$DStringE_(Util.js2java_string("canvas"));' >> test.html ;
	echo '           var frac = Util.resolveClass("heart.CFractal").newInstance();' >> test.html ;
	echo '           frac.init_Lheart$$D$$AnimatorE_(animator);' >> test.html ;
	echo '           frac.method_run_V_I_(new Integer(15));' >> test.html ;
	echo '         } catch(exception) {' >> test.html ;
	echo '           if (!((typeof exception) == "object" && ("thisclass" in exception))) {' >> test.html ;
	echo '             alert("JavaScript exception: " + exception);' >> test.html ;
	echo '           } else {' >> test.html ;
	echo '             alert(exception + " " + Util.java2js_string(exception.method_toString_Ljava$$Dlang$$DStringE__()));' >> test.html ;
	echo '           }' >> test.html ;
	echo '         }' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	echo '  </head>' >> test.html ;
	echo '  <body>' >> test.html ;
	echo '    <canvas id="canvas" width="500" height="500" style="border:1px solid black;"></canvas>' >> test.html ;
	echo '  </body>' >> test.html ;
	echo '</html>' >> test.html ;

testbf1:	java
	java -cp bin:bcel-5.2.jar bf.Runner '++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.' '' ;




compile_bf:
	$(MAKE) -s LISTFILE=packages/bf compilefile ;
	chmod -R a+r js/ ;
	find js/ -type d -exec chmod a+rx {} \;

test_bf:
	$(MAKE) -s OUTPUT=test.html LISTFILE=packages/bf testheader ;
	echo '    <script type="text/javascript" src="src/bfnative.js"></script>' >> test.html ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         document.getElementById("button").onclick = function() {' >> test.html ;
	echo '           var program = document.getElementById("program").value;' >> test.html ;
	echo '           program = Util.resolveClass("bf.Parser").method_parse_Lbf$$DCommandE_Ljava$$Dlang$$DStringE_(Util.js2java_string(program));' >> test.html ;
	echo '           var jsio = Util.resolveClass("bf.JSIO").newInstance();' >> test.html ;
	echo '           var input = document.getElementById("input").value;' >> test.html ;
	echo '           jsio.init_Ljava$$Dlang$$DStringELjava$$Dlang$$DStringE_(Util.js2java_string(input), Util.js2java_string("output"));' >> test.html ;
	echo '           var state = Util.resolveClass("bf.State").newInstance();' >> test.html ;
	echo '           state.init_Lbf$$DIOEI_(jsio, new Integer(1000));' >> test.html ;
	echo '           var interpreter = Util.resolveClass("bf.Interpreter").newInstance();' >> test.html ;
	echo '           interpreter.init_Lbf$$DCommandE_(program);' >> test.html ;
	echo '           interpreter.method_interpret_V_Lbf$$DStateE_(state);' >> test.html ;
	echo '         };' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	echo '  </head>' >> test.html ;
	echo '  <body>' >> test.html ;
	echo '    <div><textarea id="program" rows="10" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><textarea id="input" rows="5" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><textarea id="output" rows="5" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> test.html ;
	echo '  </body>' >> test.html ;
	echo '</html>' >> test.html ;


compile_lambda:
	$(MAKE) -s LISTFILE=packages/lambda compilefile ;
	chmod -R a+r js/ ;
	find js/ -type d -exec chmod a+rx {} \;

test_lambda:
	$(MAKE) -s OUTPUT=test.html LISTFILE=packages/lambda testheader ;
	echo '    <script type="text/javascript" src="src/lambdanative.js"></script>' >> test.html ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         var output = document.getElementById("output");' >> test.html ;
	echo '         document.getElementById("button").onclick = function() {' >> test.html ;
	echo '           var jsprinter = Util.resolveClass("lambda.JSPrinter").newInstance();' >> test.html ;
	echo '           jsprinter.init__();' >> test.html ;
	echo '           jsprinter.textarea = output;' >> test.html ;
	echo '           output.value += Util.java2js_string(Util.resolveClass("lambda.Functions").field_ISZ$$ERO_Llambda$$D$$ExpressionE.method_dump_V_Llambda$$DPrinterE_(jsprinter);' >> test.html ;
#	echo '           Util.resolveClass("lambda.Main").method_run_V_Llambda$$DPrinterE_(jsprinter);' >> test.html ;
	echo '         };' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	echo '  </head>' >> test.html ;
	echo '  <body>' >> test.html ;
	echo '    <div><textarea id="output" rows="10" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> test.html ;
	echo '  </body>' >> test.html ;
	echo '</html>' >> test.html ;

clean:
	rm -rf bin/* src/*~ src/java2js/*~ src/java2js/test/*~ *~ js/* test.html ;
