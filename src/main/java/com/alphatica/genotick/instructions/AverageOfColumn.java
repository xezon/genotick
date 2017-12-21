package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class AverageOfColumn extends VarVarInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -329518949586814597L;

    @SuppressWarnings("unused")
    public AverageOfColumn() {
    }

    private AverageOfColumn(AverageOfColumn ins) {
        this.setVariable1Argument(ins.getVariable1Argument());
        this.setVariable2Argument(ins.getVariable2Argument());
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new AverageOfColumn(this);
    }
}
