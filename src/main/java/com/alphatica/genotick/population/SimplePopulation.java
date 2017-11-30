package com.alphatica.genotick.population;


import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimplePopulation implements Population {
    private PopulationSettings settings;
    private PopulationDAO dao;

    @Override
    public void setSettings(PopulationSettings settings) {
        this.settings = settings;
    }

    @Override
    public int getDesiredSize() {
        return settings.desiredSize;
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
        return dao.getRobots().map(RobotInfo::new).collect(Collectors.toList());
    }

    @Override
    public boolean hasSpaceToBreed() {
        return getSize() < settings.desiredSize;
    }

    @Override
    public void loadFromFolder(String path) {
        if (canSave()) {
            dao.removeAllRobots();
            PopulationDAO fs = new PopulationDAOFileSystem(path);
            int size = settings.desiredSize;
            fs.getRobots().limit(size).forEach(dao::saveRobot);
        }
    }
    
    @Override
    public boolean saveOnDisk() {
        if (canSave()) {
            String path = settings.daoPath;
            if (!path.isEmpty()) {
                saveToExistingFolder(path);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Stream<Robot> getRobots() {
        return dao.getRobots();
    }

    @Override
    public double getAverageAge() {
        return dao.getRobots().map(Robot::getTotalPredictions).collect(Collectors.averagingDouble(Integer::doubleValue));
    }

    @Override
    public void saveToFolder(String path) {
        if (canSave()) {
            if (createDirs(path)) {
                if(saveToExistingFolder(path) == 0) {
                    removeDir(path);
                }
            } else {
                throw new DAOException("Unable to save to path " + path);
            }
        }
    }

    private static boolean createDirs(String path) {
        File dirFile = new File(path);
        return dirFile.exists() || dirFile.mkdirs();
    }

    private static void removeDir(String path) {
        File dirFile = new File(path);
        if(dirFile.isDirectory()) {
            dirFile.delete();
        }
    }

    private int saveToExistingFolder(String path) {
        PopulationDAO fs = new PopulationDAOFileSystem(path);
        fs.removeAllRobots();
        AtomicInteger savedCount = new AtomicInteger(0);
        dao.getRobots().forEach((robot) -> {
            if((settings.killNonPredictingRobots == false || robot.isPredicting()) &&
                Math.abs(robot.getWeight()) >= settings.minimumScoreToSaveToDisk) {
                fs.saveRobot(robot);
                savedCount.incrementAndGet();
            }
        });
        return savedCount.get();
    }

    private boolean canSave() {
        return !(dao instanceof PopulationDAOFileSystem);
    }
}
