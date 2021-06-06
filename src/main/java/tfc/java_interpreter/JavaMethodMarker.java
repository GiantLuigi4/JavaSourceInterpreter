package tfc.java_interpreter;

import tfc.expression_solver.MethodMarker;
import tfc.expression_solver.values.Value;
import tfc.java_interpreter.data.LangObject;

import java.util.HashMap;

public class JavaMethodMarker extends MethodMarker {
	public static HashMap<String, LangObject> locals = new HashMap<>();
	public static LangObject workingVar;
	public static LangObject invoker;
	
	@Override
	public boolean matches(String str) {
		return true;
	}
	
	@Override
	public Value generate(String str) {
		return new JavaExpressionMethod(str).setLocals(locals).setWorkingVar(workingVar).setInvoker(invoker);
	}
}
