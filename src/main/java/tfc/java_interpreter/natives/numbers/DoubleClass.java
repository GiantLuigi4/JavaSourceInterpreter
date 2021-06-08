package tfc.java_interpreter.natives.numbers;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;

public class DoubleClass extends InterpretedClass {
	public DoubleClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "double");
	}
}
