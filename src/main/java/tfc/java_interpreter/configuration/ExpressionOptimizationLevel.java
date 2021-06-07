package tfc.java_interpreter.configuration;

import tfc.expression_solver.Expression;

/**
 * NONE: ((val*3/3*1*1*4/1/6)-(4/6/2))
 * RESOLVE_CONSTANTS: NONE -> ((val*3/3*1*1*4/1/6)-0.3333333333333333)
 * SIMPLIFY: RESOLVE_CONSTANTS -> ((val*0.6666666666666666)-(0.3333333333333333))
 * <p>
 * Resolve Constants finds all constant nested expressions and replaces them with their outputs (4*3)->12, (4*3+val)->(4*3+val)
 * it *should* always return the exact same thing as the source equation would return, but there might be a few edge cases where it doesn't that I don't know of
 * <p>
 * Simplify resolves the constants in an expression, then simplifies the parts which can be simplified (4*3)->12, (4*3+val)->(12+val)
 * Simplify is likely to wind up hitting precision errors and returning the wrong value
 */
public enum ExpressionOptimizationLevel {
	NONE, RESOLVE_CONSTANTS, SIMPLIFY;
	
	ExpressionOptimizationLevel() {
	}
	
	public Expression optimize(Expression src) {
		switch (this) {
			case NONE:
				return src;
			case RESOLVE_CONSTANTS:
				return src.resolveConstants();
			case SIMPLIFY:
				return src.simplify();
			default:
				System.out.println("[ExpressionOptimizationLevel:21] uhh, what?");
				return src;
		}
	}
}
