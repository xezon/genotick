package com.alphatica.genotick.population;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimplePopulation implements Population {
    private PopulationSettings settings;
    private PopulationDAO dao;

    @Override
    public void setSettings(PopulationSettings settings) {
        this.settings = settings;
    }

    @Override
    public PopulationSettings getSettings() {
        return settings;
    }

    @Override
    public int getSize() {
        return dao.getAvailableRobotsCount();
    }

    @Override
    public void setDao(PopulationDAO dao) {
        this.dao = dao;
    }

    @Override
    public void saveRobot(Robot robot) {
        dao.saveRobot(robot);
    }

    @Override
    public Robot getRobot(RobotName name) {
        return dao.getRobotByName(name);
    }

    @Override
    public void removeRobot(RobotName robotName) {
        dao.removeRobot(robotName);
    }

    @Override
    public List<RobotInfo> getRobotInfoList() {
        List<RobotInfo> list = new ArrayList<>(dao.getAvailableRobotsCount());
        for(Robot robot : dao.getRobotList()) {
            RobotInfo robotInfo = new RobotInfo(robot);
            list.add(robotInfo);
        }
        return list;
    }

    @Override
    public boolean hasSpaceToBreed() {
        return getSize() < settings.desiredSize();
    }

    @Override
    public void loadFromDisk(String path) {
        if (!(dao instanceof PopulationDAOFileSystem)) {
            dao.removeAllRobots();
            PopulationDAO fs = new PopulationDAOFileSystem(path);
            int i = 0;
            for(Robot robot : fs.getRobotList()) {
                if(++i > settings.desiredSize())
                    break;
                dao.saveRobot(robot);
            }
        }
    }

    @Override
    public boolean saveToDisk(String path) {
        boolean success = true;
        if (!(dao instanceof PopulationDAOFileSystem)) {
            File dirFile = new File(path);
            if (!dirFile.exists() && !dirFile.mkdirs()) {
                success = false;
            }
            else {
                PopulationDAO fs = new PopulationDAOFileSystem(path);
                fs.removeAllRobots();
                for(Robot robot : dao.getRobotList()) {
                    fs.saveRobot(robot);
                }
            }
        }
        return success;
    }

    @Override
    public Set<RobotName> listRobotsNames() {
        return dao.listRobotNames();
    }

    @Override
    public double getAverageAge()
    {
        double age = 0;
        for(Robot robot : dao.getRobotList()) {
            age += robot.getTotalPredictions();
        }
        return age / this.getSize();
    }
}
