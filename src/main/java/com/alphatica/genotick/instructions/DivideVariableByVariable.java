package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class DivideVariableByVariable extends IntIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 2684230146996510206L;

    private DivideVariableByVariable(DivideVariableByVariable i) {
        this.setInt2(i.getInt2());
        this.setInt1(i.getInt1());
    }

    @SuppressWarnings("unused")
    public DivideVariableByVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public DivideVariableByVariable copy() {
        return new DivideVariableByVariable(this);
    }

}
