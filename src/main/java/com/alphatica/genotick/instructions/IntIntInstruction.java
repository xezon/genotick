package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class IntIntInstruction extends Instruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -8461921520321026497L;

    private int int1;
    private int int2;

    public int getInt1() {
        return int1;
    }

    void setInt1(int value) {
        this.int1 = value;
    }

    public int getInt2() {
        return int2;
    }

    void setInt2(int value) {
        this.int2 = value;
    }

    @Override
    public void mutate(Mutator mutator) {
        int1 = mutator.getNextInt();
        int2 = mutator.getNextInt();
    }
}
