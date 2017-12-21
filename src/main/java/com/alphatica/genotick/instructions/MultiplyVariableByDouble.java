package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class MultiplyVariableByDouble extends IntDoubleInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -488671617233131162L;

    private MultiplyVariableByDouble(MultiplyVariableByDouble i) {
        this.setDouble(i.getDouble());
        this.setInt(i.getInt());
    }

    @SuppressWarnings("unused")
    public MultiplyVariableByDouble() {
    }

    @Override
    public void executeOn(Processor processor)  {
        processor.execute(this);
    }

    @Override
    public MultiplyVariableByDouble copy() {
        return new MultiplyVariableByDouble(this);
    }

}
