package tfc.java_interpreter;

import tfc.expression_solver.ExpressionParser;
import tfc.expression_solver.values.Value;
import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.data.LangObject;
import tfc.java_interpreter.natives.NumberClasses;
import tfc.java_interpreter.structure.InterpretedClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JavaExpressionMethod extends Value {
	public ArrayList<String> methodsCalled = new ArrayList<>();
	public String str;
	
	public JavaExpressionMethod(String str) {
		this.str = str;
//		System.out.println(str);
		StringBuilder methodCall = new StringBuilder();
		int unclosedParens = 0;
		boolean inString = false;
		boolean isEscaped = false;
		for (char c : str.toCharArray()) {
			methodCall.append(c);
			if (!isEscaped) {
				if (!inString) {
					if (c == '(') unclosedParens++;
					if (c == ')') unclosedParens--;
					if (c == '\\') isEscaped = true;
				}
				if (c == '\"') inString = !inString;
			} else {
				isEscaped = false;
			}
			if (unclosedParens == 0 && c == ')') {
				// TODO: make this work for methods which use "."s in strings
				methodsCalled.addAll(Arrays.asList(methodCall.toString().split("\\.")));
				methodCall = new StringBuilder();
			}
		}
		if (methodCall.length() == 0) return;
		methodsCalled.addAll(Arrays.asList(methodCall.toString().split("\\.")));
	}
	
	public JavaExpressionMethod(ArrayList<String> methodsCalled) {
		StringBuilder str = new StringBuilder();
		for (String s : methodsCalled) str.append(s);
		this.str = str.toString();
		this.methodsCalled = methodsCalled;
	}
	
	@Override
	public double get(ExpressionParser parser) {
		locals = locals == null ? new HashMap<>() : new HashMap<>(locals);
		{
			LangObject nullO = new LangObject();
			InterpretedObject obj = new InterpretedObject();
			obj.clazz = null;
			obj.fields = null;
			obj.obj = null;
			obj.isStaticContext = false;
			obj.wrapper = null;
			nullO.val = obj;
			locals.put("null", nullO);
		}
		workingVar.val = invoker.val;
		for (String s : methodsCalled) {
			if (!s.contains("(")) {
				if (locals != null && locals.containsKey(s)) {
					if (((InterpretedObject) locals.get(s).val).obj instanceof Number)
						workingVar.val = ((InterpretedObject) locals.get(s).val).obj;
					else workingVar.val = locals.get(s).val;
				} else {
//					System.out.println(workingVar);
//					System.out.println(workingVar.val);
//					System.out.println(((InterpretedObject) (workingVar.val)).getField(s));
//					System.out.println(((InterpretedObject) (workingVar.val)).getField(s).val);
//					System.out.println(((InterpretedObject) (((InterpretedObject) (workingVar.val)).getField(s).val)).obj);
					if (((InterpretedObject) workingVar.val).getField(s) != null)
						workingVar.val = ((InterpretedObject) workingVar.val).getField(s).val;
					if (workingVar.val instanceof LangObject) workingVar.val = ((LangObject) workingVar.val).val;
//					System.out.println(((InterpretedObject)workingVar.val).obj);
//					System.out.println(((InterpretedObject)workingVar.val).clazz);
//					System.out.println(((InterpretedObject)workingVar.val).clazz == null);
//					System.out.println(((InterpretedObject) workingVar.val).obj instanceof Number);
//					System.out.println(((InterpretedObject)workingVar.val).clazz.interpreter);
//					System.out.println(((InterpretedObject)workingVar.val).clazz.interpreter.intClass);
//					System.out.println(((InterpretedObject)workingVar.val).clazz == ((InterpretedObject) workingVar.val).clazz.interpreter.intClass);
//					if (
////							(((InterpretedObject)workingVar.val).clazz == null && ((InterpretedObject) workingVar.val).obj instanceof Number) ||
//							((InterpretedObject) workingVar.val).obj instanceof Number
//					) {
					Object o = ((InterpretedObject) workingVar.val).obj;
					Interpreter interpreter = ((InterpretedObject) workingVar.val).clazz.interpreter;
					HashMap<String, LangObject> fields = ((InterpretedObject) workingVar.val).fields;
					if (o instanceof Number) {
						Number n = (Number) ((InterpretedObject) workingVar.val).obj;
						workingVar.val = interpreter.createInstance(interpreter.getOrLoad(NumberClasses.getClassName(n))).val;
						((InterpretedObject) workingVar.val).obj = n;
					} else {
						workingVar.val = interpreter.createInstance(interpreter.getOrLoad(o.getClass().getName().replace("/", "."))).val;
						((InterpretedObject) workingVar.val).obj = o;
					}
					((InterpretedObject) workingVar.val).fields = fields;
//					}
				}
			} else {
				if (s.contains(".")) {
					// TODO: whatever needs to be done here
					if (!s.startsWith(".")) {
					}
				} else {
					ArrayList<LangObject> args = new ArrayList<>();
					ArrayList<InterpretedClass> argsClasses = new ArrayList<>();
					String argsStr = s.substring(s.indexOf("(") + 1, s.length() - 1);
					// TODO: make this work properly with strings that have "," as args
					for (String arg : argsStr.split(",")) {
						if (locals != null && locals.containsKey(arg)) {
							LangObject obj = locals.get(arg);
							if (obj.val instanceof InterpretedObject) {
								LangObject object = new LangObject();
								object.val = ((InterpretedObject) obj.val).copy();
								args.add(object);
							} else args.add(obj);
							argsClasses.add(((InterpretedObject) (locals.get(arg).val)).clazz);
						} // TODO: static and instance fields
						// TODO: expressions as args
					}
					LangObject[] argsArray = args.toArray(new LangObject[0]);
					InterpretedClass[] argsClassesArray = argsClasses.toArray(new InterpretedClass[0]);
					Object o = ((InterpretedObject) invoker.val).clazz.getMethod(
							s.substring(0, s.indexOf("(")), argsClassesArray
					).checkAndInvoke(invoker, workingVar, argsArray);
					if (o instanceof LangObject) workingVar.val = ((LangObject) o).val;
					else workingVar.val = o;
//					System.out.println(workingVar.val);
				}
			}
		}
		double out = Interpreter.unbox(workingVar) instanceof Number ? ((Number) Interpreter.unbox(workingVar)).doubleValue() : Double.NaN;
		if (workingVar.val instanceof Number) workingVar.val = null;
		return out;
	}
	
	@Override
	public String toString(ExpressionParser parser) {
		return str;
	}
	
	@Override
	public String toString() {
		return str;
	}
	
	public HashMap<String, LangObject> locals = new HashMap<>();
	public LangObject workingVar;
	public LangObject invoker;
	
	public JavaExpressionMethod setWorkingVar(LangObject o) {
		this.workingVar = o;
		return this;
	}
	
	public JavaExpressionMethod setLocals(HashMap<String, LangObject> locals) {
		this.locals = locals;
		return this;
	}
	
	public JavaExpressionMethod setInvoker(LangObject invoker) {
		this.invoker = invoker;
		return this;
	}
}
