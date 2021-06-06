package tfc.java_interpreter.data;

import tfc.java_interpreter.structure.InterpretedClass;

public class InterpretedObject {
	public boolean isStaticContext = false;
	public InterpretedClass clazz;
	public Object obj;
	
	// TODO
	public boolean inheritsFrom(InterpretedClass other) {
		return false;
	}
}
