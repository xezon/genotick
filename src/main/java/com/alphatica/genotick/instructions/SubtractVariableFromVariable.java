package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class SubtractVariableFromVariable extends IntIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -5692114758846719358L;

    private SubtractVariableFromVariable(SubtractVariableFromVariable i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
    }

    @SuppressWarnings("unused")
    public SubtractVariableFromVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public SubtractVariableFromVariable copy() {
        return new SubtractVariableFromVariable(this);
    }

}
