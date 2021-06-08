package tfc.java_interpreter.natives.numbers;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;

public class LongClass extends InterpretedClass {
	public LongClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "long");
	}
}
