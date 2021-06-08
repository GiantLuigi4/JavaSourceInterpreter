package tfc.java_interpreter.natives.numbers;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;

public class BooleanClass extends InterpretedClass {
	public BooleanClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "boolean");
	}
}
