package tfc.java_interpreter;

import com.tfc.bytecode.utils.Access;
import com.tfc.bytecode.utils.Formatter;
import com.tfc.bytecode.utils.Parser;
import com.tfc.bytecode.utils.class_structure.*;
import tfc.expression_solver.Expression;
import tfc.expression_solver.ExpressionParser;
import tfc.java_interpreter.configuration.InterpreterConfiguration;
import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.data.LangObject;
import tfc.java_interpreter.natives.numbers.*;
import tfc.java_interpreter.reflections.ReflectionClass;
import tfc.java_interpreter.structure.InterpretedClass;
import tfc.java_interpreter.structure.InterpretedField;
import tfc.java_interpreter.structure.InterpretedMethod;
import tfc.utils.logging.LogColors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public class Interpreter {
	public final ExpressionParser parser = new ExpressionParser();
	public final HashMap<String, InterpretedClass> classes = new HashMap<>();
	
	public final IntegerClass intClass;
	public final DoubleClass doubleClass;
	public final FloatClass floatClass;
	public final LongClass longClass;
	public final BooleanClass booleanClass;
	public final ShortClass shortClass;
	
	public final InterpreterConfiguration configuration = new InterpreterConfiguration();
	
	public InterpretedClass[] nativeTypes() {
		return new InterpretedClass[]{
				intClass, doubleClass, floatClass, longClass, booleanClass, shortClass
		};
	}
	
	public Interpreter() {
		parser.methods.add(new JavaMethodMarker());
		intClass = registerNativeClass(new IntegerClass());
		doubleClass = registerNativeClass(new DoubleClass());
		floatClass = registerNativeClass(new FloatClass());
		longClass = registerNativeClass(new LongClass());
		booleanClass = registerNativeClass(new BooleanClass());
		shortClass = registerNativeClass(new ShortClass());
	}
	
	private <T extends InterpretedClass> T registerNativeClass(T clazz) {
		classes.put(clazz.name, clazz);
		clazz.interpreter = this;
		return clazz;
	}
	
	public static Interpreter createWithReflection() {
		Interpreter interpreter = new Interpreter();
		InterpretedClass interpretedClass = new ReflectionClass();
		interpreter.classes.put(interpretedClass.name, interpretedClass);
		return interpreter;
	}
	
	public Function<String, String> classFileGetter = (s) -> null;
	
	public LangObject getStaticContext(InterpretedClass clazz) {
		LangObject o1 = new LangObject();
		InterpretedObject interp1 = new InterpretedObject();
		interp1.clazz = clazz;
		interp1.isStaticContext = true;
		o1.val = interp1;
		return o1;
	}
	
	public LangObject box(int val) {
		LangObject o1 = new LangObject();
		InterpretedObject interp1 = new InterpretedObject();
		interp1.clazz = intClass;
		interp1.isStaticContext = false;
		interp1.obj = val;
		o1.val = interp1;
		return o1;
	}
	
	public LangObject createInstance(InterpretedClass clazz) {
		LangObject o1 = new LangObject();
		InterpretedObject interp1 = new InterpretedObject();
		interp1.clazz = clazz;
		interp1.isStaticContext = false;
		o1.val = interp1;
		// TODO: call default constructor
		return o1;
	}
	
	public static String readFile(String path, String name) {
		FileInputStream stream = null;
		byte[] bytes = new byte[0];
		try {
			File file = new File(path + name.replace(".", "/") + ".java");
			stream = new FileInputStream(file);
			bytes = new byte[stream.available()];
			stream.read(bytes);
		} catch (IOException err) {
			err.printStackTrace();
		}
		if (stream == null) return null;
		try {
			stream.close();
		} catch (IOException err) {
			err.printStackTrace();
		}
		return new String(bytes);
	}
	
	public InterpretedClass read(String s) {
		return $getOrLoad(s);
	}
	
	public InterpretedClass getOrLoad(String s) {
		if (classes.containsKey(s)) return classes.get(s);
		return $getOrLoad(classFileGetter.apply(s));
	}
	
	public InterpretedClass $getOrLoad(String file) {
		file = Formatter.formatForCompile(file);
//		System.out.println(file);
		ClassNode n = Parser.parse(file);
		if (configuration.verboseClassLoading) {
			System.out.println(LogColors.TEXT_CYAN + "loading class: " + LogColors.TEXT_BLUE + n.name + LogColors.TEXT_RESET);
			System.out.println(LogColors.TEXT_CYAN + "  extends: " + LogColors.TEXT_BLUE + n.superName + LogColors.TEXT_RESET);
			if (n.interfaces.length >= 1) System.out.println(LogColors.TEXT_CYAN + "  inherits:");
			for (String anInterface : n.interfaces)
				System.out.println(LogColors.TEXT_BLUE + "    " + anInterface + LogColors.TEXT_RESET);
		}
		if (classes.containsKey(n.name)) return classes.get(n.name);
		String access = Access.parseAccess(n.modifs);
		if (configuration.verboseClassLoading)
			System.out.println(LogColors.TEXT_CYAN + "  access: " + LogColors.TEXT_BLUE + access.trim() + LogColors.TEXT_RESET);
		InterpretedClass clazz = new InterpretedClass(access.contains("final"), EnumProtectionLevel.get(access), n.name);
//		System.out.println(clazz.toString());
		classes.put(clazz.name, clazz);
		clazz.interpreter = this;
		if (configuration.verboseClassLoading)
			System.out.println(LogColors.TEXT_GREEN + "  methods:" + LogColors.TEXT_RESET);
		for (MethodNodeSource method : n.methods) {
			MethodNode node = new MethodNode(method);
			access = Access.parseAccess(node.access);
			InterpretedMethod m = new InterpretedMethod(access.contains("final"), EnumProtectionLevel.get(access), method.getName(), clazz);
			m.interpreter = this;
			m.descArgs = node.desc;
			String args = method.code.substring(method.code.indexOf(method.getName() + "(") + (method.getName().length()));
			args = args.substring(1, args.indexOf(")")).trim();
			String[] lines = method.code.split("\n");
			m.isStatic = access.contains("static");
			m.lines = new ArrayList<>(Arrays.asList(lines).subList(1, lines.length - 1));
			if (configuration.verboseClassLoading) {
				System.out.println(LogColors.TEXT_BLUE + "    " + m.name + " : " + LogColors.TEXT_PURPLE + m.lines.size());
				System.out.println(LogColors.TEXT_CYAN + "      access: " + LogColors.TEXT_BLUE + access.trim());
				System.out.println(LogColors.TEXT_CYAN + "      static: " + LogColors.TEXT_BLUE + m.isStatic + LogColors.TEXT_RESET);
			}
			if (args.length() != 0) {
				if (configuration.verboseClassLoading) System.out.println(LogColors.TEXT_GREEN + "      args:");
				for (String s : args.split(", ")) {
					String[] parts = s.split(" ");
					if (configuration.verboseClassLoading)
						System.out.println(LogColors.TEXT_PURPLE + "        " + parts[0] + " : " + LogColors.TEXT_BLUE + parts[1] + LogColors.TEXT_RESET);
					m.argNames.add(parts[1]);
				}
			}
			clazz.methods.put(method.getName(), m);
		}
		if (configuration.verboseClassLoading)
			System.out.println(LogColors.TEXT_GREEN + "  fields:" + LogColors.TEXT_RESET);
		for (FieldNodeSource field : n.fields) {
			FieldNode node = new FieldNode(field);
//			System.out.println(field.getType().split(" ")[0]);
//			System.out.println(Access.parseAccess(node.access) + field.getType().split(" ")[0] + " " + node.name);
			access = Access.parseAccess(node.access);
			InterpretedField field1 = new InterpretedField(access.contains("final"), EnumProtectionLevel.get(access), getOrLoad(field.getType()));
			field1.isStatic = access.contains("static");
			LangObject workingVar = new LangObject();
			{
				JavaMethodMarker.locals = null;
				JavaMethodMarker.invoker = getStaticContext(clazz);
				JavaMethodMarker.workingVar = workingVar;
			}
			String s = field.code.substring((Access.parseAccess(node.access) + " " + field.getType().split(" ")[0] + node.name).length() + 3);
			Expression expression = parser.parse(s.substring(0, s.length() - 1));
			expression = configuration.expressionOptimizations.optimize(expression);
			field1.defaultExpression = expression;
			double val = expression.get();
			field1.value = createInstance(field1.type);
//			if (!s.equals("")) {
//				Expression expression = parser.parse(s.substring(0, s.length() - 1));
//				expression = configuration.expressionOptimizations.optimize(expression);
//				field1.defaultExpression = expression;
//				{
//					double val = expression.get();
//					field1.value = createInstance(field1.type);
			field1.value.val = workingVar.val == null ? val : workingVar.val;
//				}
//			} else {
//				field1.value = createInstance(field1.type);
//				((InterpretedObject)field1.value.val).obj = null;
//			}
//			System.out.println(field1.value.val);
			double d = field1.defaultExpression != null ? field1.defaultExpression.get() : 0;
			if (configuration.verboseClassLoading) {
				System.out.println(LogColors.TEXT_PURPLE + "    " + field.getType() + " : " + LogColors.TEXT_BLUE + field.getName());
				System.out.println(LogColors.TEXT_CYAN + "      default: " + LogColors.TEXT_BLUE + field1.defaultExpression + " = " + LogColors.TEXT_PURPLE + d);
				System.out.println(LogColors.TEXT_CYAN + "      access: " + LogColors.TEXT_BLUE + access.trim());
				System.out.println(LogColors.TEXT_CYAN + "      static: " + LogColors.TEXT_BLUE + field1.isStatic + LogColors.TEXT_RESET);
			}
			clazz.fields.put(field1.name = field.getName(), field1);
		}
		System.out.println(LogColors.TEXT_CYAN + "finished loading class: " + LogColors.TEXT_BLUE + n.name + LogColors.TEXT_RESET);
		return clazz;
	}
	
	public Object run(InterpretedClass clazz, String name, Object... args) {
		LangObject[] objects = new LangObject[args.length];
		InterpretedClass[] classes = new InterpretedClass[args.length];
		for (int i = 0; i < args.length; i++) {
			LangObject o = createInstance(getOrLoad(objects[i].getClass().getName().replace("/", ".")));
			((InterpretedObject) o.val).obj = args[i];
			classes[i] = ((InterpretedObject) o.val).clazz;
		}
		InterpretedMethod method = clazz.getMethod(name, classes);
		Object o = method.invoke(getStaticContext(null), getStaticContext(clazz), objects);
		return unbox(o);
	}
	
	public static Object unbox(Object o) {
		if (o instanceof LangObject) return unbox(((LangObject) o).val);
		else if (o instanceof InterpretedObject) return unbox(((InterpretedObject) o).obj);
		else return o;
	}
}
