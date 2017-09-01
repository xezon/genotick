package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.genotick.DataSetExecutor;
import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.genotick.RobotResult;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotName;
import com.alphatica.genotick.processor.RobotExecutorFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class SimpleTimePointExecutor implements TimePointExecutor {

    private DataSetExecutor dataSetExecutor;
    private RobotExecutorFactory robotExecutorFactory;

    @Override
    public Map<RobotName, List<RobotResult>> execute(List<RobotData> robotDataList, Population population) {
        if (robotDataList.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return population.listRobotsNames().parallelStream().map(robotName -> executeRobot(robotDataList, robotName, population))
                    .collect(Collectors.toMap(list -> list.get(0).getName(), Function.identity()));
        }
    }

    private List<RobotResult> executeRobot(List<RobotData> robotDataList, RobotName robotName, Population population) {
        Robot robot = population.getRobot(robotName);
        return dataSetExecutor.execute(robotDataList, robot, robotExecutorFactory);
    }

    @Override
    public void setSettings(DataSetExecutor dataSetExecutor, RobotExecutorFactory robotExecutorFactory) {
        this.dataSetExecutor = dataSetExecutor;
        this.robotExecutorFactory = robotExecutorFactory;
    }

}
