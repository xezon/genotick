package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class SumOfColumn extends VarVarInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -4448791341243829694L;

    @SuppressWarnings("unused")
    public SumOfColumn() {
    }

    private SumOfColumn(SumOfColumn ins) {
        this.setVariable1Argument(ins.getVariable1Argument());
        this.setVariable2Argument(ins.getVariable2Argument());
    }
    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new SumOfColumn(this);
    }
}
