package com.alphatica.genotick.timepoint;

import com.alphatica.genotick.genotick.DataSetExecutor;
import com.alphatica.genotick.processor.ProgramExecutorFactory;

public class TimePointExecutorFactory {
    public static TimePointExecutor getDefaultExecutor(DataSetExecutor dataSetExecutor, ProgramExecutorFactory programExecutorFactory) {
        TimePointExecutor executor = new SimpleTimePointExecutor();
        executor.setSettings(dataSetExecutor,programExecutorFactory);
        return executor;
    }


}
