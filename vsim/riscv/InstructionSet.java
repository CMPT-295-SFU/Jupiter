package vsim.riscv;

import vsim.utils.Message;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.reflect.Modifier;
import vsim.riscv.instructions.Instruction;


public final class InstructionSet {

  // packages
  private static final String RTYPE = "vsim.riscv.instructions.rtype";
  private static final String ITYPE = "vsim.riscv.instructions.itype";
  private static final String STYPE = "vsim.riscv.instructions.stype";
  private static final String BTYPE = "vsim.riscv.instructions.btype";
  private static final String UTYPE = "vsim.riscv.instructions.utype";
  private static final String JTYPE = "vsim.riscv.instructions.jtype";

  // current classes in rtype package
  private static final String[] RClasses = {
    "Add", "Sub", "Sll",
    "Slt", "Sltu", "Xor",
    "Srl", "Sra", "Or",
    "And", "Div", "Divu",
    "Mul", "Mulh", "Mulhu",
    "Mulhsu", "Rem", "Remu"
  };
  // current classes in itype package
  private static final String[] IClasses = {
    "Jalr", "Lb", "Lh",
    "Lw", "Lbu", "Lhu",
    "Addi", "Slti", "Sltiu",
    "Xori", "Ori", "Andi",
    "Slli", "Srli", "Srai",
    "Fence", "FenceI", "ECall",
    "EBreak"
  };
  // current classes in stype package
  private static final String[] SClasses = {
    "Sb", "Sh", "Sw"
  };
  // current classes in btype package
  private static final String[] BClasses = {
    "Beq", "Bge", "Bgeu",
    "Blt", "Bltu", "Bne"
  };
  // current classes in utype package
  private static final String[] UClasses = {
    "Auipc", "Lui"
  };
  // current classes in jtype package
  private static final String[] JClasses = {
    "Jal"
  };

  private Hashtable<String, Instruction> instructions;

  public InstructionSet() {
    this.instructions = new Hashtable<String, Instruction>();
    this.populate();
  }

  private void add(String[] classes, String pkg) {
    for(String className: classes) {
      String classPath = pkg + "." + className;
      try {
        Class cls = Class.forName(classPath);
        // only final classes
        if (!Instruction.class.isAssignableFrom(cls) ||
          Modifier.isAbstract(cls.getModifiers()) ||
          Modifier.isInterface(cls.getModifiers()))
          continue;
        // add this new instruction to isa
        Instruction inst = (Instruction) cls.newInstance();
        String mnemonic = inst.getMnemonic();
        if (this.instructions.containsKey(mnemonic))
          Message.warning("duplicated instruction name: '" + mnemonic + "', skip this");
        else
          this.instructions.put(mnemonic, inst);
      } catch (Exception e) {
          Message.panic("class: '" + classPath + "' could not be loaded");
      }
    }
  }

  private void populate() {
    this.add(RClasses, RTYPE);
    this.add(IClasses, ITYPE);
    this.add(SClasses, STYPE);
    this.add(BClasses, BTYPE);
    this.add(UClasses, UTYPE);
    this.add(JClasses, JTYPE);
  }

  public boolean validInstruction(String mnemonic) {
    return this.instructions.get(mnemonic) != null;
  }

  public Instruction get(String mnemonic) {
    return this.instructions.get(mnemonic);
  }

  public int size() {
    return this.instructions.size();
  }

  @Override
  public String toString() {
    String out = "";
    String newline = System.getProperty("line.separator");
    for (Enumeration<String> e = this.instructions.keys(); e.hasMoreElements();) {
      String mnemonic = e.nextElement();
      Instruction i = this.instructions.get(mnemonic);
      out += i.toString() + newline;
    }
    return out.trim();
  }

}