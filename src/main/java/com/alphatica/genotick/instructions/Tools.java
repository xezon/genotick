package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

class Tools {
    static double mutateDouble(double argument, Mutator mutator) {
        return argument + argument * mutator.getNextDouble();
    }
}
