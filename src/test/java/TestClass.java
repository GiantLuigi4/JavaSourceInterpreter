public class TestClass {
	public static final int num = 5 * 4 * 3 * 2 * 1;
	public static final int num1 = 6 * 5 * 4 * 3 * 2 * 1;
	
	public static void main(String[] args) {
		System.out.println(testMethod());
	}
	
	public static int testMethod() {
		int val1 = 0;
		int val = 1;
		val1 += 5;
		val *= 5;
		val /= 2;
		val1 /= 293 - 291;
		val += 3;
		val1 *= 5;
		val -= 530;
		val1 *= 68;
		int val2 = val + val1;
		val1 = testMethod1(val2);
		val = testMethod1(val2) + testMethod1(val2);
		return val;
	}
	
	public static int testMethod1(int val) {
		val += 5;
		val *= 6;
		val *= 42;
		val /= 5;
		val += 6;
		val %= 8;
		val += 4;
		val -= 5;
		val += 64;
		return val;
	}
}
