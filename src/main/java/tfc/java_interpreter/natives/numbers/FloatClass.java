package tfc.java_interpreter.natives.numbers;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;

public class FloatClass extends InterpretedClass {
	public FloatClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "float");
	}
}
