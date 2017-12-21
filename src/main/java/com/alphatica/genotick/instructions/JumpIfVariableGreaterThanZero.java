package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class JumpIfVariableGreaterThanZero extends IntJumpInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -9053565445759017543L;

    private JumpIfVariableGreaterThanZero(JumpIfVariableGreaterThanZero i) {
        this.setInt(i.getInt());
        this.setAddress(i.getAddress());
    }

    @SuppressWarnings("unused")
    public JumpIfVariableGreaterThanZero() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public JumpIfVariableGreaterThanZero copy() {
        return new JumpIfVariableGreaterThanZero(this);
    }
}
