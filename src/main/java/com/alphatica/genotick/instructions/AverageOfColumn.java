package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class AverageOfColumn extends IntIntInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -329518949586814597L;

    @SuppressWarnings("unused")
    public AverageOfColumn() {
    }

    private AverageOfColumn(AverageOfColumn ins) {
        this.setInt1(ins.getInt1());
        this.setInt2(ins.getInt2());
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
