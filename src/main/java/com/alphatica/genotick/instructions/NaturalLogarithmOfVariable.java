package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class NaturalLogarithmOfVariable extends IntIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -3112125542251877233L;

    private NaturalLogarithmOfVariable(NaturalLogarithmOfVariable i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
    }

    @SuppressWarnings("unused")
    public NaturalLogarithmOfVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public NaturalLogarithmOfVariable copy() {
        return new NaturalLogarithmOfVariable(this);
    }
}
