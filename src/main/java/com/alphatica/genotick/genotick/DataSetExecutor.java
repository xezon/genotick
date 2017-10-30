package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.processor.RobotExecutorFactory;

import java.util.List;

public interface DataSetExecutor {

    List<RobotResultPair> execute(List<RobotDataPair> robotDataList, Robot robot, RobotExecutorFactory robotExecutorFactory);

}
