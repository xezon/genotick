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
    private int totalChildren = 0;
    private int correctPredictions = 0;
    private int incorrectPredictions = 0;
    private double profitablePriceMove = 0.0;
    private double unprofitablePriceMove = 0.0;
    private double inheritedWeight = 0.0;
    private int totalOutcomes = 0;
    private int outcomesAtLastChild = 0;
    private int bias = 0;
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
    
    public int getTotalPredictions() {
        return correctPredictions + incorrectPredictions;
    }
    
    public int getCorrectPredictions() {
        return correctPredictions;
    }
    
    public int getIncorrectPredictions() {
        return incorrectPredictions;
    }
    
    public double getTotalPriceMove() {
        return profitablePriceMove + unprofitablePriceMove;
    }
    
    public double getProfitablePriceMove() {
        return profitablePriceMove;
    }
    
    public double getUnprofitablePriceMove() {
        return unprofitablePriceMove;
    }

    public double getEarnedWeight() {
        return WeightCalculator.calculateWeight(this);
    }
    
    public double getInheritedWeight() {
        return inheritedWeight;
    }
    
    public double getWeight() {
        return getInheritedWeight() + getEarnedWeight();
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
        ofNullable(current.remove(robotData.getName())).ifPresent(prediction -> {
            final double priceChange = robotData.getLastPriceChange();
            final Outcome outcome = Outcome.getOutcome(prediction, priceChange);
            totalOutcomes++;
            if (outcome == Outcome.CORRECT) {
                profitablePriceMove += Math.abs(priceChange);
                correctPredictions++;
            }
            else if (outcome == Outcome.INCORRECT) {
                unprofitablePriceMove += Math.abs(priceChange);
                incorrectPredictions++;
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

    @Override
    public String toString() {
        int length = mainFunction.getSize();
        return "Name: " + this.name.toString()
                + " Outcomes: " + String.valueOf(totalOutcomes)
                + " Weight: " + weightFormat.format(getWeight())
                + " Length: " + String.valueOf(length)
                + " Children: " + String.valueOf(totalChildren);
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

    int getOutcomesAtLastChild() {
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
