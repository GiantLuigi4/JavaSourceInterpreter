package tfc.java_interpreter;

public class InterpretedObject {
	public boolean isStaticContext = false;
	public InterpretedClass clazz;
	public Object obj;
	
	// TODO
	public boolean inheritsFrom(InterpretedClass other) {
		return false;
	}
}
