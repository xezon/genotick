package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.genotick.DataSetExecutor;
import com.alphatica.genotick.genotick.RobotDataPair;
import com.alphatica.genotick.genotick.RobotResultPair;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.RobotName;
import com.alphatica.genotick.processor.RobotExecutorFactory;

import java.util.List;
import java.util.Map;

public interface TimePointExecutor {

    Map<RobotName, List<RobotResultPair>> execute(List<RobotDataPair> robotDataList, Population population);

    void setSettings(DataSetExecutor dataSetExecutor, RobotExecutorFactory robotExecutorFactory);
}
