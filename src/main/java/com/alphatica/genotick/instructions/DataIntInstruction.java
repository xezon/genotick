package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class DataIntInstruction extends DataInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7780679428775612562L;

    private int int1;

    void setInt(int value) {
        this.int1 = value;
    }

    public int getInt() {
        return int1;
    }
    @Override
    public void mutate(Mutator mutator) {
        super.mutate(mutator);
        int1 = mutateExpInt(mutator, int1);
    }

}
