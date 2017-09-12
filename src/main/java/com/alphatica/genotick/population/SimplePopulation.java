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
    public int getDesiredSize() {
        return settings.desiredSize();
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
    public void loadFromFolder(String path) {
        if (canSave()) {
            dao.removeAllRobots();
            PopulationDAO fs = new PopulationDAOFileSystem(path);
            int size = settings.desiredSize();
            for(Robot robot : fs.getRobotList(0, size)) {
                dao.saveRobot(robot);
            }
        }
    }
    
    @Override
    public boolean saveOnDisk() {
        if (canSave()) {
            String path = settings.daoPath();
            if (!path.isEmpty()) {
                saveToExistingFolder(path);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean saveToFolder(String path) {
        if (canSave()) {
            File dirFile = new File(path);
            if (!dirFile.exists() && !dirFile.mkdirs()) {
                return false;
            }
            else {
                saveToExistingFolder(path);
            }
        }
        return true;
    }
    
    private void saveToExistingFolder(String path) {
        PopulationDAO fs = new PopulationDAOFileSystem(path);
        fs.removeAllRobots();
        for(Robot robot : dao.getRobotList()) {
            fs.saveRobot(robot);
        }
    }
    
    private boolean canSave() {
        return !(dao instanceof PopulationDAOFileSystem);
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
