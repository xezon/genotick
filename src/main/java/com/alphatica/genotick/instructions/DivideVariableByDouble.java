package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class DivideVariableByDouble extends IntDoubleInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 2277032167143213475L;

    private DivideVariableByDouble(DivideVariableByDouble i) {
        this.setDouble(i.getDouble());
        this.setInt(i.getInt());
    }

    @SuppressWarnings("unused")
    public DivideVariableByDouble() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public DivideVariableByDouble copy() {
        return new DivideVariableByDouble(this);
    }

}
