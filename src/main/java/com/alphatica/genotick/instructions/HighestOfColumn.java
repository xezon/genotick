package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class HighestOfColumn extends IntIntInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7922049215420858405L;

    @SuppressWarnings("unused")
    public HighestOfColumn() {

    }

    private HighestOfColumn(HighestOfColumn ins) {
        this.setInt1(ins.getInt1());
        this.setInt2(ins.getInt2());
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
