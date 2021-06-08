import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.structure.InterpretedClass;

public class TestClassA {
//	public static int test() {
//		int v = 5*(5+5+5*(5/2
//		return v
//	}
	
	public static void main(String[] args) {
		String s = "public class TestClassA {\n" +
				"\tpublic static int test() {\n" +
				"\t\tint v = 5*(5+5+5*(5/2;\n" + // two nested equations are yeeted, because reasons?
				"\t\treturn v;\n" +
				"\t}\n" +
				"}\n";
		Interpreter interpreter = new Interpreter();
		interpreter.configuration.verboseClassLoading = true;
		interpreter.configuration.verboseMethodExecution = true;
		InterpretedClass clazz = interpreter.read(s);
		double val = (double) clazz.getMethod("test").invoke(interpreter.getStaticContext(null), interpreter.getStaticContext(clazz));
		System.out.println(val);
	}
}
