package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class IntInstruction extends Instruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 5052271226112971349L;

    private int value;

    public int getInt() {
        return value;
    }

    void setInt(int value) {
        this.value = value;
    }

    @Override
    public void mutate(Mutator mutator) {
        value = mutateExpInt(mutator, value);
    }
}
