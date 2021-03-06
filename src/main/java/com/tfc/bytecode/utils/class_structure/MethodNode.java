package com.tfc.bytecode.utils.class_structure;

import com.tfc.bytecode.utils.Access;
import com.tfc.bytecode.utils.Descriptor;

import java.util.ArrayList;

public class MethodNode {
	public MethodNode(int access, String name, String desc, String signature, String[] exceptions) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
	}
	
	public int maxStack;
	public int maxLocals;
	public String desc;
	public final int access;
	public final String name;
	public ArrayList<GenericInsnNode> instructions = new ArrayList<>();
	public final String signature;
	public final String[] exceptions;
	
	public MethodNode(MethodNodeSource source) {
		this.access = Access.parseAccess(source.code.substring(0, source.code.indexOf(" " + source.getType())));
		this.name = source.getName();
		String desc = Descriptor.getDescriptorFor(source.getType(), true);
		this.desc = createDesc(desc, source.code.substring(source.code.indexOf(source.getName() + "(") + (source.getName().length())));
		this.signature = null;
		this.exceptions = null;
//		for (String line : source.code.substring(source.code.indexOf("{") + 1).split(";")) {
//			if (line.trim().startsWith("return") && line.contains("+")) {
//				addInstruction(new GenericInsnNode("VarInsn", new Object[]{Opcodes.ILOAD, 1}));
//				addInstruction(new GenericInsnNode("VarInsn", new Object[]{Opcodes.ILOAD, 2}));
//				addInstruction(new GenericInsnNode("Insn", new Object[]{Opcodes.IADD}));
//				addInstruction(new GenericInsnNode("Insn", new Object[]{Opcodes.IRETURN}));
//			} else if (line.trim().startsWith("return")) {
//				addInstruction(new GenericInsnNode(GenericInsnNode.InsnType.INSN, new Object[]{Opcodes.IRETURN}));
//			}
//		}
	}
	
	private String createDesc(String desc, String args) {
		args = args.substring(args.indexOf("(") + 1);
		args = args.substring(0, args.indexOf("{"));
		StringBuilder argsBuilder = new StringBuilder("(");
		if (args.contains(",")) {
			for (String s : args.split(",")) {
				s = s.trim();
				s = (s.substring(0, s.indexOf(" ")));
				argsBuilder.append(Descriptor.getDescriptorFor(s, true));
			}
		}
		argsBuilder.append(")");
		return argsBuilder.toString() + desc;
	}
	
	public void addInstruction(GenericInsnNode node) {
		instructions.add(node);
	}
	
	//TODO
	public Object toInsnList() {
		return null;
	}
}
