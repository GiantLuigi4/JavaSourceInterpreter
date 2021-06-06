package tfc.java_interpreter;

public enum EnumProtectionLevel {
	PUBLIC(0),
	PROTECTED(1),
	PRIVATE(2),
	PUBLIC_FINAL(3),
	PROTECTED_FINAL(4),
	PRIVATE_FINAL(5),
	;
	
	byte level;
	
	EnumProtectionLevel(int level) {
		this.level = (byte) level;
	}
	
	public static EnumProtectionLevel get(String access) {
		int aLevel = 1;
		if (access.contains("public")) aLevel = 0;
		else if (access.contains("private")) aLevel = 2;
		if (access.contains("final")) aLevel += 3;
		for (EnumProtectionLevel value : values()) if (value.level == aLevel) return value;
		return null;
	}
	
	public boolean isPublic() {
		return level == 0 || level == 3;
	}
	
	public boolean isProtected() {
		return level == 1 || level == 4;
	}
	
	public boolean isPrivate() {
		return level == 2 || level == 5;
	}
	
	public boolean isFinal() {
		return level >= 3;
	}
}
