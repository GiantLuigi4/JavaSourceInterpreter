package tfc.java_interpreter.reflections;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.InterpretedMethod;

public class ReflectionMethod extends InterpretedMethod {
	public ReflectionMethod(boolean isFinal, EnumProtectionLevel protection, String name) {
		super(isFinal, protection, name);
	}
}
