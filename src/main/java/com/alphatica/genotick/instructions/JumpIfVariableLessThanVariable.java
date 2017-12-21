package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class JumpIfVariableLessThanVariable extends IntIntJumpInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -849210590326352304L;

    private JumpIfVariableLessThanVariable(JumpIfVariableLessThanVariable i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
        this.setAddress(i.getAddress());
    }

    @SuppressWarnings("unused")
    public JumpIfVariableLessThanVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public JumpIfVariableLessThanVariable copy() {
        return new JumpIfVariableLessThanVariable(this);
    }
}
