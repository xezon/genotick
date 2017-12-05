package com.alphatica.genotick.mutator;

import com.alphatica.genotick.genotick.MainSettings;

public class MutatorSettings {
    public final double instructionMutationProbability;
    public final double newInstructionProbability;
    public final double skipInstructionProbability;
    public final long randomSeed;

    public MutatorSettings(final MainSettings settings) {
        this.instructionMutationProbability = settings.instructionMutationProbability;
        this.newInstructionProbability = settings.newInstructionProbability;
        this.skipInstructionProbability = settings.skipInstructionProbability;
        this.randomSeed = settings.randomSeed;
    }
}
