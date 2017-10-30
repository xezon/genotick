package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotExecutor;
import com.alphatica.genotick.processor.RobotExecutorFactory;

import java.util.ArrayList;
import java.util.List;

public class SimpleDataSetExecutor implements DataSetExecutor {

    @Override
    public List<RobotResultPair> execute(List<RobotDataPair> robotDataList, Robot robot, RobotExecutorFactory robotExecutorFactory) {
        List<RobotResultPair> robotResultList = new ArrayList<>(robotDataList.size());
        for(RobotDataPair pair : robotDataList) {
            RobotExecutor robotExecutor = robotExecutorFactory.getDefaultRobotExecutor();
            RobotResult originalResult = getResult(robotExecutor, robot, pair.getOriginal());
            RobotResult reversedResult = (pair.getReversed() != null) ? getResult(robotExecutor, robot, pair.getReversed()) : null;
            robotResultList.add(new RobotResultPair(originalResult, reversedResult));
        }
        return robotResultList;
    }

    private RobotResult getResult(RobotExecutor robotExecutor, Robot robot, RobotData robotData) {
        Prediction prediction = robotExecutor.executeRobot(robotData, robot);
        return new RobotResult(prediction, robot, robotData);
    }
    
}
