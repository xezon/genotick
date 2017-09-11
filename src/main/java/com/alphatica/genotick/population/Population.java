package com.alphatica.genotick.population;


import java.util.List;
import java.util.Set;

public interface Population {

    void setSettings(PopulationSettings settings);

    PopulationSettings getSettings();

    int getSize();

    double getAverageAge();

    void setDao(PopulationDAO dao);

    void saveRobot(Robot robot);

    Robot getRobot(RobotName key);

    void removeRobot(RobotName robotName);

    List<RobotInfo> getRobotInfoList();

    boolean hasSpaceToBreed();

    void loadFromDisk(String path);
    
    boolean saveToDisk(String path);

    Set<RobotName> listRobotsNames();

}
