SHELL := /bin/bash
JAVA_SRC := $(wildcard src/java2js/*.java)
JAVA_CLASS := $(patsubst src/%.java,bin/%.class,$(JAVA_SRC))
HEART_SRC := $(wildcard examples/src/heart/*.java)
HEART_CLASS := $(patsubst examples/src/%.java,examples/bin/%.class,$(HEART_SRC))
FACTORIAL_SRC := $(wildcard examples/src/factorial/*.java)
FACTORIAL_CLASS := $(patsubst examples/src/%.java,examples/bin/%.class,$(FACTORIAL_SRC))
ARITHMETIC_SRC := $(wildcard examples/src/arithmetic/*.java)
ARITHMETIC_CLASS := $(patsubst examples/src/%.java,examples/bin/%.class,$(ARITHMETIC_SRC))
BF_SRC := $(wildcard examples/src/bf/*.java)
BF_CLASS := $(patsubst examples/src/%.java,examples/bin/%.class,$(BF_SRC))
ALL_CLASS = $(HEART_CLASS) $(FACTORIAL_CLASS) $(ARITHMETIC_CLASS) $(BF_CLASS)

.SECONDARY:

.PHONY:	java all

all:	java

java:	$(JAVA_CLASS)

bin/%.class:	src/%.java
	javac -Xlint -cp examples/bin/:bin:bcel-5.2.jar -d bin/ -sourcepath src/ $< ;

examples/bin/%.class:	examples/src/%.java
	javac -Xlint -cp examples/bin:bin:bcel-5.2.jar -d examples/bin/ -sourcepath examples/src/ $< ;

examples/js/%.js:	$(ALL_CLASS)
	CLASSNAME='$@' ; \
	CLASSNAME="$${CLASSNAME%.js}" ; \
	CLASSNAME="$${CLASSNAME#examples/js/}" ; \
	CLASSNAME="$${CLASSNAME//\$$/\$$\$$}" ; \
	mkdir -p `dirname '$@'` ; \
	$(MAKE) -s SHELL=$(SHELL) "CLASS=$$CLASSNAME.class" compile > '$@' ; \
	chmod a+wr '$@' ; \
	find examples/js/ -type d -exec chmod a+rwx {} \; ;

# needs CLASS=java/lang/String.class
compile:	java
	java -cp bcel-5.2.jar:bin/:examples/bin/ java2js.CompileByName '$(CLASS)' ;

# needs LISTFILE and OUTPUT
testheader:
	echo '<html xmlns="http://www.w3.org/1999/xhtml">' > "$(OUTPUT)" ;
	echo '  <head>' >> "$(OUTPUT)" ;
	echo '    <title>Testing</title>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/util.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/native.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/reflect_native.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/int.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/long.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/object.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/arrayobject.js"></script>' >> "$(OUTPUT)" ;
	echo '    <script type="text/javascript" src="../../../src/classloader.js"></script>' >> "$(OUTPUT)" ;
	for classname in `cat $(LISTFILE)` ; do \
		echo "    <script type=\"text/javascript\" src=\"../../../examples/js/$$classname.js\"></script>" ; \
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

test_arithmetic:	$(ARITHMETIC_CLASS)
	$(MAKE) -s OUTPUT=examples/bin/arithmetic/test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript">' >> examples/bin/arithmetic/test.html ;
	echo '       var stderr = null;' >> examples/bin/arithmetic/test.html ;
	echo '       function logger(str) {' >> examples/bin/arithmetic/test.html ;
	echo '          stderr.value += str + "\n";' >> examples/bin/arithmetic/test.html ;
	echo '       }' >> examples/bin/arithmetic/test.html ;
	echo '       window.onload = function() {' >> examples/bin/arithmetic/test.html ;
	echo '         stderr = document.getElementById("stderr");' >> examples/bin/arithmetic/test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> examples/bin/arithmetic/test.html ;
	echo '         classloader.setDocument(document);' >> examples/bin/arithmetic/test.html ;
	echo '         classloader.setLogger(logger);' >> examples/bin/arithmetic/test.html ;
	echo '         Util.setClassLoader(classloader);' >> examples/bin/arithmetic/test.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> examples/bin/arithmetic/test.html ;
	echo '         Util.resolveClass("arithmetic.Arithmetic")["main([Ljava/lang/String;)V"](args);' >> examples/bin/arithmetic/test.html ;
	echo '       };' >> examples/bin/arithmetic/test.html ;
	echo '    </script>' >> examples/bin/arithmetic/test.html ;
	$(MAKE) -s OUTPUT=examples/bin/arithmetic/test.html testfooter ;

test_factorial:	$(FACTORIAL_CLASS)
	$(MAKE) -s OUTPUT=examples/bin/factorial/test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript">' >> examples/bin/factorial/test.html ;
	echo '       var stderr = null;' >> examples/bin/factorial/test.html ;
	echo '       function logger(str) {' >> examples/bin/factorial/test.html ;
	echo '          stderr.value += str + "\n";' >> examples/bin/factorial/test.html ;
	echo '       }' >> examples/bin/factorial/test.html ;
	echo '       window.onload = function() {' >> examples/bin/factorial/test.html ;
	echo '         stderr = document.getElementById("stderr");' >> examples/bin/factorial/test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> examples/bin/factorial/test.html ;
	echo '         classloader.setDocument(document);' >> examples/bin/factorial/test.html ;
	echo '         classloader.setLogger(logger);' >> examples/bin/factorial/test.html ;
	echo '         Util.setClassLoader(classloader);' >> examples/bin/factorial/test.html ;
	echo '         var args = new ArrayObject(0, new ArrayType(Util.resolveClass("java.lang.String")));' >> examples/bin/factorial/test.html ;
	echo '         Util.resolveClass("java2js.test.Factorial")["main([Ljava/lang/String;)V"](args);' >> examples/bin/factorial/test.html ;
	echo '       };' >> examples/bin/factorial/test.html ;
	echo '    </script>' >> examples/bin/factorial/test.html ;
	$(MAKE) -s OUTPUT=examples/bin/factorial/test.html testfooter ;


test_heart:	$(HEART_CLASS)
	$(MAKE) -s OUTPUT=examples/bin/heart/test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript" src="src/heartnative.js"></script>' >> examples/bin/heart/test.html ;
	echo '    <script type="text/javascript">' >> examples/bin/heart/test.html ;
	echo '       var stderr = null;' >> examples/bin/heart/test.html ;
	echo '       function logger(str) {' >> examples/bin/heart/test.html ;
	echo '          stderr.value += str + "\n";' >> examples/bin/heart/test.html ;
	echo '       }' >> examples/bin/heart/test.html ;
	echo '       window.onload = function() {' >> examples/bin/heart/test.html ;
	echo '         stderr = document.getElementById("stderr");' >> examples/bin/heart/test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> examples/bin/heart/test.html ;
	echo '         classloader.setDocument(document);' >> examples/bin/heart/test.html ;
	echo '         classloader.setLogger(logger);' >> examples/bin/heart/test.html ;
	echo '         Util.setClassLoader(classloader);' >> examples/bin/heart/test.html ;
	echo '         var animator = Util.resolveClass("heart.Animator").newInstance();' >> examples/bin/heart/test.html ;
	echo '         animator["<init>(Ljava/lang/String;)V"](Util.js2java_string("canvas"));' >> examples/bin/heart/test.html ;
	echo '         var frac = Util.resolveClass("heart.CFractal").newInstance();' >> examples/bin/heart/test.html ;
	echo '         frac["<init>(Lheart/Animator;)V"](animator);' >> examples/bin/heart/test.html ;
	echo '         frac["run(I)V"](new Integer(15));' >> examples/bin/heart/test.html ;
	echo '       };' >> examples/bin/heart/test.html ;
	echo '    </script>' >> examples/bin/heart/test.html ;
	echo '  </head>' >> examples/bin/heart/test.html ;
	echo '  <body>' >> examples/bin/heart/test.html ;
	echo '    <canvas id="canvas" width="500" height="500" style="border:1px solid black;"></canvas>' >> examples/bin/heart/test.html ;
	echo '    <textarea id="stderr" rows="20" cols="50"></textarea>' >> examples/bin/heart/test.html ;
	echo '  </body>' >> examples/bin/heart/test.html ;
	echo '</html>' >> examples/bin/heart/test.html ;


test_bf:	$(BF_CLASS)
	$(MAKE) -s OUTPUT=examples/bin/bf/test.html LISTFILE=/dev/null testheader ;
	echo '    <script type="text/javascript" src="src/bfnative.js"></script>' >> examples/bin/bf/test.html ;
	echo '    <script type="text/javascript">' >> examples/bin/bf/test.html ;
	echo '       var stderr = null;' >> examples/bin/bf/test.html ;
	echo '       function logger(str) {' >> examples/bin/bf/test.html ;
	echo '          stderr.value += str + "\n";' >> examples/bin/bf/test.html ;
	echo '       }' >> examples/bin/bf/test.html ;
	echo '       window.onload = function() {' >> examples/bin/bf/test.html ;
	echo '         stderr = document.getElementById("stderr");' >> examples/bin/bf/test.html ;
	echo '         var classloader = new DefaultURLClassLoader("http://goto.ucsd.edu/~mstepp/java2js_git/classloader.php");' >> examples/bin/bf/test.html ;
	echo '         classloader.setDocument(document);' >> examples/bin/bf/test.html ;
	echo '         classloader.setLogger(logger);' >> examples/bin/bf/test.html ;
	echo '         Util.setClassLoader(classloader);' >> examples/bin/bf/test.html ;
	echo '         document.getElementById("button").onclick = function() {' >> examples/bin/bf/test.html ;
	echo '           var program = document.getElementById("program").value;' >> examples/bin/bf/test.html ;
	echo '           program = Util.resolveClass("bf.Parser")["parse(Ljava/lang/String;)Lbf/Command;"](Util.js2java_string(program));' >> examples/bin/bf/test.html ;
	echo '           var jsio = Util.resolveClass("bf.JSIO").newInstance();' >> examples/bin/bf/test.html ;
	echo '           var input = document.getElementById("input").value;' >> examples/bin/bf/test.html ;
	echo '           jsio["<init>(Ljava/lang/String;Ljava/lang/String;)V"](Util.js2java_string(input), Util.js2java_string("output"));' >> examples/bin/bf/test.html ;
	echo '           var state = Util.resolveClass("bf.State").newInstance();' >> examples/bin/bf/test.html ;
	echo '           state["<init>(Lbf/IO;I)V"](jsio, new Integer(1000));' >> examples/bin/bf/test.html ;
	echo '           var interpreter = Util.resolveClass("bf.Interpreter").newInstance();' >> examples/bin/bf/test.html ;
	echo '           interpreter["<init>(Lbf/Command;)V"](program);' >> examples/bin/bf/test.html ;
	echo '           interpreter["interpret(Lbf/State;)V"](state);' >> examples/bin/bf/test.html ;
	echo '         };' >> examples/bin/bf/test.html ;
	echo '       };' >> examples/bin/bf/test.html ;
	echo '    </script>' >> examples/bin/bf/test.html ;
	echo '  </head>' >> examples/bin/bf/test.html ;
	echo '  <body>' >> examples/bin/bf/test.html ;
	echo '    <div><textarea id="program" rows="10" cols="50"></textarea></div>' >> examples/bin/bf/test.html ;
	echo '    <div><textarea id="input" rows="5" cols="50"></textarea></div>' >> examples/bin/bf/test.html ;
	echo '    <div><textarea id="output" rows="5" cols="50"></textarea></div>' >> examples/bin/bf/test.html ;
	echo '    <div><input type="button" value="Go" id="button"/></div>' >> examples/bin/bf/test.html ;
	echo '    <div><textarea id="stderr" rows="20" cols="50"></textarea></div>' >> examples/bin/bf/test.html ;
	echo '  </body>' >> examples/bin/bf/test.html ;
	echo '</html>' >> examples/bin/bf/test.html ;

test_tetris3d:
	$(MAKE) -s OUTPUT=test.html LISTFILE=/dev/null testheader ;
	cat src/tetris3d/tetris.html >> test.html ;

clean:
	rm -rf bin/* src/*~ src/java2js/*~ examples/bin/* examples/js/* ;
