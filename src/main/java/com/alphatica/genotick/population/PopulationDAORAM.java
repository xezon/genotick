package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.RandomGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PopulationDAORAM implements PopulationDAO {
    private final Map<RobotName,Robot> map = new HashMap<>();
    private final Random random;

    public PopulationDAORAM() {
        random = RandomGenerator.assignRandom();
    }
    @Override
    public Iterable<Robot> getRobotList() {
        return () -> map.values().iterator();
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

    @Override
    public Set<RobotName> listRobotNames() {
        return map.keySet();
    }
}
