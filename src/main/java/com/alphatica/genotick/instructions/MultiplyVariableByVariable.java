package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class MultiplyVariableByVariable extends IntIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -2530246252784080647L;

    private MultiplyVariableByVariable(MultiplyVariableByVariable i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
    }

    @SuppressWarnings("unused")
    public MultiplyVariableByVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public MultiplyVariableByVariable copy() {
        return new MultiplyVariableByVariable(this);
    }

}
