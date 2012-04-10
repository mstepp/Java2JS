SHELL := /bin/bash
JAVA_SRC := $(wildcard src/java2js/*.java src/java2js/test/*.java src/heart/*.java src/bf/*.java src/lambda/*.java src/tetris3d/*.java)
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
	mkdir -p `dirname '$@'` ; \
	$(MAKE) -s SHELL=$(SHELL) "CLASS=$$CLASSNAME.class" compile > '$@' ; \
	chmod a+r '$@' ; \
	find js/ -type d -exec chmod a+rwx {} \; ;

# needs LISTFILE
compilefile:
	for classname in `cat $(LISTFILE)` ; do \
		$(MAKE) "js/$$classname.js" ; \
	done ;

# needs CLASS
compile:	java
	java -cp bcel-5.2.jar:bin/ java2js.CompileByName '$(CLASS)' ;

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
	echo '    <script type="text/javascript" src="src/classloader.js"></script>' >> "$(OUTPUT)" ;
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


test_arithmetic:
	$(MAKE) -s OUTPUT=test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       var stderr = null;' >> test.html ;
	echo '       function logger(str) {' >> test.html ;
	echo '          stderr.value += str + "\n";' >> test.html ;
	echo '       }' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         stderr = document.getElementById("stderr");' >> test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> test.html ;
	echo '         classloader.setDocument(document);' >> test.html ;
	echo '         classloader.setLogger(logger);' >> test.html ;
	echo '         Util.setClassLoader(classloader);' >> test.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> test.html ;
	echo '         Util.resolveClass("java2js.test.TestJS")["main([Ljava/lang/String;)V"](args);' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	$(MAKE) -s OUTPUT=test.html testfooter ;


test_factorial:
	$(MAKE) -s OUTPUT=test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       var stderr = null;' >> test.html ;
	echo '       function logger(str) {' >> test.html ;
	echo '          stderr.value += str + "\n";' >> test.html ;
	echo '       }' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         stderr = document.getElementById("stderr");' >> test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> test.html ;
	echo '         classloader.setDocument(document);' >> test.html ;
	echo '         classloader.setLogger(logger);' >> test.html ;
	echo '         Util.setClassLoader(classloader);' >> test.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> test.html ;
	echo '         Util.resolveClass("java2js.test.Factorial")["main([Ljava/lang/String;)V"](args);' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	$(MAKE) -s OUTPUT=test.html testfooter ;


test_heart:
	$(MAKE) -s OUTPUT=test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript" src="src/heartnative.js"></script>' >> test.html ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       var stderr = null;' >> test.html ;
	echo '       function logger(str) {' >> test.html ;
	echo '          stderr.value += str + "\n";' >> test.html ;
	echo '       }' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         stderr = document.getElementById("stderr");' >> test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> test.html ;
	echo '         classloader.setDocument(document);' >> test.html ;
	echo '         classloader.setLogger(logger);' >> test.html ;
	echo '         Util.setClassLoader(classloader);' >> test.html ;
	echo '         var animator = Util.resolveClass("heart.Animator").newInstance();' >> test.html ;
	echo '         animator["<init>(Ljava/lang/String;)V"](Util.js2java_string("canvas"));' >> test.html ;
	echo '         var frac = Util.resolveClass("heart.CFractal").newInstance();' >> test.html ;
	echo '         frac["<init>(Lheart/Animator;)V"](animator);' >> test.html ;
	echo '         frac["run(I)V"](new Integer(15));' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	echo '  </head>' >> test.html ;
	echo '  <body>' >> test.html ;
	echo '    <canvas id="canvas" width="500" height="500" style="border:1px solid black;"></canvas>' >> test.html ;
	echo '    <textarea id="stderr" rows="20" cols="50"></textarea>' >> test.html ;
	echo '  </body>' >> test.html ;
	echo '</html>' >> test.html ;

testbf1:	java
	java -cp bin:bcel-5.2.jar bf.Runner '++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.' '' ;


test_bf:
	$(MAKE) -s OUTPUT=test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript" src="src/bfnative.js"></script>' >> test.html ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       var stderr = null;' >> test.html ;
	echo '       function logger(str) {' >> test.html ;
	echo '          stderr.value += str + "\n";' >> test.html ;
	echo '       }' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         stderr = document.getElementById("stderr");' >> test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> test.html ;
	echo '         classloader.setDocument(document);' >> test.html ;
	echo '         classloader.setLogger(logger);' >> test.html ;
	echo '         Util.setClassLoader(classloader);' >> test.html ;
	echo '         document.getElementById("button").onclick = function() {' >> test.html ;
	echo '           var program = document.getElementById("program").value;' >> test.html ;
	echo '           program = Util.resolveClass("bf.Parser")["parse(Ljava/lang/String;)Lbf/Command;"](Util.js2java_string(program));' >> test.html ;
	echo '           var jsio = Util.resolveClass("bf.JSIO").newInstance();' >> test.html ;
	echo '           var input = document.getElementById("input").value;' >> test.html ;
	echo '           jsio["<init>(Ljava/lang/String;Ljava/lang/String;)V"](Util.js2java_string(input), Util.js2java_string("output"));' >> test.html ;
	echo '           var state = Util.resolveClass("bf.State").newInstance();' >> test.html ;
	echo '           state["<init>(Lbf/IO;I)V"](jsio, new Integer(1000));' >> test.html ;
	echo '           var interpreter = Util.resolveClass("bf.Interpreter").newInstance();' >> test.html ;
	echo '           interpreter["<init>(Lbf/Command;)V"](program);' >> test.html ;
	echo '           interpreter["interpret(Lbf/State;)V"](state);' >> test.html ;
	echo '         };' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	echo '  </head>' >> test.html ;
	echo '  <body>' >> test.html ;
	echo '    <div><textarea id="program" rows="10" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><textarea id="input" rows="5" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><textarea id="output" rows="5" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> test.html ;
	echo '    <div><textarea id="stderr" rows="20" cols="50"></textarea></div>' >> test.html ;
	echo '  </body>' >> test.html ;
	echo '</html>' >> test.html ;


# TODO: add classloader
test_lambda:
	$(MAKE) -s OUTPUT=test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript" src="src/lambdanative.js"></script>' >> test.html ;
	echo '    <script type="text/javascript">' >> test.html ;
	echo '       window.onload = function() {' >> test.html ;
	echo '         var output = document.getElementById("output");' >> test.html ;
	echo '         document.getElementById("button").onclick = function() {' >> test.html ;
	echo '           var jsprinter = Util.resolveClass("lambda.JSPrinter").newInstance();' >> test.html ;
	echo '           jsprinter["<init>()V"]();' >> test.html ;
	echo '           jsprinter.textarea = output;' >> test.html ;
	echo '           output.value += Util.java2js_string(Util.resolveClass("lambda.Functions")["ISZERO.Llambda/Expression;"]["dump(Llambda/Printer;)V"](jsprinter);' >> test.html ;
#	echo '           Util.resolveClass("lambda.Main")["run(Llambda/Printer;)V"](jsprinter);' >> test.html ;
	echo '         };' >> test.html ;
	echo '       };' >> test.html ;
	echo '    </script>' >> test.html ;
	echo '  </head>' >> test.html ;
	echo '  <body>' >> test.html ;
	echo '    <div><textarea id="output" rows="10" cols="50"></textarea></div>' >> test.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> test.html ;
	echo '  </body>' >> test.html ;
	echo '</html>' >> test.html ;

test_tetris3d:
	$(MAKE) -s OUTPUT=test.html LISTFILE=/dev/null testheader ;
	cat src/tetris3d/tetris.html >> test.html ;

clean:
	rm -rf bin/* src/*~ src/java2js/*~ src/java2js/test/*~ *~ js/* test.html ;
