package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class IntDoubleInstruction extends IntInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 5798418310767182684L;

    private double double1;

    void setDouble(double value) {
        this.double1 = value;
    }

    public double getDouble() {
        return double1;
    }

    @Override
    public void mutate(Mutator mutator) {
        super.mutate(mutator);
        if(double1 == 0) {
            double1 = Tools.mutateDouble(1_000_000 * mutator.getNextDouble(), mutator);
        }
        double1 = Tools.mutateDouble(double1, mutator);
     }
}

