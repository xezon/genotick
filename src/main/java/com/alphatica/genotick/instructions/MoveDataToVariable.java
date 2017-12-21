package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

public class MoveDataToVariable extends DataIntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3017704625520415010L;

    private MoveDataToVariable(MoveDataToVariable i) {
        this.setDataTableIndex(i.getDataColumnIndex());
        this.setDataOffsetIndex(i.getDataOffsetIndex());
        this.setInt(i.getInt());
    }

    @SuppressWarnings("unused")
    public MoveDataToVariable() {
    }

    @Override
    public void executeOn(Processor processor)  {
        processor.execute(this);
    }

    @Override
    public MoveDataToVariable copy() {
        return new MoveDataToVariable(this);
    }

}
