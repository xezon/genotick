package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class IntJumpInstruction extends IntInstruction implements JumpInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7018453916150975326L;

    private int address;

    IntJumpInstruction() {
        address = 0;
    }

    @Override
    public int getAddress() {
        return address;
    }

    void setAddress(int address) {
        this.address = address;
    }

    @Override
    public void mutate(Mutator mutator) {
        super.mutate(mutator);
        int minJump = mutator.getMinJumpSize();
        int jumpRange = mutator.getMaxJumpSize() - minJump;
        address = minJump + (mutator.getNextInt() % jumpRange);
    }
}
