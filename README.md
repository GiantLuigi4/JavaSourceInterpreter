# JavaSourceInterpreter

A very poorly written java source code interpreter

Currently it only handles math and somewhat handles static methods

I do plan to make it so that this can access regular java classes, as well as extend said classes

This will take a lot of effort and will likely never be finished, but it's the thought that matters, right?

Anyway, 
## Usage

### Gradle
Jitpack, https://jitpack.io/#GiantLuigi4/JavaSourceInterpreter
```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
    ...
}

dependencies {
    ...
    // the interpreter will not work without these. period.
    implementation 'com.github.GiantLuigi4:EquationSolver:6c1d030ead'
    implementation 'com.github.GiantLuigi4:Bytecode-Utils:455c3d13b7'
    
    // these are here so it can access base java classes, as I plan to use runtime class generation for that
    implementation 'org.codehaus.janino:janino:3.1.2'
    implementation 'org.codehaus.janino:commons-compiler:3.1.2'
    implementation 'org.codehaus.janino:commons-compiler-jdk:3.1.2'
    ...
}
```

### Java
```java
		// creates an interpreter instance, so you can interpret your source files
		Interpreter interpreter = new Interpreter();
		// sets up default source file getting from a folder called "classes"
		interpreter.classFileGetter = (name) -> Interpreter.readFile("classes", name);
		// gets or loads the class "TestClass", in this case, from "classes/TestClass.java", as that's what the default file reader will direct it to
		InterpretedClass clazz = interpreter.getOrLoad("TestClass");
		// invokes the method "testMethod" with a classless static context for "invoker" and a static context for the class "TestClass" for "invoked" and prints the result of it to the console
		System.out.println(clazz.getMethod("testMethod").invoke(interpreter.getStaticContext(null), interpreter.getStaticContext(clazz)));
```
