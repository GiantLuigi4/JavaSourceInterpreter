package tfc.java_interpreter.structure;

import tfc.expression_solver.Expression;
import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.JavaMethodMarker;
import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.data.LangObject;
import tfc.java_interpreter.data.VoidReturnMarker;
import tfc.utils.logging.LogColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InterpretedMethod {
	public final InterpretedClass owner;
	public ArrayList<String> lines = new ArrayList<>();
	public boolean isFinal;
	public EnumProtectionLevel protection;
	public final String name;
	public boolean isStatic = false;
	public String descArgs;
	public Interpreter interpreter;
	public ArrayList<String> argNames = new ArrayList<>();
	
	protected static int stackDepth = 0;
	
	protected static String getVerboseLoggingPrefix() {
		StringBuilder sb = new StringBuilder(LogColors.TEXT_CYAN);
		for (int i = 0; i < stackDepth; i++) sb.append("|  ");
		sb.append(LogColors.TEXT_RESET);
		return sb.toString();
	}
	
	protected void log(String msg) {
		if (interpreter.configuration.verboseMethodExecution) System.out.print(msg);
	}
	
	protected void logPrefix(String msg) {
		if (interpreter.configuration.verboseMethodExecution) System.out.print(getVerboseLoggingPrefix() + msg);
	}
	
	protected void logLinePrefix(String msg) {
		if (interpreter.configuration.verboseMethodExecution) System.out.println(getVerboseLoggingPrefix() + msg);
	}
	
	public InterpretedMethod(boolean isFinal, EnumProtectionLevel protection, String name, InterpretedClass owner) {
		this.isFinal = isFinal;
		this.protection = protection;
		this.name = name;
		this.owner = owner;
	}
	
	public Object checkAndInvoke(LangObject invoker, LangObject invoked, LangObject... args) {
		if (checkInvoke(invoker, invoked)) return invoke(invoker, invoked, args);
		throw new RuntimeException(new IllegalAccessError()); // TODO: detail this
	}
	
	public boolean checkInvoke(LangObject invoker, LangObject invoked) {
		if (invoker.val instanceof InterpretedObject) {
			if (protection.isPublic()) return true;
			InterpretedClass clazz = ((InterpretedObject) invoker.val).clazz;
			if (protection.isProtected())
				return
						clazz.getPackage().equals(((InterpretedObject) invoked.val).clazz.getPackage()) ||
								((InterpretedObject) invoker.val).inheritsFrom(((InterpretedObject) invoked.val).clazz);
			else if (protection.isPrivate()) return ((InterpretedObject) invoker.val).clazz.equals(((InterpretedObject)invoked.val).clazz);
		}
		return true;
	}
	
	@Override
	public String toString() {
		return '(' + "name: " + name + ", lines: " + lines.size() + ')';
	}
	
	public Object invoke(LangObject invoker, LangObject invoked, LangObject... args) {
		HashMap<String, LangObject> locals = new HashMap<>();
		logLinePrefix(name);
		stackDepth++;
		int codeBlockDepth = 0;
		for (int i = 0; i < args.length; i++) locals.put(argNames.get(i), args[i]);
		loopLines:
		for (String line : lines) {
//			System.out.println("\t" + line); //
			if (line.startsWith("{")) {
				codeBlockDepth++;
				logLinePrefix(LogColors.TEXT_YELLOW + "{" + LogColors.TEXT_RESET);
				continue;
			} else if (line.startsWith("}")) {
				ArrayList<String> toClear = new ArrayList<>();
				for (String typeName : locals.keySet()) {
					if (locals.get(typeName).yeetMarker == codeBlockDepth) {
						toClear.add(typeName);
					}
				}
				toClear.forEach(locals::remove);
				logLinePrefix(LogColors.TEXT_YELLOW + "} " + LogColors.TEXT_WHITE + "// cleared " + toClear.size() + " locals" + LogColors.TEXT_RESET);
				codeBlockDepth--;
				continue;
			}
			logLinePrefix(LogColors.TEXT_YELLOW + line + LogColors.TEXT_RESET); // TODO: better colors to mimic basic syntax highlighting
			if (line.startsWith("return")) {
//				System.out.println(); //
				stackDepth--;
				if (descArgs.endsWith("V")) return VoidReturnMarker.INSTANCE;
				LangObject returnVal = new LangObject();
				String val = line.substring("return".length()).trim();
				val = val.substring(0, val.length() - 1);
				for (String localName : locals.keySet()) {
					if (localName.equals(val)) {
						InterpretedObject object = ((InterpretedObject) (locals.get(localName).val));
						Object o = object.obj;
						return o;
					}
				}
				LangObject workingVar = new LangObject();
				{
					JavaMethodMarker.locals = locals;
					JavaMethodMarker.invoker = invoked;
					JavaMethodMarker.workingVar = workingVar;
				}
				Expression ex = interpreter.parser.parse(val);
				double val1 = ex.get();
				workingVar.val = workingVar.val == null ? val1 : workingVar.val;
				return workingVar.val;
			}
			for (InterpretedClass interpretedClass : interpreter.nativeTypes()) {
				if (line.startsWith(interpretedClass.name + " ")) {
					String s = line.substring((interpretedClass.name + " ").length());
					String[] parts = s.split(" ");
					String name = "";
					StringBuilder ex = new StringBuilder();
					for (int i = 0; i < parts.length; i++) {
						if (i == 0) {
							name = parts[i];
							LangObject obj = new LangObject();
							obj.yeetMarker = codeBlockDepth;
							locals.put(name, obj);
						} else if (i == 1) {
							if (!Objects.equals(parts[i], "="))
								throw new RuntimeException(new IllegalArgumentException(name + ":" + line)); // TODO: descriptions of exceptions
						} else {
							ex.append(parts[i]).append(" ");
						}
					}
					ex = new StringBuilder(ex.substring(0, ex.length() - 2));
					LangObject obj = locals.get(name);
					obj.val = new InterpretedObject();
					((InterpretedObject) obj.val).isStaticContext = false;
					((InterpretedObject) obj.val).clazz = interpretedClass;
					if (!ex.toString().equals("")) {
						LangObject workingVar = new LangObject();
						{
							JavaMethodMarker.locals = locals;
							JavaMethodMarker.invoker = invoked;
							JavaMethodMarker.workingVar = workingVar;
						}
						Expression expression = interpreter.parser.parse(ex.toString());
						((InterpretedObject) obj.val).obj = expression.get();
					}
					continue;
				}
			}
			LangObject obj = getObject(locals, line);
			if (obj != null) {
				String objStr = getObjectStr(locals, line);
				String operator = getAssignmentOperator(line, objStr);
				InterpretedObject object = ((InterpretedObject) obj.val);
				Object o = object.obj;
				LangObject workingVar = new LangObject();
				{
					JavaMethodMarker.locals = locals;
					JavaMethodMarker.invoker = invoked;
					JavaMethodMarker.workingVar = workingVar;
				}
				if (o instanceof Number && operator.charAt(0) != '=') {
					object.obj = doOperator(((Number) o).doubleValue(), operator.charAt(0), interpreter.parser.parse(getAssignmentExpression(line, objStr)));
				} else if (operator.equals("=")) {
					double val = interpreter.parser.parse(getAssignmentExpression(line, objStr)).get();
					object.obj = workingVar.val == null ? val : workingVar.val;
				} else throw new RuntimeException("Cannot use operators on non numbers.");
//				object.obj = NumberClasses.cast(object, object.clazz);
//				if (object.clazz.equals(interpreter.intClass)) object.obj = ((Number) object.obj).intValue();
				logLinePrefix(LogColors.TEXT_WHITE + "// " + objStr + " = " + object.obj + LogColors.TEXT_RESET);
//				System.out.println(object.obj);
				continue;
			}
			if (invoked.val != null) {
				HashMap<String, LangObject> fields = ((InterpretedObject) invoked.val).getFields();
				obj = getObject(fields, line);
				if (obj != null) {
					String objStr = getObjectStr(fields, line);
					String operator = getAssignmentOperator(line, objStr);
					InterpretedObject object = ((InterpretedObject) obj.val);
					Object o = object.obj;
					LangObject workingVar = new LangObject();
					{
						JavaMethodMarker.locals = locals;
						JavaMethodMarker.invoker = invoked;
						JavaMethodMarker.workingVar = workingVar;
					}
					if (o instanceof Number && operator.charAt(0) != '=') {
						object.obj = doOperator(((Number) o).doubleValue(), operator.charAt(0), interpreter.parser.parse(getAssignmentExpression(line, objStr)));
					} else if (operator.equals("=")) {
						double val = interpreter.parser.parse(getAssignmentExpression(line, objStr)).get();
						object.obj = workingVar.val == null ? val : workingVar.val;
					} else throw new RuntimeException("Cannot use operators on non numbers.");
					if (object.clazz.equals(interpreter.intClass)) object.obj = ((Number) object.obj).intValue();
//					if (((InterpretedObject) obj.val).obj instanceof Number) ((InterpretedObject) obj.val).obj = NumberClasses.cast(object, ((InterpretedObject) obj.val).clazz);
					logLinePrefix(LogColors.TEXT_BLACK + "// " + objStr + " = " + object.obj + LogColors.TEXT_RESET);
//					System.out.println(object.obj);
					continue;
				}
			}
		}
		stackDepth--;
		return VoidReturnMarker.INSTANCE;
	}
	
	public LangObject getObject(HashMap<String, LangObject> locals, String line) {
		for (String typeName : locals.keySet()) {
			if (!line.startsWith(typeName)) continue;
			boolean isCorrect = line.substring(typeName.length()).startsWith(".");
			if (!isCorrect && !line.substring(typeName.length()).startsWith(" ")) continue;
			if (isCorrect)
				return getObject(((InterpretedObject) locals.get(typeName).val).getFields(), line.substring(typeName.length() + 1));
			else return locals.get(typeName);
		}
		return null;
	}
	
	public String getObjectStr(HashMap<String, LangObject> locals, String line) {
		for (String typeName : locals.keySet()) {
			if (!line.startsWith(typeName)) continue;
			boolean isCorrect = line.substring(typeName.length()).startsWith(".");
			if (!isCorrect && !line.substring(typeName.length()).startsWith(" ")) continue;
			if (isCorrect)
				return line.substring(0, typeName.length()) + "." + getObjectStr(((InterpretedObject) locals.get(typeName).val).getFields(), line.substring(typeName.length() + 1));
			else return line.substring(0, typeName.length());
		}
		return null;
	}
	
	protected boolean isAssignment(String line, String localName) {
		if (line.startsWith(localName)) {
			line = line.substring(localName.length()).trim();
			if (isOperator(line.charAt(0)) && line.charAt(1) == '=') return true;
			return line.startsWith("=");
		}
		return false;
	}
	
	protected String getAssignmentOperator(String line, String localName) {
		if (line.startsWith(localName)) {
			line = line.substring(localName.length()).trim();
			if (line.startsWith("=")) return "=";
			if (isOperator(line.charAt(0)) && line.charAt(1) == '=') return line.charAt(0) + "=";
		}
		return null;
	}
	
	protected String getAssignmentExpression(String line, String localName) {
		if (line.startsWith(localName)) {
			String operator = getAssignmentOperator(line, localName);
			line = line.substring(localName.length()).trim();
			line = line.substring(operator.length()).trim();
			return line.substring(0, line.length()-1).trim();
		}
		return null;
	}
	
	protected boolean isOperator(char c) {
		return c == '=' || c == '/' || c == '*' || c == '+' || c == '-' || c == '%' || c == '&' || c == '|' || c == '^';
	}
	
	public double doOperator(double val, char op, Expression expression) {
		switch (op) {
			case '=': return expression.get();
			case '/': return val / expression.get();
			case '*': return val * expression.get();
			case '-': return val - expression.get();
			case '+': return val + expression.get();
			case '&': return (int) val & (int) expression.get();
			case '|': return (int) val | (int) expression.get();
			case '^': return (int) val ^ (int) expression.get();
			case '%': return val % expression.get();
		}
		return Double.NaN;
	}
}
