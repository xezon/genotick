package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class IntInstruction extends Instruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 5052271226112971349L;

    private int int1;

    public int getInt() {
        return int1;
    }

    void setInt(int value) {
        this.int1 = value;
    }

    @Override
    public void mutate(Mutator mutator) {
        int1 = mutateExpInt(mutator, int1);
    }
}
