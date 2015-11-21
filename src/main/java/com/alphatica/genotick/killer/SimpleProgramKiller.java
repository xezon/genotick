package com.alphatica.genotick.killer;

import com.alphatica.genotick.genotick.Debug;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramInfo;

import java.util.*;


class SimpleProgramKiller implements ProgramKiller {
    private ProgramKillerSettings settings;
    private final Random random;

    public static ProgramKiller getInstance() {
        return new SimpleProgramKiller();
    }
    private SimpleProgramKiller() {
        random = new Random();
    }

    @Override
    public void killPrograms(Population population, List<ProgramInfo> originalList) {
        killNonPredictingPrograms(population, originalList);
        killNonSymmetricalPrograms(population, originalList);
        List<ProgramInfo> listCopy = new ArrayList<>(originalList);
        removeProtectedPrograms(population,listCopy);
        killProgramsByWeight(population, listCopy, originalList);
        killProgramsByAge(population, listCopy, originalList);
        Debug.d("Population average weight:",ProgramInfo.getAverageWeight(originalList));
    }

    private void killNonSymmetricalPrograms(Population population, List<ProgramInfo> list) {
        if(!settings.requireSymmetricalPrograms)
            return;
        Debug.d("Killing non symmetrical programs");
        for(int i = list.size() - 1; i >= 0; i--) {
            ProgramInfo info = list.get(i);
            if(info.getBias() != 0) {
                list.remove(i);
                population.removeProgram(info.getName());
            }
        }
        Debug.d("Finished killing non symmetrical programs");
    }

    private void killNonPredictingPrograms(Population population, List<ProgramInfo> list) {
        if(!settings.killNonPredictingPrograms)
            return;
        Debug.d("Killing non predicting programs");
        for(int i = list.size() - 1; i >= 0; i--) {
            ProgramInfo info = list.get(i);
            if(info.getTotalPredictions() == 0) {
                list.remove(i);
                population.removeProgram(info.getName());
            }
        }
        Debug.d("Finished killing non predicting programs");
    }

    private void removeProtectedPrograms(Population population, List<ProgramInfo> list) {
        protectUntilOutcomes(list);
        protectBest(population, list);
    }

    private void protectBest(Population population, List<ProgramInfo> list) {
        if(settings.protectBestPrograms > 0) {
            Collections.sort(list, ProgramInfo.comparatorByAbsoluteWeight);
            int i = (int)Math.round(settings.protectBestPrograms * population.getDesiredSize());
            while(i-- > 0) {
                ProgramInfo programInfo = getLastFromList(list);
                if(programInfo == null)
                    break;
            }
        }
    }

    private void protectUntilOutcomes(List<ProgramInfo> list) {
        for(int i = list.size()-1; i >= 0; i--) {
            ProgramInfo programInfo = list.get(i);
            if(programInfo.getTotalOutcomes() < settings.protectProgramUntilOutcomes)
                list.remove(i);
        }
    }

    @Override
    public void setSettings(ProgramKillerSettings killerSettings) {
        settings = killerSettings;
    }

    private void killProgramsByAge(Population population, List<ProgramInfo> listCopy, List<ProgramInfo> originalList) {
        Collections.sort(listCopy,ProgramInfo.comparatorByAge);
        int numberToKill = (int)Math.round(settings.maximumDeathByAge * originalList.size());
        Debug.d("Killing max",numberToKill,"by age.");
        killPrograms(listCopy,originalList,numberToKill,population,settings.probabilityOfDeathByAge);
        Debug.d("Finished killing by age.");
    }

    private void killProgramsByWeight(Population population, List<ProgramInfo> listCopy, List<ProgramInfo> originalList) {
        if(population.haveSpaceToBreed())
            return;
        Collections.sort(listCopy, ProgramInfo.comparatorByAbsoluteWeight);
        Collections.reverse(listCopy);
        int numberToKill = (int) Math.round(settings.maximumDeathByWeight * originalList.size());
        Debug.d("Killing max",numberToKill,"by weight");
        killPrograms(listCopy,originalList,numberToKill,population,settings.probabilityOfDeathByWeight);
        Debug.d("Finished killing by weight");
    }

    private void killPrograms(List<ProgramInfo> listCopy, List<ProgramInfo> originalList, int numberToKill, Population population, double probability) {
        while(numberToKill-- > 0) {
            ProgramInfo toKill = getLastFromList(listCopy);
            if(toKill == null)
                return;
            if(random.nextDouble() < probability) {
                population.removeProgram(toKill.getName());
                originalList.remove(toKill);
            }
        }
    }

    private ProgramInfo getLastFromList(List<ProgramInfo> list) {
        int size = list.size();
        if(size == 0)
            return null;
        return list.remove(size-1);
    }
}
