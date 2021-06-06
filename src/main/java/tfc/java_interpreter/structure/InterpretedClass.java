package tfc.java_interpreter.structure;

import tfc.java_interpreter.EnumProtectionLevel;

import java.util.HashMap;
import java.util.Objects;

public class InterpretedClass {
	public boolean isFinal;
	public EnumProtectionLevel protection;
	public final String name;
	public HashMap<String, InterpretedMethod> methods = new HashMap<>();
	public HashMap<String, InterpretedField> fields = new HashMap<>();
	
	public InterpretedClass(boolean isFinal, EnumProtectionLevel protection, String name) {
		this.isFinal = isFinal;
		this.protection = protection;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return protection.toString().toLowerCase() +
				(isFinal ? " final" : "") +
				" " + name +
				", methods=" + methods +
				", fields=" + fields;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		InterpretedClass that = (InterpretedClass) object;
		return isFinal == that.isFinal &&
				protection == that.protection &&
				Objects.equals(methods, that.methods) &&
				Objects.equals(fields, that.fields);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(isFinal, protection, methods, fields);
	}
	
	public InterpretedMethod getMethod(String name) {
		return methods.get(name);
	}
	
	public InterpretedMethod getMethod(String name, String desc) {
		for (InterpretedMethod value : methods.values())
			if (value.name.equals(name) && value.descArgs.equals(desc))
				return value;
		return null;
	}
	public InterpretedMethod getMethod(String name, InterpretedClass... args) {
		for (InterpretedMethod value : methods.values())
			if (value.name.equals(name)) {
				// TODO
				return value;
			}
		return null;
	}
	
	public InterpretedField getField(String name) {
		return fields.get(name);
	}
	
	public String getPackage() {
		return name.substring(0, name.lastIndexOf(".") - 1);
	}
}
