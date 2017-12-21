package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class IntIntJumpInstruction extends IntIntInstruction implements JumpInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 6418593915852218659L;

    private int address;

    IntIntJumpInstruction() {
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
