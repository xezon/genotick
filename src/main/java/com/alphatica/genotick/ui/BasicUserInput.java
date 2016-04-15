package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.Simulation;

@SuppressWarnings("WeakerAccess")
abstract public class BasicUserInput implements UserInput {
    private Simulation simulation;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public MainAppData getData(String settings) {
        DataLoader dl = DataFactory.getDefaultLoader(settings);
        return dl.createRobotData();
    }

}
