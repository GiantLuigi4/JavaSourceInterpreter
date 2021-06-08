package tfc.java_interpreter.natives.numbers;

import tfc.java_interpreter.EnumProtectionLevel;
import tfc.java_interpreter.structure.InterpretedClass;

public class ByteClass extends InterpretedClass {
	public ByteClass() {
		super(true, EnumProtectionLevel.PUBLIC_FINAL, "byte");
	}
}
