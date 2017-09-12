package com.alphatica.genotick.population;

import java.util.Set;

public interface PopulationDAO {

    Iterable<Robot> getRobotList();
    
    Iterable<Robot> getRobotList(int fromIndex, int toIndex);

    int getAvailableRobotsCount();

    Robot getRobotByName(RobotName name);

    void saveRobot(Robot robot);

    void removeRobot(RobotName robotName);
    
    void removeAllRobots();

    Set<RobotName> listRobotNames();
}
