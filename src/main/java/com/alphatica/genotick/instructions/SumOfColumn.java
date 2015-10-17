package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

import java.io.Serializable;

public class SumOfColumn extends RegRegInstruction implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -4448791341243829694L;

    @SuppressWarnings("unused")
    public SumOfColumn() {
    }

    private SumOfColumn(SumOfColumn ins) {
        this.setRegister1(ins.getRegister1());
        this.setRegister2(ins.getRegister2());
    }
    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new SumOfColumn(this);
    }
}
