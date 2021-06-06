package tfc.java_interpreter.data;

import tfc.java_interpreter.java_linkage.IWrapperClass;
import tfc.java_interpreter.structure.InterpretedClass;

public class InterpretedObject {
	public boolean isStaticContext = false;
	public InterpretedClass clazz;
	public Object obj;
	public IWrapperClass wrapper;
	
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
}
