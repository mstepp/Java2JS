package java2js;

import java.util.*;
import java.io.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

public class ClassFinder {
    private static class Item {
	final Type type, prev;
	Item(Type _type, Type _prev) {
	    this.type = _type;
	    this.prev = _prev;
	}
    }

    public Map<ObjectType,ObjectType> getReferencedClasses(String classname) throws IOException {
	return getReferencedClasses(new ObjectType(classname));
    }

    public Map<ObjectType,ObjectType> getReferencedClasses(Type startType) throws IOException {
	LinkedList<Item> worklist = new LinkedList<Item>();
	addReferencedTypes(startType, null, worklist);
	return getAllClasses(worklist);
    }

    public Map<ObjectType,ObjectType> getReferencedClasses(JavaClass clazz) throws IOException {
	LinkedList<Item> worklist = new LinkedList<Item>();
	addReferencedTypes(clazz, null, worklist);
	return getAllClasses(worklist);
    }

    private void addReferencedTypes(Type type, Type prev, LinkedList<Item> worklist) throws IOException {
	worklist.addLast(new Item(type, prev));
	while (type instanceof ArrayType) {
	    type = ((ArrayType)type).getElementType();
	    worklist.addLast(new Item(type, prev));
	}

	if (type instanceof ObjectType) {
	    String classname = ((ObjectType)type).getClassName();
	    String resourceName = classname.replaceAll("[.]", "/") + ".class";
	    InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resourceName);
	    JavaClass clazz = new ClassParser(stream, resourceName).parse();
	    addReferencedTypes(clazz, prev, worklist);
	}
    }

    private void addReferencedTypes(JavaClass clazz, Type prev, LinkedList<Item> worklist) {
	Type type = new ObjectType(clazz.getClassName());
	worklist.addLast(new Item(type, prev));
	ConstantPool cp = clazz.getConstantPool();
	for (Constant c : cp.getConstantPool()) {
	    if (c instanceof ConstantClass) {
		String name = ((ConstantClass)c).getBytes(cp);
		if (name.startsWith("[")) {
		    // array type, descriptor
		    worklist.addLast(new Item(Type.getType(name), type));
		} else {
		    // 'java/lang/String'
		    worklist.addLast(new Item(new ObjectType(name.replaceAll("/", ".")), type));
		}
	    } else if (c instanceof ConstantFieldref) {
		ConstantNameAndType nameAndType = (ConstantNameAndType)cp.getConstant(((ConstantCP)c).getNameAndTypeIndex());
		String signature = nameAndType.getSignature(cp);
		worklist.addLast(new Item(Type.getType(signature), type));
	    } else if (c instanceof ConstantInterfaceMethodref ||
		       c instanceof ConstantMethodref) {
		ConstantNameAndType nameAndType = (ConstantNameAndType)cp.getConstant(((ConstantCP)c).getNameAndTypeIndex());
		String methodSignature = nameAndType.getSignature(cp);
		for (Type arg : Type.getArgumentTypes(methodSignature)) {
		    worklist.addLast(new Item(arg, type));
		}
		worklist.addLast(new Item(Type.getReturnType(methodSignature), type));
	    }
	}
    }

    private Map<ObjectType,ObjectType> getAllClasses(LinkedList<Item> worklist) throws IOException {
	// map types to referers
	Map<Type,Type> seen = new HashMap<Type,Type>();
	Map<ObjectType,ObjectType> classes = new HashMap<ObjectType,ObjectType>();

	while (worklist.size() > 0) {
	    Item next = worklist.removeFirst();
	    if (seen.containsKey(next.type))
		continue;
	    seen.put(next.type, next.prev);
	    if (next.type instanceof ObjectType) {
		Type prev = next.prev;
		while (prev != null && !(prev instanceof ObjectType)) {
		    prev = seen.get(prev);
		}
		classes.put((ObjectType)next.type, (ObjectType)prev);
	    }
	    addReferencedTypes(next.type, next.prev, worklist);
	}
	seen = null;
	worklist = null;

	return classes;
    }

    public static void main(String args[]) throws IOException {
	if (args.length < 1) {
	    System.err.println("USAGE: ClassFinder <classname> [map]");
	    System.exit(1);
	}
	ClassFinder finder = new ClassFinder();
	Map<ObjectType,ObjectType> typemap = finder.getReferencedClasses(args[0]);
	
	if (args.length > 1) {
	    System.out.println("digraph {");
	    for (Map.Entry<ObjectType,ObjectType> entry : typemap.entrySet()) {
		String key = entry.getKey().toString();
		String value = String.valueOf(entry.getValue());
		System.out.printf("  node%s [label=\"%s\"];\n", key.hashCode() & 0xFFFFFFFFL, key);
		if (value != null) {
		    System.out.printf("  node%s -> node%s [dir=back];\n", key.hashCode() & 0xFFFFFFFFL, value.hashCode() & 0xFFFFFFFFL);
		}
	    }
	    System.out.println("}");
	} else {
	    for (ObjectType type : typemap.keySet()) {
		System.out.println(type);
	    }
	}
    }
}
