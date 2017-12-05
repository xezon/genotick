package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.genotick.DataSetExecutor;
import com.alphatica.genotick.genotick.RobotDataPair;
import com.alphatica.genotick.genotick.RobotResultPair;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotName;
import com.alphatica.genotick.processor.RobotExecutorFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SimpleTimePointExecutor implements TimePointExecutor {

    private DataSetExecutor dataSetExecutor;
    private RobotExecutorFactory robotExecutorFactory;

    @Override
    public Map<RobotName, List<RobotResultPair>> execute(List<RobotDataPair> robotDataList, Population population) {
        if (robotDataList.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Stream<List<RobotResultPair>> stream = population.getRobots().parallel().map(robot -> executeRobot(robotDataList, robot));
            return stream.collect(Collectors.toMap(list -> list.get(0).getOriginal().getName(), Function.identity()));
        }
    }

    private List<RobotResultPair> executeRobot(List<RobotDataPair> robotDataList, Robot robot) {
        return dataSetExecutor.execute(robotDataList, robot, robotExecutorFactory);
    }

    @Override
    public void setSettings(DataSetExecutor dataSetExecutor, RobotExecutorFactory robotExecutorFactory) {
        this.dataSetExecutor = dataSetExecutor;
        this.robotExecutorFactory = robotExecutorFactory;
    }

}
