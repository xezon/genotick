package com.alphatica.genotick.population;

import java.util.List;

public interface Population {

    void setDesiredSize(int size);

    int getDesiredSize();

    int getSize();

    void setDao(PopulationDAO dao);

    void saveProgram(Program program);

    Program getProgram(ProgramName key);

    void removeProgram(ProgramName programName);

    List<ProgramInfo> getProgramInfoList();

    boolean haveSpaceToBreed();

    void savePopulation(String path);

    ProgramName[] listProgramNames();
}
