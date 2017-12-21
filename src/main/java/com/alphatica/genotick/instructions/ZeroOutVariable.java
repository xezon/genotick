package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class ZeroOutVariable extends IntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7513892893024990050L;

    private ZeroOutVariable(ZeroOutVariable i) {
        this.setInt(i.getInt());
    }

    @SuppressWarnings("unused")
    public ZeroOutVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public ZeroOutVariable copy() {
        return new ZeroOutVariable(this);
    }

}
