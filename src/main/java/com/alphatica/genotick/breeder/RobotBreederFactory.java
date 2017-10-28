package com.alphatica.genotick.breeder;

import com.alphatica.genotick.mutator.Mutator;
import com.alphatica.genotick.ui.UserOutput;

public class RobotBreederFactory {
    public static RobotBreeder getDefaultBreeder(BreederSettings breederSettings, Mutator mutator, UserOutput output) {
        RobotBreeder breeder = SimpleBreeder.create(output);
        breeder.setSettings(breederSettings,mutator);
        return breeder;
    }
}
