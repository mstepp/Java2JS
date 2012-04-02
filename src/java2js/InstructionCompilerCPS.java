package java2js;

import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;

public class InstructionCompilerCPS {
   private final Printer out;
   private final ClassGen classgen;
   private final ConstantPoolGen cpg;
   private final String unqualified;
   private final Function<InstructionHandle,Integer> h2i;

   public InstructionCompilerCPS(Printer _out, 
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
   private void inst(String message, Object... args) {
      println("last = %s;", String.format(message, args));
   }
   private void di(String name) {
      inst("%s(last, exc)", name);
   }
   private void dif(String name, int targetIndex, int ftIndex) {
      inst("%s(blocks[%s], blocks[%s], exp)", name, targetIndex, ftIndex);
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
         di("aconst_null");
      } 
      else if (inst instanceof DADD) {
         di("dadd");
      } 
      else if (inst instanceof DDIV) {
         di("ddiv");
      }
      else if (inst instanceof DMUL) {
         di("dmul");
      }
      else if (inst instanceof DNEG) {
         di("dneg");
      }
      else if (inst instanceof DREM) {
         di("drem");
      }
      else if (inst instanceof DSUB) {
         di("dsub");
      }
      else if (inst instanceof FADD) {
         di("fadd");
      }
      else if (inst instanceof FDIV) {
         di("fdiv");
      }
      else if (inst instanceof FMUL) {
         di("fmul");
      }
      else if (inst instanceof FNEG) {
         di("fneg");
      }
      else if (inst instanceof FREM) {
         di("frem");
      }
      else if (inst instanceof FSUB) {
         di("fsub");
      }
      else if (inst instanceof IADD) {
         di("iadd");
      }
      else if (inst instanceof IAND) {
         di("iand");
      }
      else if (inst instanceof IDIV) {
         di("idiv");
      }
      else if (inst instanceof IMUL) {
         di("imul");
      }
      else if (inst instanceof INEG) {
         di("ineg");
      }
      else if (inst instanceof IOR) {
         di("ior");
      }
      else if (inst instanceof IREM) {
         di("irem");
      }
      else if (inst instanceof ISHL) {
         di("ishl");
      }
      else if (inst instanceof ISHR) {
         di("ishr");
      }
      else if (inst instanceof ISUB) {
         di("isub");
      }
      else if (inst instanceof IUSHR) {
         di("iushr");
      }
      else if (inst instanceof IXOR) {
         di("ixor");
      }
      else if (inst instanceof LADD) {
         di("ladd");
      }
      else if (inst instanceof LAND) {
         di("land");
      }
      else if (inst instanceof LDIV) {
         di("ldiv");
      }
      else if (inst instanceof LMUL) {
         di("lmul");
      }
      else if (inst instanceof LNEG) {
         di("lneg");
      }
      else if (inst instanceof LOR) {
         di("lor");
      }
      else if (inst instanceof LREM) {
         di("lrem");
      }
      else if (inst instanceof LSHL) {
         di("lshl");
      }
      else if (inst instanceof LSHR) {
         di("lshr");
      }
      else if (inst instanceof LSUB) {
         di("lsub");
      }
      else if (inst instanceof LUSHR) {
         di("lushr");
      }
      else if (inst instanceof LXOR) {
         di("lxor");
      }
      else if (inst instanceof AALOAD ||
               inst instanceof BALOAD ||
               inst instanceof CALOAD ||
               inst instanceof FALOAD ||
               inst instanceof IALOAD ||
               inst instanceof SALOAD) {
         di("aload");
      }
      else if (inst instanceof DALOAD ||
               inst instanceof LALOAD) {
         di("aload2");
      }
      else if (inst instanceof AASTORE ||
               inst instanceof BASTORE ||
               inst instanceof CASTORE ||
               inst instanceof FASTORE ||
               inst instanceof IASTORE ||
               inst instanceof SASTORE) {
         di("astore");
      }
      else if (inst instanceof DASTORE ||
               inst instanceof LASTORE) {
         di("astore2");
      }
      else if (inst instanceof ARRAYLENGTH) {
         di("arraylength");
      }
      else if (inst instanceof ATHROW) {
         di("athrow");
      }
      else if (inst instanceof BIPUSH) {
         inst("bipush(%s, exc)", String.valueOf(((BIPUSH)inst).getValue().intValue()));
      }
      else if (inst instanceof GotoInstruction) {
         InstructionHandle target = ((GotoInstruction)inst).getTarget();
         println("last = frame[\"goto\"](blocks[%s], exc);", h2i.get(target));
      }
      else if (inst instanceof IfInstruction) {
         compileIfInstruction(handle);
      }
      else if (inst instanceof JsrInstruction) {
         int targetIndex = h2i.get(((JsrInstruction)inst).getTarget());
         int fallThroughIndex = h2i.get(handle.getNext());
         inst("jsr(blocks[%s], blocks[%s], exc)", targetIndex, fallThroughIndex);
      }
      else if (inst instanceof Select) {
         println("last = function() {");
         Select select = (Select)inst;
         println("   switch(frame.POP().bits | 0) {");
         int[] matches = select.getMatchs();
         InstructionHandle[] targets = select.getTargets();
         int i;
         for (i = 0; i < matches.length; i++) {
            println("      case %s: return blocks[%s];", matches[i], h2i.get(targets[i]));
         }
         println("      default: return blocks[%s];", h2i.get(targets[i]));
         println("   }");
         println("};");
      }
      else if (inst instanceof D2F) {
         di("d2f");
      }
      else if (inst instanceof D2I) {
         di("d2i");
      }
      else if (inst instanceof F2D) {
         di("f2d");
      }
      else if (inst instanceof F2I) {
         di("f2i");
      }
      else if (inst instanceof I2B) {
         di("i2b");
      }
      else if (inst instanceof I2C) {
         di("i2c");
      }
      else if (inst instanceof I2D) {
         di("i2d");
      }
      else if (inst instanceof I2F) {
         di("i2f");
      }
      else if (inst instanceof I2L) {
         di("i2l");
      }
      else if (inst instanceof I2S) {
         di("i2s");
      }
      else if (inst instanceof L2I) {
         di("l2i");
      }
      else if (inst instanceof D2L) {
         di("d2l");
      }
      else if (inst instanceof F2L) {
         di("f2l");
      }
      else if (inst instanceof L2D) {
         di("l2d");
      } 
      else if (inst instanceof L2F) {
         di("l2f");
      }
      else if (inst instanceof ANEWARRAY) {
         Type elementType = ((ANEWARRAY)inst).getType(cpg);
         inst("anwearray(\"%s\", last, exc)", elementType.toString());
      }
      else if (inst instanceof CHECKCAST) {
         String typename = ((CHECKCAST)inst).getType(cpg).toString();
         inst("checkcast(\"%s\", last, exc)", typename);
      }
      else if (inst instanceof GETFIELD) {
         GETFIELD getfield = (GETFIELD)inst;
         Type type = getfield.getFieldType(cpg);
         String classname = getfield.getReferenceType(cpg).toString();
         String fieldname = getfield.getFieldName(cpg);
         String signature = getfield.getSignature(cpg);
         inst("getfield(\"%s\", last, exc)", fieldname + "." + signature);
      }
      else if (inst instanceof GETSTATIC) {
         GETSTATIC getstatic = (GETSTATIC)inst;
         Type type = getstatic.getFieldType(cpg);
         String classname = getstatic.getReferenceType(cpg).toString();
         String fieldname = getstatic.getFieldName(cpg);
         String signature = getstatic.getSignature(cpg);
         inst("getstatic(\"%s\", \"%s\", last, exc)", classname, fieldname + "." + signature);
      }
      else if (inst instanceof PUTFIELD) {
         PUTFIELD putfield = (PUTFIELD)inst;
         Type type = putfield.getFieldType(cpg);
         String classname = putfield.getReferenceType(cpg).toString();
         String fieldname = putfield.getFieldName(cpg);
         String signature = putfield.getSignature(cpg);
         inst("putfield(\"%s\", last, exc)", fieldname + "." + signature);
      }
      else if (inst instanceof PUTSTATIC) {
         PUTSTATIC putstatic = (PUTSTATIC)inst;
         Type type = putstatic.getFieldType(cpg);
         String classname = putstatic.getReferenceType(cpg).toString();
         String fieldname = putstatic.getFieldName(cpg);
         String signature = putstatic.getSignature(cpg);
         inst("putstatic(\"%s\", \"%s\", last, exc)", classname, fieldname + "." + signature);
      }
      else if (inst instanceof INVOKESPECIAL) {
         InvokeInstruction invoke = (InvokeInstruction)inst;
         Type[] args = invoke.getArgumentTypes(cpg);
         Type returnType = invoke.getReturnType(cpg);
         ReferenceType type = invoke.getReferenceType(cpg);
         if (type instanceof ArrayType) {
            type = Type.OBJECT;
         }
         String methodname = invoke.getMethodName(cpg);
         String signature = invoke.getSignature(cpg);
         inst("invokespecial(\"%s\", \"%s\", last, exc)", type.toString(), methodname + signature);
      }
      else if (inst instanceof INVOKEINTERFACE ||
               inst instanceof INVOKEVIRTUAL) {
         InvokeInstruction invoke = (InvokeInstruction)inst;
         Type[] args = invoke.getArgumentTypes(cpg);
         Type returnType = invoke.getReturnType(cpg);
         ReferenceType type = invoke.getReferenceType(cpg);
         if (type instanceof ArrayType) {
            type = Type.OBJECT;
         }
         String methodname = invoke.getMethodName(cpg);
         String signature = invoke.getSignature(cpg);
         String instruction = (inst instanceof INVOKEVIRTUAL) ? "invokevirtual" : "invokeinterface";
         inst("%s(\"%s\", last, exc)", instruction, methodname + signature);
      }
      else if (inst instanceof INVOKESTATIC) {
         InvokeInstruction invoke = (InvokeInstruction)inst;
         Type[] args = invoke.getArgumentTypes(cpg);
         Type returnType = invoke.getReturnType(cpg);
         String classname = invoke.getReferenceType(cpg).toString();
         String methodname = invoke.getMethodName(cpg);
         String signature = invoke.getSignature(cpg);
         inst("invokestatic(\"%s\", \"%s\", last, exc)", classname, methodname + signature);
      }
      else if (inst instanceof INSTANCEOF) {
         Type type = ((INSTANCEOF)inst).getType(cpg);
         println("last = frame[\"instanceof\"](\"%s\", last, exc);", type.toString());
      }
      else if (inst instanceof LDC) {
         Object value = ((LDC)inst).getValue(cpg);
         if (value instanceof String) {
            inst("ldc_string(\"%s\", last, exc)", escapeLiteralString((String)value));
         } else if (value instanceof Integer) {
            inst("ldc_value(new Integer(%s), last, exc)", value);
         } else if (value instanceof ConstantClass) {
            String classname = ((ConstantClass)value).getBytes(cpg.getConstantPool());
            if (classname.startsWith("[")) {
               // array, signature
               Type type = Type.getType(classname);
               classname = type.toString();
            }
            inst("ldc_class(\"%s\", last, exc)", escapeLiteralString(classname.replaceAll("/", ".")));
         } else {
            inst("ldc_value(%s, last, exc)", value.toString());
         }
      }
      else if (inst instanceof LDC2_W) {
         Object value = ((LDC2_W)inst).getValue(cpg);
         if (value instanceof Long) {
            long lvalue = ((Long)value).longValue();
            int low = (int)(lvalue & 0xFFFFFFFF);
            int high = (int)((lvalue>>>32) & 0xFFFFFFFF);
            inst("ldc2_w(new Long(%s, %s), last, exc)", low, high);
         } else if (value instanceof Double) {
            inst("ldc2_w(%s, last, exc)", value.toString());
         } else {
            throw new CompilationException("Unsupported type: " + value.getClass());
         }
      }
      else if (inst instanceof MULTIANEWARRAY) {
         MULTIANEWARRAY multi = (MULTIANEWARRAY)inst;
         ArrayType type = (ArrayType)multi.getType(cpg);
         int ndims = multi.getDimensions();
         inst("multianewarray(%s, \"%s\", last, exc)", ndims, type.toString());
      }
      else if (inst instanceof NEW) {
         NEW newinst = (NEW)inst;
         ObjectType type = (ObjectType)newinst.getType(cpg);
         println("last = frame[\"new\"](\"%s\", last, exc);", type.getClassName());
      }
      else if (inst instanceof DCMPG) {
         di("dcmpg");
      } 
      else if (inst instanceof FCMPG) {
         di("fcmpg");
      } 
      else if (inst instanceof DCMPL) {
         di("dcmpl");
      } 
      else if (inst instanceof FCMPL) {
         di("fcmpl");
      }
      else if (inst instanceof DCONST) {
         double value = ((DCONST)inst).getValue().doubleValue();
         inst("dconst(%s, last, exc)", value);
      }
      else if (inst instanceof FCONST) {
         double value = ((FCONST)inst).getValue().doubleValue();
         inst("fconst(%s, last, exc)", value);
      }
      else if (inst instanceof ICONST) {
         int value = ((ICONST)inst).getValue().intValue();
         inst("iconst(%s, last, exc)", value);
      }
      else if (inst instanceof LCMP) {
         di("lcmp");
      }
      else if (inst instanceof LCONST) {
         long value = ((LCONST)inst).getValue().longValue();
         String svalue;
         if (value == 0L)
            svalue = "Long.ZERO";
         else if (value == 1L)
            svalue = "Long.ONE";
         else
            throw new CompilationException("Bad value for LCONST: " + value);
         inst("lconst(%s, last, exc", svalue);
      }
      else if (inst instanceof IINC) {
         IINC iinc = (IINC)inst;
         inst("iinc(%s, %s, last, exc)", iinc.getIndex(), iinc.getIncrement());
      }
      else if (inst instanceof ALOAD ||
               inst instanceof ILOAD ||
               inst instanceof FLOAD) {
         LoadInstruction load = (LoadInstruction)inst;
         inst("load(%s, last, exc)", load.getIndex());
      }
      else if (inst instanceof DLOAD ||
               inst instanceof LLOAD) {
         LoadInstruction load = (LoadInstruction)inst;
         inst("load2(%s, last, exc)", load.getIndex());
      }
      else if (inst instanceof ASTORE ||
               inst instanceof ISTORE ||
               inst instanceof FSTORE) {
         StoreInstruction store = (StoreInstruction)inst;
         inst("store(%s, last, exc)", store.getIndex());
      }
      else if (inst instanceof DSTORE || 
               inst instanceof LSTORE) {
         StoreInstruction store = (StoreInstruction)inst;
         inst("store2(%s, last, exc)", store.getIndex());
      }
      else if (inst instanceof MONITORENTER ||
               inst instanceof MONITOREXIT) {
         di("monitorenter");
      }
      else if (inst instanceof NEWARRAY) {
         Type type = ((NEWARRAY)inst).getType();
         String typeString = makeTypeObject(((NEWARRAY)inst).getType());
         inst("newarray(%s, last, exc)", typeString);
      }
      else if (inst instanceof NOP) {
         di("nop");
      }
      else if (inst instanceof RET) {
         int index = ((RET)inst).getIndex();
         inst("ret(%s, last, exc)", index);
      }
      else if (inst instanceof ARETURN ||
               inst instanceof IRETURN ||
               inst instanceof FRETURN) {
         inst("return1(cont, exc)");
      }
      else if (inst instanceof DRETURN ||
               inst instanceof LRETURN) {
         inst("return2(cont, exc)");
      }
      else if (inst instanceof RETURN) {
         println("last = frame[\"return\"](cont, exc);");
      }
      else if (inst instanceof SIPUSH) {
         short value = ((SIPUSH)inst).getValue().shortValue();
         inst("sipush(%s, last, exc)", value);
      }
      else if (inst instanceof DUP) {
         di("dup");
      }
      else if (inst instanceof DUP_X1) {
         di("dup_x1");
      }
      else if (inst instanceof DUP_X2) {
         di("dup_x2");
      }
      else if (inst instanceof DUP2) {
         di("dup2");
      }
      else if (inst instanceof DUP2_X1) {
         di("dup2_x1");
      }
      else if (inst instanceof DUP2_X2) {
         di("dup2_x2");
      }
      else if (inst instanceof POP) {
         di("pop");
      }
      else if (inst instanceof POP2) {
         di("pop2");
      }
      else if (inst instanceof SWAP) {
         di("swap");
      }
      else {
         throw new CompilationException("Unsupported instruction type: " + inst);
      }
   }

   private void compileIfInstruction(InstructionHandle handle) throws CompilationException {
      IfInstruction inst = (IfInstruction)handle.getInstruction();
      int tIndex = h2i.get(inst.getTarget());
      int fIndex = h2i.get(handle.getNext());

      if (inst instanceof IF_ACMPEQ) {
         dif("if_acmpeq", tIndex, fIndex);
      } 
      else if (inst instanceof IF_ACMPNE) {
         dif("if_acmpne", tIndex, fIndex);
      } 
      else if (inst instanceof IF_ICMPEQ) {
         dif("if_icmpeq", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPNE) {
         dif("if_icmpne", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPGE) {
         dif("if_icmpge", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPGT) {
         dif("if_icmpgt", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPLE) {
         dif("if_icmple", tIndex, fIndex);
      }
      else if (inst instanceof IF_ICMPLT) {
         dif("if_icmplt", tIndex, fIndex);
      }
      else if (inst instanceof IFEQ) {
         dif("ifeq", tIndex, fIndex);
      }
      else if (inst instanceof IFNE) {
         dif("ifne", tIndex, fIndex);
      }
      else if (inst instanceof IFGE) {
         dif("ifge", tIndex, fIndex);
      }
      else if (inst instanceof IFGT) {
         dif("ifgt", tIndex, fIndex);
      }
      else if (inst instanceof IFLE) {
         dif("ifle", tIndex, fIndex);
      }
      else if (inst instanceof IFLT) {
         dif("iflt", tIndex, fIndex);
      }
      else if (inst instanceof IFNULL) {
         dif("ifnull", tIndex, fIndex);
      }
      else if (inst instanceof IFNONNULL) {
         dif("ifnonnull", tIndex, fIndex);
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
}
