package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.genotick.DataSetExecutor;
import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.genotick.RobotResult;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.RobotName;
import com.alphatica.genotick.processor.RobotExecutorFactory;

import java.util.List;
import java.util.Map;

public interface TimePointExecutor {

    Map<RobotName, List<RobotResult>> execute(List<RobotData> robotDataList, Population population);

    void setSettings(DataSetExecutor dataSetExecutor, RobotExecutorFactory robotExecutorFactory);
}
