package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.RandomGenerator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class PopulationDAORAM implements PopulationDAO {

    private final Map<RobotName,Robot> map = new HashMap<>(100, 1.0f); 
    private final Random random;

    public PopulationDAORAM() {
        random = RandomGenerator.get();
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
    public synchronized void saveRobot(Robot robot) {
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
