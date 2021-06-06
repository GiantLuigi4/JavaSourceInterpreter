package tfc.java_interpreter.natives;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;

public class IntegerClass extends InterpretedClass {
	public IntegerClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "int");
	}
}
