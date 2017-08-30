package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.*;
import com.alphatica.genotick.processor.RobotExecutorFactory;

import java.util.List;

public interface DataSetExecutor {

    List<RobotResult> execute(List<RobotData> robotDataList, Robot robot, RobotExecutorFactory robotExecutorFactory);

}
