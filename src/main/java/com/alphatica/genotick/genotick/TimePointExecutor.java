package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramInfo;
import com.alphatica.genotick.processor.ProgramExecutorFactory;

import java.util.List;

public interface TimePointExecutor {
    List<ProgramInfo> getProgramInfos();

    TimePointResult execute(List<ProgramData> programDataList,
                            Population population,
                            boolean updatePrograms);

    void setSettings(DataSetExecutor dataSetExecutor, ProgramExecutorFactory programExecutorFactory);
}
