package tfc.java_interpreter.reflections;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;
import tfc.java_interpreter.structure.InterpretedMethod;

public class ReflectionClass extends InterpretedClass {
	public ReflectionClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "tfc.interpreted_lang.Reflections");
	}
	
	private static final ReflectionMethod invoke;
	
	static {
		invoke = new ReflectionMethod(true, EnumProtectionLevel.PRIVATE_FINAL, "invoke");
		invoke.isStatic = true;
	}
	
	@Override
	public InterpretedMethod getMethod(String name) {
		if (name.startsWith("invoke")) return invoke;
		return super.getMethod(name);
	}
}
