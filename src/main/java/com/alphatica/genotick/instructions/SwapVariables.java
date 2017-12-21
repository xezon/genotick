package com.alphatica.genotick.instructions;


import com.alphatica.genotick.processor.Processor;

public class SwapVariables extends IntIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -6328103475159894381L;

    private SwapVariables(SwapVariables i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
    }

    @SuppressWarnings("unused")
    public SwapVariables() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public SwapVariables copy() {
        return new SwapVariables(this);
    }

}
