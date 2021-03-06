/*
Copyright (C) 2018-2019 Andres Castellanos

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>
*/

package jupiter.riscv.instructions.btype;

import jupiter.exc.SimulationException;
import jupiter.riscv.instructions.Format;
import jupiter.riscv.instructions.Instruction;
import jupiter.riscv.instructions.InstructionField;
import jupiter.riscv.instructions.MachineCode;
import jupiter.sim.State;
import jupiter.utils.Data;


/** RISC-V bge (Branch if Greater Than or Equal) instruction. */
public final class BGE extends Instruction {

  /** Creates a new bge instruction. */
  public BGE() {
    super(Format.B, "bge", 0b1100011, 0b101, 0b0000000);
  }

  /** {@inheritDoc} */
  @Override
  public void execute(MachineCode code, State state) throws SimulationException {
    int rs1 = state.xregfile().getRegister(code.get(InstructionField.RS1));
    int rs2 = state.xregfile().getRegister(code.get(InstructionField.RS2));
    if (rs1 >= rs2) {
      int pc = state.xregfile().getProgramCounter();
      state.xregfile().setProgramCounter(pc + Data.imm(code, Format.B));
    } else {
      state.xregfile().incProgramCounter();
    }
  }

  /** {@inheritDoc} */
  @Override
  public String disassemble(MachineCode code) {
    int rs1 = code.get(InstructionField.RS1);
    int rs2 = code.get(InstructionField.RS2);
    int imm = Data.imm(code, Format.B);
    return String.format("bge x%d, x%d, %d", rs1, rs2, imm);
  }

}
