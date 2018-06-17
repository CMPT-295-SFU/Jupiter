package vsim.riscv.instructions.itype;

import vsim.Globals;


public final class Lbu extends IType {

  public Lbu() {
    super(
      "lbu",
      "lbu rd, offset(rs1)",
      "set rd = memory[rs1 + sext(offset)][7:0]"
    );
    // set opcode
    this.opcode = 0b0000011;
    this.funct3 = 0b100;
  }

  @Override
  protected int compute(int rs1, int imm) {
    return Globals.memory.loadByteUnsigned(rs1 + imm);
  }

}
