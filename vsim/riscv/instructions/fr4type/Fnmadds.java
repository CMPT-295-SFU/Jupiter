package vsim.riscv.instructions.fr4type;


public final class Fnmadds extends FR4Type {

  public Fnmadds() {
    super(
      "fnmadd.s",
      "fnmadd.s frd, frs1, frs2, frs3",
      "set frd = -frs1 * frs2 - frs3"
    );
  }

  @Override
  public float compute(float rs1, float rs2, float rs3) {
    return -((rs1 * rs2) + rs3);
  }

}