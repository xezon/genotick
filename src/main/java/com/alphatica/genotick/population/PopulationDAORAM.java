package com.alphatica.genotick.population;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class PopulationDAORAM implements PopulationDAO {

    private final Map<RobotName,Robot> map;
    private final Random random;

    public PopulationDAORAM(Random random) {
        this.map = new HashMap<>();
        this.random = random;
    }

    @Override
    public Stream<Robot> getRobots() {
        return map.values().stream();
    }

    @Override
    public Stream<RobotName> getRobotNames() {
        return map.keySet().stream();
    }

    @Override
    public int getAvailableRobotsCount() {
        return map.size();
    }

    @Override
    public Robot getRobotByName(RobotName name) {
        return map.get(name);
    }

    @Override
    public void saveRobot(Robot robot) {
        if(robot.getName() == null) {
            robot.setName(getAvailableRobotName());
        }
        map.put(robot.getName(), robot);
    }

    @Override
    public void removeRobot(RobotName robotName) {
        map.remove(robotName);
    }

    @Override
    public void removeAllRobots() {
        map.clear();
    }

    private RobotName getAvailableRobotName() {
        long l;
        RobotName name;
        boolean nameExist;
        do {
            l = random.nextLong();
            if(l < 0)
                l = -l;
            name =  new RobotName(l);
            nameExist = map.containsKey(name);
        } while(nameExist);
        return name;
    }

}
