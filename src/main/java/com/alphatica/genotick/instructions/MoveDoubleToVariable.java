
package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class MoveDoubleToVariable extends IntDoubleInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -1120463586513743256L;

    private MoveDoubleToVariable(MoveDoubleToVariable i) {
        this.setInt(i.getInt());
        this.setDouble(i.getDouble());
    }

    @SuppressWarnings("unused")
    public MoveDoubleToVariable() {
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public MoveDoubleToVariable copy() {
        return new MoveDoubleToVariable(this);
    }

}
