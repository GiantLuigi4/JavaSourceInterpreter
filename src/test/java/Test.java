import tfc.java_interpreter.structure.InterpretedClass;
import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.data.LangObject;

public class Test {
	public static void main(String[] args) {
		Interpreter interpreter = new Interpreter();
		InterpretedClass clazz = interpreter.read("" +
				"public class TestClass {\n" +
				"\tpublic final int num = 5 * 4 * 3 * 2 * 1;\n" +
				"\tpublic final int num1 = 6 * 5 * 4 * 3 * 2 * 1;\n" +
				"\t\n" +
				"\tpublic int testMethod() {\n" +
				"\t\tint val1 = 0;\n" +
				"\t\tint val = 1;\n" +
				"\t\tval1 += 5;\n" +
				"\t\tval *= 5;\n" +
				"\t\tval /= 2;\n" +
				"\t\tval1 /= 293 -291;\n" +
				"\t\tval += 3;\n" +
				"\t\tval1 *= 5;\n" +
				"\t\tval -= 530;\n" +
				"\t\tval1 *= 68;\n" +
				"\t\treturn val;\n" +
				"\t}\n" +
				"}\n" +
				"");
		System.out.println(clazz);
		LangObject o = new LangObject();
		InterpretedObject interpO = new InterpretedObject();
		interpO.isStaticContext = true;
		o.val = interpO;
		LangObject o1 = new LangObject();
		InterpretedObject interp1 = new InterpretedObject();
		interp1.clazz = clazz;
		interp1.isStaticContext = true;
		o1.val = interp1;
		System.out.println(clazz.getMethod("testMethod").invoke(o, o1));
	}
}
