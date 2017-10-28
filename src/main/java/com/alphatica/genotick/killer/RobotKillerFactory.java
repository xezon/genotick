package com.alphatica.genotick.killer;

import com.alphatica.genotick.ui.UserOutput;

public class RobotKillerFactory {
    public static RobotKiller getDefaultRobotKiller(RobotKillerSettings killerSettings, UserOutput output) {
        RobotKiller killer = SimpleRobotKiller.create(output);
        killer.setSettings(killerSettings);
        return killer;
    }
}
