package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class MoveVariableToVariable extends IntIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -8962949754876920077L;

    private MoveVariableToVariable(MoveVariableToVariable i) {
        this.setInt1(i.getInt1());
        this.setInt2(i.getInt2());
    }

    @SuppressWarnings("unused")
    public MoveVariableToVariable() {
    }

    @Override
    public void executeOn(Processor processor)  {
        processor.execute(this);
    }

    @Override
    public MoveVariableToVariable copy() {
        return new MoveVariableToVariable(this);
    }

}
