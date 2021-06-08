package tfc.java_interpreter.natives;

import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.natives.numbers.*;
import tfc.java_interpreter.structure.InterpretedClass;

public class NumberClasses {
	public static Number cast(InterpretedObject o, InterpretedClass targ) {
		if (targ instanceof IntegerClass) return getAsInt(o);
		else if (targ instanceof DoubleClass) return getAsDouble(o);
		else if (targ instanceof FloatClass) return getAsFloat(o);
		else if (targ instanceof LongClass) return getAsLong(o);
		else if (targ instanceof ShortClass) return getAsShort(o);
		else if (targ instanceof ByteClass) return getAsByte(o);
		else if (targ instanceof BooleanClass) return ((getAsDouble(o)) != 0 ? 1 : 0);
		return null;
	}
	
	public static String getClassName(Number n) {
		if (n instanceof Long) return "long";
		if (n instanceof Short) return "short";
		if (n instanceof Byte) return "byte";
		if (n instanceof Double) return "double";
		if (n instanceof Float) return "float";
		if (n instanceof Integer) return "int";
		return null;
	}
	
	public static int getAsInt(InterpretedObject o) {
		if (o.obj instanceof Number) return ((Number) o.obj).intValue();
		return -0;
	}
	
	public static double getAsDouble(InterpretedObject o) {
		if (o.obj instanceof Number) return ((Number) o.obj).doubleValue();
		return -0;
	}
	
	public static float getAsFloat(InterpretedObject o) {
		if (o.obj instanceof Number) return ((Number) o.obj).floatValue();
		return -0;
	}
	
	public static float getAsLong(InterpretedObject o) {
		if (o.obj instanceof Number) return ((Number) o.obj).longValue();
		return -0;
	}
	
	public static short getAsShort(InterpretedObject o) {
		if (o.obj instanceof Number) return ((Number) o.obj).shortValue();
		return -0;
	}
	
	public static byte getAsByte(InterpretedObject o) {
		if (o.obj instanceof Number) return ((Number) o.obj).byteValue();
		return -0;
	}
}
