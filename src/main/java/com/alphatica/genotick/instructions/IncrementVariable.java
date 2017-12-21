package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class IncrementVariable extends IntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7224362880966273739L;

    private IncrementVariable(IncrementVariable i) {
        this.setInt(i.getInt());
    }

    @SuppressWarnings("unused")
    public IncrementVariable() {
    }

    @Override
    public void executeOn(Processor processor)  {
        processor.execute(this);
    }

    @Override
    public IncrementVariable copy() {
        return new IncrementVariable(this);
    }

}
