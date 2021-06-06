package tfc.java_interpreter;

import com.tfc.bytecode.utils.Access;
import com.tfc.bytecode.utils.Formatter;
import com.tfc.bytecode.utils.Parser;
import com.tfc.bytecode.utils.class_structure.*;
import tfc.expression_solver.Expression;
import tfc.expression_solver.ExpressionParser;
import tfc.java_interpreter.natives.IntegerClass;
import tfc.java_interpreter.reflections.ReflectionClass;
import tfc.java_interpreter.structure.InterpretedClass;
import tfc.java_interpreter.structure.InterpretedField;
import tfc.java_interpreter.structure.InterpretedMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public class Interpreter {
	public final ExpressionParser parser = new ExpressionParser();
	public final HashMap<String, InterpretedClass> classes = new HashMap<>();
	public final InterpretedClass intClass;
	
	public Interpreter() {
		InterpretedClass interpretedClass = new IntegerClass();
		classes.put(interpretedClass.name, interpretedClass);
		intClass = interpretedClass;
	}
	
	public static Interpreter createWithReflection() {
		Interpreter interpreter = new Interpreter();
		InterpretedClass interpretedClass = new ReflectionClass();
		interpreter.classes.put(interpretedClass.name, interpretedClass);
		return interpreter;
	}
	
	public Function<String, String> classFileGetter = (s) -> null;
	
	public InterpretedClass read(String s) {
		return $getOrLoad(s);
	}
	
	public InterpretedClass getOrLoad(String s) {
		if (classes.containsKey(s)) return classes.get(s);
		return $getOrLoad(classFileGetter.apply(s));
	}
	
	public InterpretedClass $getOrLoad(String file) {
		file = Formatter.formatForCompile(file);
		System.out.println(file);
		ClassNode n = Parser.parse(file);
		if (classes.containsKey(n.name)) return classes.get(n.name);
		String access = Access.parseAccess(n.modifs);
		InterpretedClass clazz = new InterpretedClass(access.contains("final"), EnumProtectionLevel.get(access), n.name);
		System.out.println(clazz.toString());
		classes.put(clazz.name, clazz);
		for (FieldNodeSource field : n.fields) {
			Expression expression = parser.parse(field.code);
			FieldNode node = new FieldNode(field);
			access = Access.parseAccess(node.access);
			InterpretedField field1 = new InterpretedField(access.contains("final"), EnumProtectionLevel.get(access), getOrLoad(field.getType()));
			field1.defaultExpression = expression;
			field1.isStatic = access.contains("static");
			// TODO: setup parser to set value of object
			expression.get();
			clazz.fields.put(field1.name = field.getName(), field1);
		}
		for (MethodNodeSource method : n.methods) {
			MethodNode node = new MethodNode(method);
			access = Access.parseAccess(node.access);
			InterpretedMethod m = new InterpretedMethod(access.contains("final"), EnumProtectionLevel.get(access), method.getName());
			m.interpreter = this;
			m.descArgs = node.desc;
			String[] lines = method.code.split("\n");
			m.lines = new ArrayList<>(Arrays.asList(lines).subList(1, lines.length - 1));
			System.out.println(method.getType());
			clazz.methods.put(method.getName(), m);
		}
		return clazz;
	}
}
