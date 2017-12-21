package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class SqRootOfVariable extends IntIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -6025395017177611705L;

    private SqRootOfVariable(SqRootOfVariable i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
    }

    @SuppressWarnings("unused")
    public SqRootOfVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public SqRootOfVariable copy() {
        return new SqRootOfVariable(this);
    }
}
