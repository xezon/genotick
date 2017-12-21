package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class ReturnVariableAsResult extends IntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -1366004911226575165L;

    private ReturnVariableAsResult(ReturnVariableAsResult i) {
        this.setInt(i.getInt());
    }

    @SuppressWarnings("unused")
    public ReturnVariableAsResult() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public ReturnVariableAsResult copy() {
        return new ReturnVariableAsResult(this);
    }

}
