import tfc.java_interpreter.structure.InterpretedClass;
import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.data.LangObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		Interpreter interpreter = new Interpreter();
		byte[] bytes;
		{
			File file = new File("src/test/java/TestClass.java");
			FileInputStream stream = new FileInputStream(file);
			bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();
		}
		InterpretedClass clazz = interpreter.read(new String(bytes));
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
		long start = System.nanoTime();
		System.out.println(clazz.getMethod("testMethod").invoke(o, o1));
		System.out.println(Math.abs(start - System.nanoTime()));
		start = System.nanoTime();
		TestClass.main(args);
		System.out.println(Math.abs(start - System.nanoTime()));
	}
}
