package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class HighestOfColumn extends VarVarInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7922049215420858405L;

    @SuppressWarnings("unused")
    public HighestOfColumn() {

    }

    private HighestOfColumn(HighestOfColumn ins) {
        this.setVariable1Argument(ins.getVariable1Argument());
        this.setVariable2Argument(ins.getVariable2Argument());
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new HighestOfColumn(this);
    }
}
