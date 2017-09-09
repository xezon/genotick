package com.alphatica.genotick.population;


import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.Outcome;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.genotick.RobotResult;
import com.alphatica.genotick.genotick.WeightCalculator;
import com.alphatica.genotick.instructions.Instruction;
import com.alphatica.genotick.instructions.InstructionList;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class Robot implements Serializable {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -32164662984L;
    private static final DecimalFormat weightFormat = new DecimalFormat("0.00");

    private RobotName name;
    private final int maximumDataOffset;
    private final int ignoreColumns;
    private InstructionList mainFunction;
    private int totalChildren;
    private int totalPredictions;
    private int correctPredictions;
    private double inheritedWeight;
    private int totalOutcomes;
    private long outcomesAtLastChild;
    private int bias;
    private boolean isPredicting = false;

    private final Map<DataSetName, Prediction> current = new HashMap<>();
    private final Map<DataSetName, Prediction> pending = new HashMap<>();

    public static Robot createEmptyRobot(int maximumDataOffset, int ignoreColumns) {
        return new Robot(maximumDataOffset, ignoreColumns);
    }

    public int getLength() {
        return mainFunction.getSize();
    }

    public RobotName getName() {
        return name;
    }

    public int getIgnoreColumns() {
        return ignoreColumns;
    }

    public void setInheritedWeight(double inheritedWeight) {
        this.inheritedWeight = inheritedWeight;
    }

    public InstructionList getMainFunction() {
        return mainFunction;
    }

    public double getWeight() {
        double earnedWeight = WeightCalculator.calculateWeight(this);
        return inheritedWeight + earnedWeight;
    }

    public void setMainInstructionList(InstructionList newMainFunction) {
        mainFunction = newMainFunction;
    }

    public void increaseChildren() {
        totalChildren++;
        outcomesAtLastChild = totalOutcomes;
    }

    public int getMaximumDataOffset() {
        return maximumDataOffset;
    }

    public void recordMarketChange(RobotData robotData) {
        ofNullable(current.get(robotData.getName())).ifPresent(prediction -> {
            current.remove(robotData.getName());
            Outcome outcome = Outcome.getOutcome(prediction, robotData.getLastPriceChange());
            totalOutcomes++;
            if(outcome != Outcome.OUT) {
                totalPredictions++;
            }
            if(outcome == Outcome.CORRECT) {
                correctPredictions++;
            }
        });
    }

    public void recordPrediction(RobotResult result) {
        DataSetName dataSetName = result.getData().getName();
        Prediction pendingPrediction = pending.get(dataSetName);
        current.put(dataSetName, pendingPrediction);
        pending.put(dataSetName, result.getPrediction());
        if(result.getPrediction() != Prediction.OUT) {
            isPredicting = true;
        }
        bias += result.getPrediction().getValue();
    }

    public int getTotalPredictions() {
        return totalPredictions;
    }

    @Override
    public String toString() {
        int length = mainFunction.getSize();
        return "Name: " + this.name.toString()
                + " Outcomes: " + String.valueOf(totalOutcomes)
                + " Weight: " + weightFormat.format(getWeight())
                + " Length: " + String.valueOf(length)
                + " Children: " + String.valueOf(totalChildren);
    }

    public int getCorrectPredictions() {
        return correctPredictions;
    }

    public void setName(RobotName name) {
        this.name = name;
    }

    public String showRobot() throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        addFields(sb);
        addMainFunction(sb);
        return sb.toString();
    }

    boolean isPredicting() {
        return isPredicting;
    }

    int getTotalChildren() {
        return totalChildren;
    }

    long getOutcomesAtLastChild() {
        return outcomesAtLastChild;
    }

    int getTotalOutcomes() {
        return totalOutcomes;
    }

    int getBias() {
        return bias;
    }

    private Robot(int maximumDataOffset, int ignoreColumns) {
        mainFunction = InstructionList.createInstructionList();
        this.maximumDataOffset = maximumDataOffset;
        this.ignoreColumns = ignoreColumns;
    }

    private void addMainFunction(StringBuilder sb) throws IllegalAccessException {
        sb.append("MainFunction:").append("\n");
        sb.append("VariableCount: ").append(mainFunction.getVariablesCount()).append("\n");
        for(int i = 0; i < mainFunction.getSize(); i++) {
            Instruction instruction = mainFunction.getInstruction(i);
            sb.append(instruction.instructionString()).append("\n");
        }
    }

    private void addFields(StringBuilder sb) throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field: fields) {
            if(field.getName().equals("mainFunction"))
                continue;
            field.setAccessible(true);
            if(!Modifier.isStatic(field.getModifiers())) {
                sb.append(field.getName()).append(" ").
                        append(field.get(this).toString()).append("\n");
            }
        }
    }
}
