package java2js;

import org.apache.bcel.generic.*;

public interface NameMunger {
   String mungeInit(String classname, Type[] argtypes);
   String mungeClinit(String classname);
   String mungeFieldName(String classname, String fieldname, Type type, boolean isStatic);
   String mungeMethodName(String classname, String methodname, Type returnType, Type[] argtypes, boolean isStatic);
}
