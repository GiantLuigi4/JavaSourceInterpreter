package tfc.java_interpreter;

import tfc.expression_solver.Expression;

public class InterpretedField {
	public LangObject value;
	public Expression defaultExpression;
	public InterpretedClass type;
	public boolean isFinal;
	public boolean isStatic = false;
	public EnumProtectionLevel protection;
	public String name;
	
	public InterpretedField(boolean isFinal, EnumProtectionLevel protection, InterpretedClass type) {
		this.isFinal = isFinal;
		this.protection = protection;
		this.type = type;
	}
	
	public void set(LangObject o) {
		value.val = o.val;
	}
	
	public Object get() {
		if (value == null) {
			value = new LangObject();
			// TODO: setup parser to set value of object
			double r = defaultExpression.get();
			if (value.val == null) value.val = r;
		}
		return value.val;
	}
	
	@Override
	public String toString() {
		return '(' + "name: " + name + ", val: " + get() + ')';
	}
}
