package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class LowestOfColumn extends VarVarInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -6945803435707758563L;

    @SuppressWarnings("unused")
    public LowestOfColumn() {
    }

    private LowestOfColumn(LowestOfColumn lowestOfColumn) {
        this.setVariable1Argument(lowestOfColumn.getVariable1Argument());
        this.setVariable2Argument(lowestOfColumn.getVariable2Argument());
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new LowestOfColumn(this);
    }
}
