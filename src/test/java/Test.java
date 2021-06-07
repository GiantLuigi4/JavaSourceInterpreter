import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.structure.InterpretedClass;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		Interpreter interpreter = new Interpreter();
		interpreter.configuration.verboseMethodExecution = true;
		interpreter.classFileGetter = (name) -> Interpreter.readFile("src/test/java/", name);
//		byte[] bytes;
//		{
//			File file = new File("src/test/java/TestClass.java");
//			FileInputStream stream = new FileInputStream(file);
//			bytes = new byte[stream.available()];
//			stream.read(bytes);
//			stream.close();
//		}
//		InterpretedClass clazz = interpreter.read(new String(bytes));
		InterpretedClass clazz = interpreter.getOrLoad("TestClass");
		System.out.println();
		System.out.println(clazz);
		long start = System.nanoTime();
		System.out.println(clazz.getMethod("testMethod").invoke(interpreter.getStaticContext(null), interpreter.getStaticContext(clazz)));
		System.out.println(Math.abs(start - System.nanoTime()));
		start = System.nanoTime();
		TestClass.main(args);
		System.out.println(Math.abs(start - System.nanoTime()));

//		LangObject obj = interpreter.createInstance(clazz);
//		System.out.println(((InterpretedObject) (((InterpretedObject) obj.val).getField("test").val)).obj);
//		System.out.println(((InterpretedObject) (clazz.getMethod("add").invoke(interpreter.getStaticContext(null), obj, interpreter.box(4)))).obj);
//		System.out.println(((InterpretedObject) (clazz.getMethod("add").invoke(interpreter.getStaticContext(null), obj, interpreter.box(-6)))).obj);
	}
}
