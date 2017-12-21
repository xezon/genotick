package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class LowestOfColumn extends IntIntInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -6945803435707758563L;

    @SuppressWarnings("unused")
    public LowestOfColumn() {
    }

    private LowestOfColumn(LowestOfColumn lowestOfColumn) {
        this.setInt1(lowestOfColumn.getInt1());
        this.setInt2(lowestOfColumn.getInt2());
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
