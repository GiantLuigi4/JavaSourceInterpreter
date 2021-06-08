import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.structure.InterpretedClass;

public class FieldTypesTest {
	public static double d = 0;
	public static int i = 0;
	
	public static double test() {
		d += 64;
		i += 32;
		d += 0.5;
		d += i;
		i += d;
		double d1 = 0;
		d1 += d;
		d1 *= i;
		return d1;
	}
	
	public static void main(String[] args) {
		Interpreter interpreter = new Interpreter();
		interpreter.configuration.verboseMethodExecution = true;
		interpreter.configuration.verboseClassLoading = true;
		interpreter.classFileGetter = (name) -> Interpreter.readFile("src/test/java/", name);
		InterpretedClass clazz = interpreter.getOrLoad("FieldTypesTest");
		double v1 = ((Number) interpreter.run(clazz, "test")).doubleValue();
		double v2 = test();
		System.out.println(v1);
		System.out.println(v2);
		System.out.println("offset: " + (v1 - v2));
	}
}
