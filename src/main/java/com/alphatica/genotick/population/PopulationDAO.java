package com.alphatica.genotick.population;

import java.util.stream.Stream;

public interface PopulationDAO {

    Stream<Robot> getRobots();

    Stream<RobotName> getRobotNames();

    Iterable<Robot> getRobotList();
    
    Iterable<Robot> getRobotList(int fromIndex, int toIndex);

    int getAvailableRobotsCount();

    Robot getRobotByName(RobotName name);

    void saveRobot(Robot robot);

    void removeRobot(RobotName robotName);
    
    void removeAllRobots();

}
