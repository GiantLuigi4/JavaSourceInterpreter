import tfc.java_interpreter.data.InterpretedObject;
import tfc.java_interpreter.data.LangObject;
import tfc.java_interpreter.data.VoidReturnMarker;
import tfc.java_interpreter.java_linkage.IWrapperClass;
import tfc.java_interpreter.structure.InterpretedMethod;

public class InvokerTestWrapper extends InvokerTest implements IWrapperClass {
	private final LangObject wrapped;
	
	public InvokerTestWrapper(LangObject wrapped) {
		this.wrapped = wrapped;
	}
	
	LangObject caller = null;
	
	@Override
	public void setCaller(LangObject obj) {
		caller = obj;
	}
	
	@Override
	public LangObject getCaller() {
		return caller;
	}
	
	@Override
	public void yes1() {
		if (wrapped == null) super.yes1();
		else {
			InterpretedMethod method = ((InterpretedObject)wrapped.val).clazz.getMethod("yes1", "()V");
			if (method == null) super.yes1();
			else {
				method.invoke(caller, wrapped);
			}
		}
	}
	
	public static java.lang.Object invoke(String param0, LangObject param1, LangObject param2, LangObject[] param3) {
		if (param0.equals("yes1")) {
			InterpretedObject object = ((InterpretedObject) param2.val);
			LangObject caller = object.wrapper.getCaller();
			object.wrapper.setCaller(param1);
			((InvokerTestWrapper) object.wrapper).yes1();
			object.wrapper.setCaller(caller);
			return VoidReturnMarker.INSTANCE;
		} else if (param0.equals("yes")) {
			InvokerTest.yes();
			return VoidReturnMarker.INSTANCE;
		}
		return VoidReturnMarker.INSTANCE;
	}
}