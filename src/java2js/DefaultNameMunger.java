package java2js;

import org.apache.bcel.generic.*;

public class DefaultNameMunger implements NameMunger {
   private String escape(String str) {
      // escape char is '$'
      StringBuilder result = new StringBuilder(str.length()*2);
      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         switch (c) {
         case '$':
         case '_': // used as a separator
         case 'A': // used to indicate array type
         case 'L': // used to indicate start of classname
         case 'E': // used to indicate end of classname
            result.append('$').append(c);
         break;

         case '.':
         case '/':
            result.append("$D");
         break;

         default:
            result.append(c);
            break;
         }
      }
      return result.toString();
   }

   private String mungeType(Type type) {
      if (type instanceof ArrayType) {
         return "A" + mungeType(((ArrayType)type).getElementType());
      } else if (type instanceof ObjectType) {
         return "L" + escape(((ObjectType)type).getClassName()) + "E";
      } else {
         // includes void 'V'
         return escape(type.getSignature());
      }
   }

   public String mungeInit(String classname, Type[] argtypes) {
      StringBuilder result = new StringBuilder("init_");
      for (Type t : argtypes) {
         result.append(mungeType(t));
      }
      return result.append('_').toString();
   }

   public String mungeClinit(String classname) {
      return "clinit";
   }

   public String mungeFieldName(String classname, String fieldname, Type type, boolean isStatic) {
      return new StringBuilder("field_").append(escape(fieldname)).append('_').append(mungeType(type)).toString();
   }

   public String mungeMethodName(String classname, String methodname, Type returnType, Type[] argtypes, boolean isStatic) {
      StringBuilder result = new StringBuilder("method_").append(escape(methodname)).append('_').append(mungeType(returnType)).append('_');
      for (Type t : argtypes) {
         result.append(mungeType(t));
      }
      return result.append('_').toString();
   }
}
