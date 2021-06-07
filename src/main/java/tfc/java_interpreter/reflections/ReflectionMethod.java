package tfc.java_interpreter.reflections;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;
import tfc.java_interpreter.structure.InterpretedMethod;

public class ReflectionMethod extends InterpretedMethod {
	public ReflectionMethod(boolean isFinal, EnumProtectionLevel protection, String name, InterpretedClass owner) {
		super(isFinal, protection, name, owner);
	}
}
