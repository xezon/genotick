package com.alphatica.genotick.genotick;

import com.alphatica.genotick.processor.ProgramExecutorFactory;

class TimePointExecutorFactory {
    public static TimePointExecutor getDefaultExecutor(DataSetExecutor dataSetExecutor, ProgramExecutorFactory programExecutorFactory) {
        TimePointExecutor executor = new SimpleTimePointExecutor();
        executor.setSettings(dataSetExecutor,programExecutorFactory);
        return executor;
    }


}
