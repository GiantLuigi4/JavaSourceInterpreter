package tfc.java_interpreter.java_linkage;

import tfc.java_interpreter.data.LangObject;

public interface IWrapperClass {
	void setCaller(LangObject obj);
	LangObject getCaller();
}
