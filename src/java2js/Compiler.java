package java2js;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import java.io.*;
import java.util.*;

/**
   Does not support:
   - monitors/synchronized
   - multi-threading (duh)
   - reflection
*/
public class Compiler {
    private final ClassGen classgen;
    private final String[] pieces;
    private final String unqualified;
    private final NameMunger munger;
    private final Logger logger;

    public Compiler(ClassGen _classgen, NameMunger _munger, Logger _logger) {
	this.classgen = _classgen;
	String classname = this.classgen.getClassName();
	this.pieces = classname.split("[.]");
	this.unqualified = pieces[pieces.length-1];
	this.munger = _munger;
	this.logger = _logger;
    }

    private String initValue(String type) {
	switch (type.charAt(0)) {
	case 'I':
	case 'C':
	case 'S':
	case 'B':
	case 'Z':
	    return "Integer.ZERO";
	case 'J':
	    return "Long.ZERO";
	case 'F':
	case 'D':
	    return "0";
	default:
	    return "null";
	}
    }

    private String getArgStr(Method method) {
	StringBuilder argstr = new StringBuilder();
	Type[] argtypes = method.getArgumentTypes();
	for (int i = 0; i < argtypes.length; i++) {
	    if (i>0) argstr.append(", ");
	    argstr.append("arg" + i);
	}
	return argstr.toString();
    }

    private void methodStart(Printer inside, Method method) {
	inside.println("var stack = [];");

	StringBuilder localstr = new StringBuilder();
	int count = 0;
	if (!method.isStatic()) {
	    localstr.append("this");
	    count++;
	}
	Type[] argtypes = method.getArgumentTypes();
	for (int i = 0; i < argtypes.length; i++) {
	    if (count>0) localstr.append(", ");
	    localstr.append("arg" + i);
	    count++;
	    if (argtypes[i].equals(Type.DOUBLE) || argtypes[i].equals(Type.LONG)) {
		localstr.append(", arg" + i);
		count++;
	    }
	}
	while (count < method.getCode().getMaxLocals()) {
	    if (count>0) localstr.append(", ");
	    localstr.append("null");
	    count++;
	}
	inside.println("var locals = [%s];", localstr); 

	inside.println("var return_value = null;");
	inside.println("var blocks = [];");
	inside.println("var blockIndex;");
    }

    private void compileCFG(Printer out, final CFG cfg) throws CompilationException {
	final List<CFG.Block> blocks = cfg.getBlocks();
	Function<InstructionHandle,Integer> handle2index = new Function<InstructionHandle,Integer>() {
	    public Integer get(InstructionHandle handle) {
		return blocks.indexOf(cfg.getBlockFromStart(handle));
	    }
	};
	Printer inside = out.tab("  ");
	InstructionCompiler compiler = new InstructionCompiler(inside, this.classgen, this.munger, handle2index);
	for (int i = 0; i < blocks.size(); i++) {
	    CFG.Block block = blocks.get(i);
	    out.println("blocks.push(function() { // block %s", i);
	    boolean hasExceptions = !block.exceptions().isEmpty();

	    if (hasExceptions) {
		inside.println("try {");
	    }

	    // print in either case
	    for (InstructionHandle curr = block.getStart(); ; curr = curr.getNext()) {
		compiler.compileInstruction(curr);
		if (curr == block.getEnd())
		    break;
	    }
	    if (!block.hasTerminator()) {
		inside.println("// fallthrough");
		inside.println("blockIndex = %s;", handle2index.get(block.getEnd().getNext()));
	    }

	    if (hasExceptions) {
		inside.println("} catch (exception) {");
		StringBuilder exlist = new StringBuilder();
		for (CFG.ExceptionTarget target : block.exceptions()) {
		    int blockIndex = handle2index.get(target.target.getStart());
		    if (exlist.length() > 0) exlist.append(", ");
		    if (target.type == null)
			exlist.append(String.format("{type:null, index:%s}", blockIndex));
		    else
			exlist.append(String.format("{type:%s, index:%s}", resolve(target.type.getClassName()), blockIndex));
		}
		inside.println("  stack = [exception];");
		inside.println("  blockIndex = Util.catch_exception(exception, [%s]);", exlist);
		inside.println("}");
	    }

	    out.println("});");
	}
	out.println("blockIndex = %s;", blocks.indexOf(cfg.getStart()));
	out.println("while (blockIndex>=0) {");
	out.println("   blocks[blockIndex]();");
	out.println("}");
    }

    public static String resolve(String classname) {
	return String.format("Util.resolveClass(\"%s\")", classname);
    }

    private void compileMethods(Printer out, List<Method> methods) throws CompilationException {
	Printer inside = out.tab("  ");

	int length = methods.size();
	for (int i = 0; i < length; i++) {
	    Method method = methods.get(i);

	    String munged;
	    if (method.getName().equals("<init>")) {
		munged = this.munger.mungeInit(this.classgen.getClassName(), method.getArgumentTypes());
	    } else if (method.getName().equals("<clinit>")) {
		munged = this.munger.mungeClinit(this.classgen.getClassName());
	    } else {
		munged = this.munger.mungeMethodName(this.classgen.getClassName(), method.getName(), method.getReturnType(), method.getArgumentTypes(), method.isStatic());
	    }
	    String argstr = getArgStr(method);

	    out.println("%s : function(%s) {", munged, argstr);
         
	    if (method.isNative()) {
		inside.println("return Util.invoke_native(%s, \"%s\", \"%s\", [%s]);", 
			       method.isStatic() ? "null" : "this", 
			       this.classgen.getClassName(), 
			       munged, 
			       argstr);
	    } else if (method.isAbstract()) {
		inside.println("Util.assertWithMessageException(false, \"Method %s.%s is abstract\", %s);", 
			       this.classgen.getClassName(), munged, resolve("java.lang.AbstractMethodError"));
	    } else {
		if (method.isStatic() && !method.getName().equals("<clinit>")) {
		    inside.println("Util.initialize(this);"); // call clinit if needed
		}
		methodStart(inside, method);
		MethodGen gen = new MethodGen(method, this.classgen.getClassName(), this.classgen.getConstantPool());
		CFG cfg = new CFG(gen.getInstructionList(), gen.getExceptionHandlers());
		compileCFG(inside, cfg);
		inside.println("return return_value;");
	    }

	    out.println("}%s", (i<length-1) ? "," : "");
	}
    }

    public void compile(Printer out) throws CompilationException {
	final String classname = this.classgen.getClassName();

	StringBuilder packagestr = new StringBuilder();
	for (int i = 0; i < pieces.length-1; i++) {
	    if (i>0) packagestr.append(", ");
	    packagestr.append('"').append(pieces[i]).append('"');
	}
	out.println("Util.add_package([%s]);", packagestr);

	out.println("Packages.%s = ", classname);
	out.println("  (function() {");
	out.println("     var cached = null;");
	out.println("     function create() {");
	out.println("       if (cached == null) {");
	out.println("         cached = Util.defineClass({");

	{
            Printer itemout = out.tab("           ");
            Printer memberout = itemout.tab("  ");

	    // classname
	    itemout.println("classname : \"%s\",", this.classgen.getClassName());

	    // class modifiers
	    itemout.println("modifiers : 0x%s,", Integer.toHexString(this.classgen.getModifiers()));

	    // isInterface
	    itemout.println("isInterface : %s,", this.classgen.isInterface());

            // superclass
	    String superclass = this.classgen.getSuperclassName();
	    if (superclass.equals(this.classgen.getClassName())) {
		// object
		itemout.println("superclass : null,");
	    } else {
		itemout.println("superclass : %s,", resolve(superclass));
	    }

            // interfaces
            String[] interfaceNames = this.classgen.getInterfaceNames();
            StringBuilder interfaces = new StringBuilder();
            for (int i = 0; i < interfaceNames.length; i++) {
		if (i>0) interfaces.append(", ");
		interfaces.append(String.format("%s", resolve(interfaceNames[i])));
            }
            itemout.println("interfaces : [%s],", interfaces);

            Field[] fields = this.classgen.getFields();
            Method[] methods = this.classgen.getMethods();

	    // memberinfo
	    Map<String,Integer> member2modifiers = new HashMap<String,Integer>();
	    for (Field field : fields) {
		String munged = this.munger.mungeFieldName(classname, field.getName(), field.getType(), field.isStatic());
		member2modifiers.put(munged, field.getModifiers());
	    }
	    for (Method method : methods) {
		String munged;
		if (method.getName().equals("<init>")) {
		    munged = this.munger.mungeInit(this.classgen.getClassName(), method.getArgumentTypes());
		} else if (method.getName().equals("<clinit>")) {
		    munged = this.munger.mungeClinit(this.classgen.getClassName());
		} else {
		    munged = this.munger.mungeMethodName(this.classgen.getClassName(), method.getName(), method.getReturnType(), method.getArgumentTypes(), method.isStatic());
		}
		member2modifiers.put(munged, method.getModifiers());
	    }

	    itemout.println("memberinfo : {");
	    for (Iterator<Map.Entry<String,Integer>> iter = member2modifiers.entrySet().iterator(); iter.hasNext(); ) {
		Map.Entry<String,Integer> entry = iter.next();
		itemout.println("  %s : 0x%s%s", entry.getKey(), Integer.toHexString(entry.getValue()), iter.hasNext() ? "," : "");
	    }
	    itemout.println("},");

            // fields
            itemout.println("fields : {");
            {
		List<Field> myfields = new ArrayList<Field>(fields.length);
		for (int i = 0; i < fields.length; i++) {
		    Field field = fields[i];
		    if (!field.isStatic())
			myfields.add(field);
		}

		for (Iterator<Field> iter = myfields.iterator(); iter.hasNext(); ) {
		    Field field = iter.next();
		    if (!field.isStatic()) {
			String munged = this.munger.mungeFieldName(classname, field.getName(), field.getType(), false);
			memberout.println("%s : %s%s", 
					  munged,
					  initValue(field.getSignature()),
					  iter.hasNext() ? "," : "");
		    }
		}
            }
            itemout.println("},");

	    ConstantPool cp = this.classgen.getConstantPool().getConstantPool();

            // static fields
            itemout.println("static_fields : {");
            {
		List<Field> myfields = new ArrayList<Field>(fields.length);
		for (int i = 0; i < fields.length; i++) {
		    Field field = fields[i];
		    if (field.isStatic()) {
			myfields.add(field);
		    }
		}

		for (Iterator<Field> iter = myfields.iterator(); iter.hasNext(); ) {
		    Field field = iter.next();
		    if (field.isStatic()) {
			String munged = this.munger.mungeFieldName(classname, field.getName(), field.getType(), true);
			String initValue = initValue(field.getSignature());
			ConstantValue cv = field.getConstantValue();
			if (cv != null) {
			    Type type = field.getType();
			    if (type.equals(Type.LONG)) {
				long value = ((ConstantLong)cp.getConstant(cv.getConstantValueIndex())).getBytes();
				initValue = String.format("new Long(%s,%s)", value&0xFFFFFFFFL, (value>>>32)&0xFFFFFFFFL);
			    } else if (type.equals(Type.DOUBLE)) {
				double value = ((ConstantDouble)cp.getConstant(cv.getConstantValueIndex())).getBytes();
				initValue = "" + value;
			    } else if (type.equals(Type.FLOAT)) {
				float value = ((ConstantFloat)cp.getConstant(cv.getConstantValueIndex())).getBytes();
				initValue = "" + value;
			    } else if (type.equals(Type.INT) ||
				       type.equals(Type.SHORT) ||
				       type.equals(Type.CHAR) ||
				       type.equals(Type.BYTE) ||
				       type.equals(Type.BOOLEAN)) {
				int value = ((ConstantInteger)cp.getConstant(cv.getConstantValueIndex())).getBytes();
				initValue = String.format("new Integer(%s)", value);
			    } else if (type.equals(Type.STRING)) {
				String value = ((ConstantString)cp.getConstant(cv.getConstantValueIndex())).getBytes(cp);
				initValue = String.format("Util.js2java_string(\"%s\")", InstructionCompiler.escapeLiteralString(value));
			    } else {
				throw new RuntimeException("Invalid type for ConstantValue attribute: " + type);
			    }
			}

			memberout.println("%s : %s%s", 
					  munged, 
					  initValue,
					  iter.hasNext() ? "," : "");
		    }
		}
            }
            itemout.println("},");

            // methods
            itemout.println("methods : {");
            {
		List<Method> mymethods = new ArrayList<Method>(methods.length);
		for (int i = 0; i < methods.length; i++) {
		    Method method = methods[i];
		    if (!method.isStatic()) {
			mymethods.add(method);
		    }
		}
		compileMethods(memberout, mymethods);
            }
            itemout.println("},");

            // static methods
            itemout.println("static_methods : {");
            {
		List<Method> mymethods = new ArrayList<Method>(methods.length);
		for (int i = 0; i < methods.length; i++) {
		    Method method = methods[i];
		    if (method.isStatic()) {
			mymethods.add(method);
		    }
		}
		compileMethods(memberout, mymethods);
            }
            itemout.println("}");
	}

	out.println("         });");
	out.println("       }");
	out.println("       return cached;");
	out.println("     }");
	out.println("     return create;");
	out.println("  })();");
    }
}
