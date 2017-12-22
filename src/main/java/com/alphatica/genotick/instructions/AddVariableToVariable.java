package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class AddVariableToVariable extends IntIntInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 232466498704321646L;

    private AddVariableToVariable(AddVariableToVariable i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
    }

    @SuppressWarnings("unused")
    public AddVariableToVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public AddVariableToVariable copy() {
        return new AddVariableToVariable(this);
    }

}
