package java2js;

import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;

public class InstructionCompiler {
   private final Printer out;
   private final ClassGen classgen;
   private final ConstantPoolGen cpg;
   private final String unqualified;
   private final Function<InstructionHandle,Integer> h2i;

   public InstructionCompiler(Printer _out, 
                              ClassGen _classgen,
                              Function<InstructionHandle,Integer> _h2i) {
      this.out = _out;
      this.classgen = _classgen;
      this.h2i = _h2i;
      this.cpg = this.classgen.getConstantPool();

      String unqualified;
      String classname = this.classgen.getClassName();
      if (classname.indexOf('.') < 0) {
         unqualified = classname;
      } else {
         String[] pieces = classname.split("[.]");
         unqualified = pieces[pieces.length-1];
      }
      this.unqualified = unqualified;
   }

   private void println(String message, Object... args) {
      this.out.println(message, args);
   }
   private void comment(String message, Object... args) {
      println("// " + message, args);
   }
   private void pop(String name, Object... args) {
      println("var %s = stack.pop();", String.format(name, args));
   }
   private void pop2(String name, Object... args) {
      println("var %s = stack.pop(); stack.pop();", String.format(name, args));
   }
   private void push(String value, Object... args) {
      println("stack.push(%s);", String.format(value, args));
   }
   private void push2(String value, Object... args) {
      result(value, args);
      println("stack.push(result); stack.push(result);");
   }
   private void result(String result, Object... args) {
      println("var result = %s;", String.format(result, args));
   }

   private void assertNonNull(String value) {
      println("Util.assertWithException((%s) != null, %s);", value, Compiler.resolve("java.lang.NullPointerException"));
   }

   private void assertWithException(String test, String exceptionName) {
      println("Util.assertWithException((%s), %s);", test, Compiler.resolve(exceptionName));
   }

   private String makeTypeObject(Type type) {
      if (type instanceof ArrayType) {
         return String.format("new ArrayType(%s)", makeTypeObject(((ArrayType)type).getElementType()));
      } else if (type instanceof ObjectType) {
         return String.format("%s", Compiler.resolve(((ObjectType)type).getClassName()));
      } else if (type.equals(Type.BOOLEAN)) {
         return "PrimitiveType.BOOLEAN";
      } else if (type.equals(Type.BYTE)) {
         return "PrimitiveType.BYTE";
      } else if (type.equals(Type.CHAR)) {
         return "PrimitiveType.CHAR";
      } else if (type.equals(Type.DOUBLE)) {
         return "PrimitiveType.DOUBLE";
      } else if (type.equals(Type.FLOAT)) {
         return "PrimitiveType.FLOAT";
      } else if (type.equals(Type.INT)) {
         return "PrimitiveType.INT";
      } else if (type.equals(Type.LONG)) {
         return "PrimitiveType.LONG";
      } else if (type.equals(Type.SHORT)) {
         return "PrimitiveType.SHORT";
      } else {
         throw new RuntimeException("Invalid type: " + type);
      }
   }

   public void compileInstruction(InstructionHandle handle) throws CompilationException {
      Instruction inst = handle.getInstruction();
      comment(inst.toString());
      if (inst instanceof ACONST_NULL) {
         println("stack.push(null);");
      } 
      else if (inst instanceof DADD) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs + rhs");
      } 
      else if (inst instanceof DDIV) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs / rhs");
      }
      else if (inst instanceof DMUL) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs * rhs");
      }
      else if (inst instanceof DNEG) {
         pop2("value");
         push2("-value");
      }
      else if (inst instanceof DREM) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs % rhs");
      }
      else if (inst instanceof DSUB) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs - rhs");
      }
      else if (inst instanceof FADD) {
         pop("rhs");
         pop("lhs");
         push("lhs + rhs");
      }
      else if (inst instanceof FDIV) {
         pop("rhs");
         pop("lhs");
         push("lhs / rhs");
      }
      else if (inst instanceof FMUL) {
         pop("rhs");
         pop("lhs");
         push("lhs * rhs");
      }
      else if (inst instanceof FNEG) {
         pop("value");
         push("-value");
      }
      else if (inst instanceof FREM) {
         pop("rhs");
         pop("lhs");
         push("lhs % rhs");
      }
      else if (inst instanceof FSUB) {
         pop("rhs");
         pop("lhs");
         push("lhs - rhs");
      }
      else if (inst instanceof IADD) {
         pop("rhs");
         pop("lhs");
         push("lhs.add(rhs)");
      }
      else if (inst instanceof IAND) {
         pop("rhs");
         pop("lhs");
         push("lhs.and(rhs)");
      }
      else if (inst instanceof IDIV) {
         pop("rhs");
         pop("lhs");
         push("lhs.divmod(rhs).div");
      }
      else if (inst instanceof IMUL) {
         pop("rhs");
         pop("lhs");
         push("lhs.multiply(rhs)");
      }
      else if (inst instanceof INEG) {
         pop("value");
         push("value.negate()");
      }
      else if (inst instanceof IOR) {
         pop("rhs");
         pop("lhs");
         push("lhs.or(rhs)");
      }
      else if (inst instanceof IREM) {
         pop("rhs");
         pop("lhs");
         push("lhs.divmod(rhs).mod");
      }
      else if (inst instanceof ISHL) {
         pop("rhs");
         pop("lhs");
         push("lhs.shl(rhs)");
      }
      else if (inst instanceof ISHR) {
         pop("rhs");
         pop("lhs");
         push("lhs.shr(rhs)");
      }
      else if (inst instanceof ISUB) {
         pop("rhs");
         pop("lhs");
         push("lhs.subtract(rhs)");
      }
      else if (inst instanceof IUSHR) {
         pop("rhs");
         pop("lhs");
         push("lhs.ushr(rhs)");
      }
      else if (inst instanceof IXOR) {
         pop("rhs");
         pop("lhs");
         push("lhs.xor(rhs)");
      }
      else if (inst instanceof LADD) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.add(rhs)");
      }
      else if (inst instanceof LAND) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.and(rhs)");
      }
      else if (inst instanceof LDIV) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.divmod(rhs).div");
      }
      else if (inst instanceof LMUL) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.multiply(rhs)");
      }
      else if (inst instanceof LNEG) {
         pop2("value");
         push2("value.negate()");
      }
      else if (inst instanceof LOR) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.or(rhs)");
      }
      else if (inst instanceof LREM) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.divmod(rhs).mod");
      }
      else if (inst instanceof LSHL) {
         pop("rhs");
         pop2("lhs");
         push2("lhs.shl(rhs)");
      }
      else if (inst instanceof LSHR) {
         pop("rhs");
         pop2("lhs");
         push2("lhs.shr(rhs)");
      }
      else if (inst instanceof LSUB) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.subtract(rhs)");
      }
      else if (inst instanceof LUSHR) {
         pop("rhs");
         pop2("lhs");
         push2("lhs.ushr(rhs)");
      }
      else if (inst instanceof LXOR) {
         pop2("rhs");
         pop2("lhs");
         push2("lhs.xor(rhs)");
      }
      else if (inst instanceof AALOAD ||
               inst instanceof BALOAD ||
               inst instanceof CALOAD ||
               inst instanceof FALOAD ||
               inst instanceof IALOAD ||
               inst instanceof SALOAD) {
         pop("index");
         pop("array");
         assertNonNull("array");
         push("array.get(index.toJS())");
      }
      else if (inst instanceof DALOAD ||
               inst instanceof LALOAD) {
         pop("index");
         pop("array");
         assertNonNull("array");
         push2("array.get(index.toJS())");
      }
      else if (inst instanceof AASTORE ||
               inst instanceof BASTORE ||
               inst instanceof CASTORE ||
               inst instanceof FASTORE ||
               inst instanceof IASTORE ||
               inst instanceof SASTORE) {
         pop("value");
         pop("index");
         pop("array");
         assertNonNull("array");
         println("array.set(index.toJS(), value);");
      }
      else if (inst instanceof DASTORE ||
               inst instanceof LASTORE) {
         pop2("value");
         pop("index");
         pop("array");
         assertNonNull("array");
         println("array.set(index.toJS(), value);");
      }
      else if (inst instanceof ARRAYLENGTH) {
         pop("array");
         assertNonNull("array");
         push("new Integer(array.arraylength())");
      }
      else if (inst instanceof ATHROW) {
         pop("value");
         assertNonNull("value");
         println("throw value;");
      }
      else if (inst instanceof BIPUSH) {
         push("new Integer(%s)", String.valueOf(((BIPUSH)inst).getValue().intValue()));
      }
      else if (inst instanceof GotoInstruction) {
         int index = h2i.get(((GotoInstruction)inst).getTarget());
         println("blockIndex = %s;", index);
      }
      else if (inst instanceof IfInstruction) {
         int tIndex = h2i.get(((IfInstruction)inst).getTarget());
         int fIndex = h2i.get(handle.getNext());
         compileIfInstruction(tIndex, fIndex, (IfInstruction)inst);
      }
      else if (inst instanceof JsrInstruction) {
         int targetIndex = h2i.get(((JsrInstruction)inst).getTarget());
         int fallThroughIndex = h2i.get(handle.getNext());
         println("stack.push({address:%s});", fallThroughIndex);
         println("blockIndex = %s;", targetIndex);
      }
      else if (inst instanceof Select) {
         Select select = (Select)inst;
         println("switch(stack.pop().bits | 0) {");
         int[] matches = select.getMatchs();
         InstructionHandle[] targets = select.getTargets();
         for (int i = 0; i < matches.length; i++) {
            println("  case %s: blockIndex = %s; break;", matches[i], h2i.get(targets[i]));
         }
         println("  default: blockIndex = %s; break;", h2i.get(select.getTarget()));
         println("}");
      }
      else if (inst instanceof D2F) {
         pop2("value");
         push("value");
      }
      else if (inst instanceof D2I) {
         pop2("value");
         push("Util.d2i(value)");
      }
      else if (inst instanceof F2D) {
         pop("value");
         push2("value");
      }
      else if (inst instanceof F2I) {
         pop("value");
         push("Util.d2i(value)");
      }
      else if (inst instanceof I2B) {
         pop("value");
         push("value.i2b()");
      }
      else if (inst instanceof I2C) {
         pop("value");
         push("value.i2c()");
      }
      else if (inst instanceof I2D) {
         pop("value");
         push2("value.i2d()");
      }
      else if (inst instanceof I2F) {
         pop("value");
         push("value.i2d()");
      }
      else if (inst instanceof I2L) {
         pop("value");
         push2("value.i2l()");
      }
      else if (inst instanceof I2S) {
         pop("value");
         push("value.i2s()");
      }
      else if (inst instanceof L2I) {
         pop2("value");
         push("value.l2i()");
      }
      else if (inst instanceof D2L) {
         pop2("value");
         push2("Util.d2l(value)");
      }
      else if (inst instanceof F2L) {
         pop2("value");
         push("Util.d2l(value)");
      }
      else if (inst instanceof L2D) {
         pop2("value");
         push2("value.l2d()");
      } 
      else if (inst instanceof L2F) {
         pop2("value");
         push("Util.l2d(value)");
      }
      else if (inst instanceof ANEWARRAY) {
         Type elementType = ((ANEWARRAY)inst).getType(cpg);
         pop("length");
         assertWithException("!length.isNegative()", "java.lang.NegativeArraySizeException");
         push("Util.multianewarray([length], new ArrayType(%s))", makeTypeObject(elementType));
      }
      else if (inst instanceof CHECKCAST) {
         pop("obj");
         println("var check = Util.instance_of(obj, %s);", makeTypeObject(((CHECKCAST)inst).getType(cpg)));
         println("Util.assertWithException(check, %s);", Compiler.resolve("java.lang.ClassCastException"));
         push("obj");
      }
      else if (inst instanceof GETFIELD) {
         GETFIELD getfield = (GETFIELD)inst;
         Type type = getfield.getFieldType(cpg);
         String classname = getfield.getReferenceType(cpg).toString();
         String munged = getfield.getFieldName(cpg) + "." + getfield.getSignature(cpg);
         if (type.equals(Type.DOUBLE) ||
             type.equals(Type.LONG)) {
            pop("obj");
            assertNonNull("obj");
            push2("obj[\"%s\"]", munged);
         } else {
            pop("obj");
            assertNonNull("obj");
            push("obj[\"%s\"]", munged);
         }
      }
      else if (inst instanceof GETSTATIC) {
         GETSTATIC getstatic = (GETSTATIC)inst;
         Type type = getstatic.getFieldType(cpg);
         String classname = getstatic.getReferenceType(cpg).toString();
         String munged = getstatic.getFieldName(cpg) + "." + getstatic.getSignature(cpg);
         println("Util.initialize(%s);", Compiler.resolve(classname));
         if (type.equals(Type.DOUBLE) ||
             type.equals(Type.LONG)) {
            // don't need resolve because we already init'ed
            push2("Packages.%s()[\"%s\"]", classname, munged);
         } else {
            // don't need resolve because we already init'ed
            push("Packages.%s()[\"%s\"]", classname, munged);
         }
      }
      else if (inst instanceof PUTFIELD) {
         PUTFIELD putfield = (PUTFIELD)inst;
         Type type = putfield.getFieldType(cpg);
         String classname = putfield.getReferenceType(cpg).toString();
         String munged = putfield.getFieldName(cpg) + "." + putfield.getSignature(cpg);
         if (type.equals(Type.DOUBLE) ||
             type.equals(Type.LONG)) {
            pop2("value");
            pop("obj");
            assertNonNull("obj");
            println("obj[\"%s\"] = value;", munged);
         } else {
            pop("value");
            pop("obj");
            assertNonNull("obj");
            println("obj[\"%s\"] = value;", munged);
         }
      }
      else if (inst instanceof PUTSTATIC) {
         PUTSTATIC putstatic = (PUTSTATIC)inst;
         Type type = putstatic.getFieldType(cpg);
         String classname = putstatic.getReferenceType(cpg).toString();
         String munged = putstatic.getFieldName(cpg) + "." + putstatic.getSignature(cpg);
         println("Util.initialize(%s);", Compiler.resolve(classname));
         if (type.equals(Type.DOUBLE) ||
             type.equals(Type.LONG)) {
            pop2("value");
            // don't need resolve because we already init'ed
            println("Packages.%s()[\"%s\"] = value;", classname, munged);
         } else {
            pop("value");
            // don't need resolve because we already init'ed
            println("Packages.%s()[\"%s\"] = value;", classname, munged);
         }
      }
      else if (inst instanceof INVOKESPECIAL) {
         InvokeInstruction invoke = (InvokeInstruction)inst;
         Type[] args = invoke.getArgumentTypes(cpg);
         Type returnType = invoke.getReturnType(cpg);
         for (int i = args.length-1; i >= 0; i--) {
            if (args[i].equals(Type.DOUBLE) ||
                args[i].equals(Type.LONG)) {
               pop2("arg%s", i);
            } else {
               pop("arg%s", i);
            }
         }
         pop("obj");
         assertNonNull("obj");

         ReferenceType type = invoke.getReferenceType(cpg);
         ObjectType mysupertype = new ObjectType(this.classgen.getSuperclassName());
         if (type instanceof ArrayType) {
            type = Type.OBJECT;
         }

         String munged = invoke.getMethodName(cpg) + invoke.getSignature(cpg);

         boolean returns = !returnType.equals(Type.VOID);
         String argstr = getArgStr(args.length);
         // write out super call
         if (returns) {
            println("var result = %s.prototype[\"%s\"].apply(obj, [%s]);", 
                    Compiler.resolve(((INVOKESPECIAL)inst).getReferenceType(cpg).toString()), munged, argstr);
         } else {
            println("%s.prototype[\"%s\"].apply(obj, [%s]);", 
                    Compiler.resolve(((INVOKESPECIAL)inst).getReferenceType(cpg).toString()), munged, argstr);
         }

         if (returnType.equals(Type.DOUBLE) || returnType.equals(Type.LONG)) {
            push2("result");
         } else if (!returnType.equals(Type.VOID)) {
            push("result");
         }
      }
      else if (inst instanceof INVOKEINTERFACE ||
               inst instanceof INVOKEVIRTUAL) {
         InvokeInstruction invoke = (InvokeInstruction)inst;
         Type[] args = invoke.getArgumentTypes(cpg);
         Type returnType = invoke.getReturnType(cpg);
         for (int i = args.length-1; i >= 0; i--) {
            if (args[i].equals(Type.DOUBLE) ||
                args[i].equals(Type.LONG)) {
               pop2("arg%s", i);
            } else {
               pop("arg%s", i);
            }
         }
         pop("obj");
         assertNonNull("obj");
         String munged = invoke.getMethodName(cpg) + invoke.getSignature(cpg);

         String argstr = getArgStr(args.length);
         if (returnType.equals(Type.VOID)) {
            println("obj[\"%s\"](%s);", munged, argstr);
         } else {
            println("var result = obj[\"%s\"](%s);", munged, argstr);
         }

         if (returnType.equals(Type.DOUBLE) || returnType.equals(Type.LONG)) {
            push2("result");
         } else if (!returnType.equals(Type.VOID)) {
            push("result");
         }
      }
      else if (inst instanceof INVOKESTATIC) {
         InvokeInstruction invoke = (InvokeInstruction)inst;
         Type[] args = invoke.getArgumentTypes(cpg);
         Type returnType = invoke.getReturnType(cpg);
         for (int i = args.length-1; i >= 0; i--) {
            if (args[i].equals(Type.DOUBLE) ||
                args[i].equals(Type.LONG)) {
               pop2("arg%s", i);
            } else {
               pop("arg%s", i);
            }
         }
         // should never be clinit, cannot call directly
         String classname = invoke.getReferenceType(cpg).toString();
         String munged = invoke.getMethodName(cpg) + invoke.getSignature(cpg);

         String argstr = getArgStr(args.length);
         if (returnType.equals(Type.VOID)) {
            println("%s[\"%s\"](%s);", Compiler.resolve(classname), munged, argstr);
         } else {
            println("var result = %s[\"%s\"](%s);", Compiler.resolve(classname), munged, argstr);
         }

         if (returnType.equals(Type.DOUBLE) || returnType.equals(Type.LONG)) {
            push2("result");
         } else if (!returnType.equals(Type.VOID)) {
            push("result");
         }
      }
      else if (inst instanceof INSTANCEOF) {
         Type type = ((INSTANCEOF)inst).getType(cpg);
         String typeString = makeTypeObject(type);
         pop("obj");
         push("Util.instance_of(obj, %s) ? Integer.ONE : Integer.ZERO", typeString);
      }
      else if (inst instanceof LDC) {
         Object value = ((LDC)inst).getValue(cpg);
         if (value instanceof String) {
            push("Util.js2java_string(\"%s\")", escapeLiteralString((String)value));
         } else if (value instanceof Integer) {
            push("new Integer(%s)", value);
         } else if (value instanceof ConstantClass) {
            String classname = ((ConstantClass)value).getBytes(cpg.getConstantPool());
            if (classname.startsWith("[")) {
               // array, signature
               Type type = Type.getType(classname);
               classname = type.toString();
            }
            push("Util.getClassByName(\"%s\")", escapeLiteralString(classname.replaceAll("/", ".")));
         } else {
            push(value.toString());
         }
      }
      else if (inst instanceof LDC2_W) {
         Object value = ((LDC2_W)inst).getValue(cpg);
         if (value instanceof Long) {
            long lvalue = ((Long)value).longValue();
            int low = (int)(lvalue & 0xFFFFFFFF);
            int high = (int)((lvalue>>>32) & 0xFFFFFFFF);
            push2("new Long(%s, %s)", low, high);
         } else if (value instanceof Double) {
            push2(value.toString());
         } else {
            throw new CompilationException("Unsupported type: " + value.getClass());
         }
      }
      else if (inst instanceof MULTIANEWARRAY) {
         MULTIANEWARRAY multi = (MULTIANEWARRAY)inst;
         ArrayType type = (ArrayType)multi.getType(cpg);
         int ndims = multi.getDimensions();
         println("var dims = new Array(%s);", ndims);
         for (int i = ndims-1; i >= 0; i--) {
            println("dims[%s] = stack.pop();", i);
         }
         push("Util.multianewarray(dims, %s)", makeTypeObject(type));
      }
      else if (inst instanceof NEW) {
         NEW newinst = (NEW)inst;
         ObjectType type = (ObjectType)newinst.getType(cpg);
         push("%s.newInstance()", Compiler.resolve(type.getClassName()));
      }
      else if (inst instanceof DCMPG) {
         pop2("rhs");
         pop2("lhs");
         push("Util.dcmpg(lhs, rhs)");
      } 
      else if (inst instanceof FCMPG) {
         pop("rhs");
         pop("lhs");
         push("Util.dcmpg(lhs, rhs)");
      } 
      else if (inst instanceof DCMPL) {
         pop2("rhs");
         pop2("lhs");
         push("Util.dcmpl(lhs, rhs)");
      } 
      else if (inst instanceof FCMPL) {
         pop("rhs");
         pop("lhs");
         push("Util.dcmpl(lhs, rhs)");
      }
      else if (inst instanceof DCONST) {
         double value = ((DCONST)inst).getValue().doubleValue();
         push2("%s", value);
      }
      else if (inst instanceof FCONST) {
         double value = ((FCONST)inst).getValue().doubleValue();
         push("%s", value);
      }
      else if (inst instanceof ICONST) {
         int value = ((ICONST)inst).getValue().intValue();
         push("new Integer(%s)", value);
      }
      else if (inst instanceof LCMP) {
         pop2("rhs");
         pop2("lhs");
         push("new Integer(lhs.compareTo(rhs))");
      }
      else if (inst instanceof LCONST) {
         long value = ((LCONST)inst).getValue().longValue();
         if (value == 0L)
            push2("Long.ZERO");
         else if (value == 1L)
            push2("Long.ONE");
         else
            throw new CompilationException("Bad value for LCONST: " + value);
      }
      else if (inst instanceof IINC) {
         IINC iinc = (IINC)inst;
         println("locals[%s] = locals[%s].add(new Integer(%s));", iinc.getIndex(), iinc.getIndex(), iinc.getIncrement());
      }
      else if (inst instanceof ALOAD ||
               inst instanceof ILOAD ||
               inst instanceof FLOAD) {
         LoadInstruction load = (LoadInstruction)inst;
         push("locals[%s]", load.getIndex());
      }
      else if (inst instanceof DLOAD ||
               inst instanceof LLOAD) {
         LoadInstruction load = (LoadInstruction)inst;
         push2("locals[%s]", load.getIndex());
      }
      else if (inst instanceof ASTORE ||
               inst instanceof ISTORE ||
               inst instanceof FSTORE) {
         StoreInstruction store = (StoreInstruction)inst;
         pop("value");
         println("locals[%s] = value;", store.getIndex());
      }
      else if (inst instanceof DSTORE || 
               inst instanceof LSTORE) {
         StoreInstruction store = (StoreInstruction)inst;
         pop2("value");
         println("locals[%s] = value;", store.getIndex());
      }
      else if (inst instanceof MONITORENTER ||
               inst instanceof MONITOREXIT) {
         // TODO throw exception?
         pop("obj");
         assertNonNull("obj");
      }
      else if (inst instanceof NEWARRAY) {
         Type type = ((NEWARRAY)inst).getType();
         pop("count");
         String typeString = makeTypeObject(((NEWARRAY)inst).getType());
         assertWithException("!count.isNegative()", "java.lang.NegativeArraySizeException");
         push("Util.multianewarray([count], %s)", typeString);
      }
      else if (inst instanceof NOP) {
         // noop
      }
      else if (inst instanceof RET) {
         int index = ((RET)inst).getIndex();
         println("blockIndex = locals[%s].address;", index);
      }
      else if (inst instanceof ARETURN ||
               inst instanceof IRETURN ||
               inst instanceof FRETURN) {
         pop("value");
         println("return_value = value;");
         println("blockIndex = -1;");
      }
      else if (inst instanceof DRETURN ||
               inst instanceof LRETURN) {
         pop2("value");
         println("return_value = value;");
         println("blockIndex = -1;");
      }
      else if (inst instanceof RETURN) {
         println("blockIndex = -1;");
      }
      else if (inst instanceof SIPUSH) {
         short value = ((SIPUSH)inst).getValue().shortValue();
         push("new Integer(%s)", (int)value);
      }
      else if (inst instanceof DUP) {
         pop("top");
         push("top");
         push("top");
      }
      else if (inst instanceof DUP_X1) {
         pop("top1");
         pop("top2");
         push("top1");
         push("top2");
         push("top1");
      }
      else if (inst instanceof DUP_X2) {
         pop("top1");
         pop("top2");
         pop("top3");
         push("top1");
         push("top3");
         push("top2");
         push("top1");
      }
      else if (inst instanceof DUP2) {
         pop("top1");
         pop("top2");
         push("top2");
         push("top1");
         push("top2");
         push("top1");
      }
      else if (inst instanceof DUP2_X1) {
         pop("top1");
         pop("top2");
         pop("top3");
         push("top2");
         push("top1");
         push("top3");
         push("top2");
         push("top1");
      }
      else if (inst instanceof DUP2_X2) {
         pop("top1");
         pop("top2");
         pop("top3");
         pop("top4");
         push("top2");
         push("top1");
         push("top4");
         push("top3");
         push("top2");
         push("top1");
      }
      else if (inst instanceof POP) {
         pop("value");
      }
      else if (inst instanceof POP2) {
         pop2("value");
      }
      else if (inst instanceof SWAP) {
         pop("top1");
         pop("top2");
         push("top1");
         push("top2");
      }
      else {
         throw new CompilationException("Unsupported instruction type: " + inst);
      }
   }

   private void cmpTwo(String format, int tIndex, int fIndex) {
      pop("rhs");
      pop("lhs");
      String test = String.format(format, "lhs", "rhs");
      println("blockIndex = ((%s) ? %s : %s);", test, tIndex, fIndex);
   }
   private void cmpOne(String format, int tIndex, int fIndex) {
      pop("value");
      String test = String.format(format, "value");
      println("blockIndex = ((%s) ? %s : %s);", test, tIndex, fIndex);
   }

   private void compileIfInstruction(final int tIndex, final int fIndex,
                                     final IfInstruction inst) throws CompilationException {
      if (inst instanceof IF_ACMPEQ) {
         cmpTwo("%s == %s", tIndex, fIndex);
      } 
      else if (inst instanceof IF_ACMPNE) {
         cmpTwo("%s != %s", tIndex, fIndex);
      } 
      else if (inst instanceof IF_ICMPEQ) {
         cmpTwo("%s.equals(%s)", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPNE) {
         cmpTwo("%s.notEquals(%s)", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPGE) {
         cmpTwo("%s.gte(%s)", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPGT) {
         cmpTwo("%s.gt(%s)", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPLE) {
         cmpTwo("%s.lte(%s)", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPLT) {
         cmpTwo("%s.lt(%s)", tIndex, fIndex);
      }
      else if (inst instanceof IFEQ) {
         cmpOne("%s.equals(Integer.ZERO)", tIndex, fIndex);
      }
      else if (inst instanceof IFNE) {
         cmpOne("%s.notEquals(Integer.ZERO)", tIndex, fIndex);
      }
      else if (inst instanceof IFGE) {
         cmpOne("%s.gte(Integer.ZERO)", tIndex, fIndex);
      }
      else if (inst instanceof IFGT) {
         cmpOne("%s.gt(Integer.ZERO)", tIndex, fIndex);
      }
      else if (inst instanceof IFLE) {
         cmpOne("%s.lte(Integer.ZERO)", tIndex, fIndex);
      }
      else if (inst instanceof IFLT) {
         cmpOne("%s.lt(Integer.ZERO)", tIndex, fIndex);
      }
      else if (inst instanceof IFNULL) {
         cmpOne("%s == null", tIndex, fIndex);
      }
      else if (inst instanceof IFNONNULL) {
         cmpOne("%s != null", tIndex, fIndex);
      }
      else {
         throw new CompilationException("Unsupported instruction: " + inst.getClass());
      }
   }

   private static final char HEX[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
   public static String escapeLiteralString(String str) {
      StringBuilder result = new StringBuilder(str.length()*2);
      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         switch (c) {
         case '\n': result.append("\\n"); break;
         case '\t': result.append("\\t"); break;
         case '\r': result.append("\\r"); break;
         case '\f': result.append("\\f"); break;
         case '\"': result.append("\\\""); break;
         default: 
            if (c > 127 || c < 32) {
               result.append("\\u").append(HEX[(c>>12)&0xF]).append(HEX[(c>>8)&0xF]).append(HEX[(c>>4)&0xF]).append(HEX[c&0xF]);
            } else {
               result.append(c);
            }
         }
      }
      return result.toString();
   }

   private String getArgStr(int length) {
      StringBuilder argstr = new StringBuilder();
      for (int i = 0; i < length; i++) {
         if (i>0) argstr.append(", ");
         argstr.append(String.format("arg%s", i));
      }
      return argstr.toString();
   }
}
