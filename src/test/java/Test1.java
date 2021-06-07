import tfc.java_interpreter.Interpreter;
import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.data.LangObject;
import tfc.java_interpreter.structure.InterpretedClass;

public class Test1 {
	public static Test2 test = null;
	public static Test2 test1 = null;
	
	public static int doThing() {
		test.num += 1;
		test.num += 1;
		test1.num += 1;
		test.num += 1;
		test1.num *= 2;
		test.num += 1;
		test1.num *= test.num;
		test1.num += 0.5;
		return test1.num;
	}
	
	private static final int i1 = 1;
	
	public static int codeBlocks() {
		int i = 5;
		{
			int i1 = 5;
			i += i1;
		}
		{
			int i1 = 4;
			i += i1;
		}
		{
			int i1 = 4;
			{
				int i2 = 5;
				int i3 = i2 + 5;
				i3 += 1;
				i = i1 + i;
				i += i3;
				i += i3;
			}
		}
		i += i1;
		return i;
	}
	
	public static void main(String[] args) {
		{
			Interpreter interpreter = new Interpreter();
			interpreter.configuration.verboseMethodExecution = true;
			interpreter.configuration.verboseClassLoading = true;
			interpreter.classFileGetter = (name) -> Interpreter.readFile("src/test/java/", name);
			InterpretedClass test1 = interpreter.getOrLoad("Test1");
			InterpretedClass test2 = interpreter.getOrLoad("Test2");
			
			LangObject obj = interpreter.createInstance(test1);
			InterpretedObject iobj = ((InterpretedObject) obj.val);
			LangObject obj1 = iobj.getField("test");
			LangObject obj2 = iobj.getField("test1");
			obj1.val = interpreter.createInstance(test2);
			obj2.val = interpreter.createInstance(test2);
			
			System.out.println((iobj.clazz.getMethod("doThing").invoke(interpreter.getStaticContext(null), interpreter.getStaticContext(test1))));
			System.out.println((iobj.clazz.getMethod("codeBlocks").invoke(interpreter.getStaticContext(null), interpreter.getStaticContext(test1))));
		}
		test = new Test2();
		test1 = new Test2();
		System.out.println(codeBlocks());
		System.out.println(doThing());
	}
}
