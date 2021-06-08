package tfc.java_interpreter.natives.numbers;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;

public class ShortClass extends InterpretedClass {
	public ShortClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "short");
	}
}
