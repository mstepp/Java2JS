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
public class CompilerCPS {
   private final ClassGen classgen;
   private final String[] pieces;
   private final String unqualified;
   private final Logger logger;

   public CompilerCPS(ClassGen _classgen, Logger _logger) {
      this.classgen = _classgen;
      String classname = this.classgen.getClassName();
      this.pieces = classname.split("[.]");
      this.unqualified = pieces[pieces.length-1];
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
      StringBuilder localstr = new StringBuilder();
      int count = 0;
      if (!method.isStatic()) {
         inside.println("args.unshift(this);");
      }
      inside.println("var frame = new Frame(args, %s);", method.getCode().getMaxLocals());
   }

   private void compileCFG(Printer out, final CFG cfg) throws CompilationException {
      final List<CFG.Block> blocks = cfg.getBlocks();
      Function<InstructionHandle,Integer> handle2index = new Function<InstructionHandle,Integer>() {
         public Integer get(InstructionHandle handle) {
            return blocks.indexOf(cfg.getBlockFromStart(handle));
         }
      };
      Printer inside = out.tab("  ");
      out.println("var blocks = [];");
      InstructionCompilerCPS compiler = new InstructionCompilerCPS(inside, this.classgen, handle2index);
      for (int i = 0; i < blocks.size(); i++) {
         CFG.Block block = blocks.get(i);
         out.println("blocks.push(function() { // block %s", i);

         if (!block.hasTerminator()) {
            inside.println("// fallthrough");
            inside.println("var last = blocks[%s];", handle2index.get(block.getEnd().getNext()));
         } else {
            inside.println("var last;");
         }

         if (!block.exceptions().isEmpty()) {
            inside.println("var outerexc = exc;");
            inside.println("var exc = function(ex) {");
            inside.println("  frame.exception(ex);");
            
            StringBuilder exlist = new StringBuilder();
            for (CFG.ExceptionTarget target : block.exceptions()) {
               int blockIndex = handle2index.get(target.target.getStart());
               if (exlist.length() > 0) exlist.append(", ");
               if (target.type == null)
                  exlist.append(String.format("{type:null, cont:blocks[%s]}", blockIndex));
               else
                  exlist.append(String.format("{type:\"%s\", cont:blocks[%s]}", target.type.getClassName(), blockIndex));
            }
            inside.println("  return Util.catch_exception(ex, [%s], outerexc);", exlist);
            inside.println("};");
         }

         // print in reverse order
         for (InstructionHandle curr = block.getEnd(); ; curr = curr.getPrev()) {
            compiler.compileInstruction(curr);
            if (curr == block.getStart())
               break;
         }
         inside.println("return last;");
         out.println("});");
      }

      out.println("var start_block = blocks[%s];", handle2index.get(cfg.getStart().getStart()));
   }

   private void compileMethods(Printer out, List<Method> methods) throws CompilationException {
      Printer inside = out.tab("  ");

      int length = methods.size();
      for (int i = 0; i < length; i++) {
         Method method = methods.get(i);
         String signature = method.getSignature();

         out.println("\"%s\" : function(args, cont, exc) {", method.getName() + signature);
         
         if (method.isNative()) {
            inside.println("return Util.invoke_native(%s, \"%s\", \"%s\", args, cont, exc);", 
                           method.isStatic() ? "null" : "this", 
                           this.classgen.getClassName(), 
                           signature);
         } else if (method.isAbstract()) {
            inside.println("return Util.assertWithMessageException(false, \"Method %s.%s is abstract\", \"%s\", cont, exc);", 
                           this.classgen.getClassName(), signature, "java.lang.AbstractMethodError");
         } else {
            methodStart(inside, method);
            MethodGen gen = new MethodGen(method, this.classgen.getClassName(), this.classgen.getConstantPool());
            CFG cfg = new CFG(gen.getInstructionList(), gen.getExceptionHandlers());
            compileCFG(inside, cfg);
            if (method.isStatic() && !method.getName().equals("<clinit>")) {
               inside.println("return Util.initialize(this, start_block, exc);"); // call clinit if needed
            } else {
               inside.println("return start_block;");
            }
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
            itemout.println("superclass : \"%s\",", superclass);
         }

         // interfaces
         String[] interfaceNames = this.classgen.getInterfaceNames();
         StringBuilder interfaces = new StringBuilder();
         for (int i = 0; i < interfaceNames.length; i++) {
            if (i>0) interfaces.append(", ");
            interfaces.append(String.format("\"%s\"", interfaceNames[i]));
         }
         itemout.println("interfaces : [%s],", interfaces);

         Field[] fields = this.classgen.getFields();
         Method[] methods = this.classgen.getMethods();

         // memberinfo
         Map<String,Integer> member2modifiers = new HashMap<String,Integer>();
         for (Field field : fields) {
            member2modifiers.put(field.getName() + "." + field.getSignature(), field.getModifiers());
         }
         for (Method method : methods) {
            member2modifiers.put(method.getName() + method.getSignature(), method.getModifiers());
         }

         itemout.println("memberinfo : {");
         for (Iterator<Map.Entry<String,Integer>> iter = member2modifiers.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String,Integer> entry = iter.next();
            itemout.println("  \"%s\" : 0x%s%s", entry.getKey(), Integer.toHexString(entry.getValue()), iter.hasNext() ? "," : "");
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
                  memberout.println("\"%s\" : %s%s", 
                                    field.getName() + "." + field.getSignature(),
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
                  String signature = field.getSignature();
                  String initValue = initValue(signature);
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

                  memberout.println("\"%s\" : %s%s", 
                                    field.getName() + "." + signature, 
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
