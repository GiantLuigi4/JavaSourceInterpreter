package tfc.java_interpreter.data;

import tfc.java_interpreter.JavaMethodMarker;
import tfc.java_interpreter.java_linkage.IWrapperClass;
import tfc.java_interpreter.structure.InterpretedClass;

import java.util.HashMap;

public class InterpretedObject {
	public boolean isStaticContext = false;
	public InterpretedClass clazz;
	public Object obj;
	public IWrapperClass wrapper;
	public HashMap<String, LangObject> fields = new HashMap<>();
	
	// TODO
	public boolean inheritsFrom(InterpretedClass other) {
		return false;
	}
	
	public InterpretedObject copy() {
		InterpretedObject copy = new InterpretedObject();
		copy.clazz = clazz;
		copy.obj = obj;
		copy.wrapper = wrapper;
		copy.isStaticContext = isStaticContext;
		return copy;
	}
	
	public LangObject getField(String name) {
		if (fields.containsKey(name)) return fields.get(name);
		if (!clazz.fields.containsKey(name)) return null;
//		if (clazz.fields.get(name).isStatic) return clazz.getField(name).value;
		LangObject workingVar = new LangObject();
		{
			JavaMethodMarker.locals = null;
			JavaMethodMarker.workingVar = workingVar;
			JavaMethodMarker.invoker = new LangObject();
			JavaMethodMarker.invoker.val = this;
		}
		double val1 = clazz.getField(name).defaultExpression.get();
		InterpretedObject val = new InterpretedObject();
		val.obj = workingVar.val == null ? val1 : workingVar.val;
		if (val.obj instanceof InterpretedObject) val = (InterpretedObject) val.obj;
		val.clazz = clazz.getField(name).type;
		workingVar.val = val;
		fields.put(name, workingVar);
		return fields.get(name);
	}
	
	public HashMap<String, LangObject> getFields() {
		for (String s : clazz.fields.keySet()) {
			if (fields.containsKey(s)) continue;
			getField(s);
		}
		return fields;
	}
}
