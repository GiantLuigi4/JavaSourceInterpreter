package tfc.java_interpreter.structure;

import tfc.expression_solver.Expression;
import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.JavaMethodMarker;
import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.data.LangObject;
import tfc.java_interpreter.data.VoidReturnMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InterpretedMethod {
	public ArrayList<String> lines = new ArrayList<>();
	public boolean isFinal;
	public EnumProtectionLevel protection;
	public final String name;
	public boolean isStatic = false;
	public String descArgs;
	public Interpreter interpreter;
	public ArrayList<String> argNames = new ArrayList<>();
	
	public InterpretedMethod(boolean isFinal, EnumProtectionLevel protection, String name) {
		this.isFinal = isFinal;
		this.protection = protection;
		this.name = name;
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
		System.out.println(name);
		for (int i = 0; i < args.length; i++) locals.put(argNames.get(i), args[i]);
		loopLines:
		for (String line : lines) {
			System.out.println("\t" + line);
			if (line.startsWith("return")) {
				System.out.println();
				if (descArgs.endsWith("V")) return VoidReturnMarker.INSTANCE;
				LangObject returnVal = new LangObject();
				String val = line.substring("return".length()).trim();
				val = val.substring(0, val.length() -1);
				for (String localName : locals.keySet()) {
					if (localName.equals(val)) {
						InterpretedObject object = ((InterpretedObject)(locals.get(localName).val));
						Object o = object.obj;
						return o;
					}
				}
				Expression ex = interpreter.parser.parse(val);
				// TODO: expression assignment related stuff
				return ex.get();
			}
			if (line.startsWith("int ")) {
				String s = line.substring("int ".length());
				String[] parts = s.split(" ");
				String name = "";
				StringBuilder ex = new StringBuilder();
				for (int i = 0; i < parts.length; i++) {
					if (i == 0) {
						name = parts[i];
						locals.put(name, new LangObject());
					} else if (i == 1) {
						if (!Objects.equals(parts[i], "=")) throw new RuntimeException(new IllegalArgumentException(name + ":" + line)); // TODO: descriptions of exceptions
					} else {
						ex.append(parts[i]).append(" ");
					}
				}
				ex = new StringBuilder(ex.substring(0, ex.length()-2));
				LangObject obj = locals.get(name);
				obj.val = new InterpretedObject();
				((InterpretedObject)obj.val).isStaticContext = false;
				((InterpretedObject) obj.val).clazz = interpreter.intClass;
				if (!ex.toString().equals("")) {
					Expression expression = interpreter.parser.parse(ex.toString());
					((InterpretedObject) obj.val).obj = expression.get();
				}
				continue;
			}
			for (String localName : locals.keySet()) {
				if (isAssignment(line, localName)) {
					String operator = getAssignmentOperator(line, localName);
					InterpretedObject object = ((InterpretedObject)(locals.get(localName).val));
					Object o = object.obj;
					LangObject workingVar = new LangObject();
					{
						JavaMethodMarker.locals = locals;
						JavaMethodMarker.invoker = invoked;
						JavaMethodMarker.workingVar = workingVar;
					}
					if (o instanceof Number && operator.charAt(0) != '=') {
						object.obj = doOperator(((Number) o).doubleValue(), operator.charAt(0), interpreter.parser.parse(getAssignmentExpression(line, localName)));
					} else if (operator.equals("=")) {
						double val = interpreter.parser.parse(getAssignmentExpression(line, localName)).get();
						object.obj = workingVar.val == null ? val : workingVar.val;
					} else throw new RuntimeException("Cannot use operators on non numbers.");
					if (object.clazz.equals(interpreter.intClass)) object.obj = ((Number)object.obj).intValue();
//					System.out.println(object.obj);
					continue loopLines;
				}
			}
		}
		System.out.println();
		return VoidReturnMarker.INSTANCE;
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
