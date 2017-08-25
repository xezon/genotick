package com.alphatica.genotick.population;

import java.util.Set;

public interface PopulationDAO {

    Iterable<Robot> getRobotList();

    int getAvailableRobotsCount();

    Robot getRobotByName(RobotName name);

    void saveRobot(Robot robot);

    void removeRobot(RobotName robotName);

    Set<RobotName> listRobotNames();
}
