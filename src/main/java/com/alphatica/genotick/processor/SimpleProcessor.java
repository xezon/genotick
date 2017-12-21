package com.alphatica.genotick.processor;

import com.alphatica.genotick.data.ColumnAccess;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.instructions.AddDoubleToVariable;
import com.alphatica.genotick.instructions.AddVariableToVariable;
import com.alphatica.genotick.instructions.AverageOfColumn;
import com.alphatica.genotick.instructions.DataInstruction;
import com.alphatica.genotick.instructions.DecrementVariable;
import com.alphatica.genotick.instructions.DivideVariableByDouble;
import com.alphatica.genotick.instructions.DivideVariableByVariable;
import com.alphatica.genotick.instructions.HighestOfColumn;
import com.alphatica.genotick.instructions.IncrementVariable;
import com.alphatica.genotick.instructions.Instruction;
import com.alphatica.genotick.instructions.InstructionList;
import com.alphatica.genotick.instructions.JumpIfVariableGreaterThanDouble;
import com.alphatica.genotick.instructions.JumpIfVariableGreaterThanVariable;
import com.alphatica.genotick.instructions.JumpIfVariableGreaterThanZero;
import com.alphatica.genotick.instructions.JumpIfVariableLessThanDouble;
import com.alphatica.genotick.instructions.JumpIfVariableLessThanVariable;
import com.alphatica.genotick.instructions.JumpIfVariableLessThanZero;
import com.alphatica.genotick.instructions.LowestOfColumn;
import com.alphatica.genotick.instructions.MoveDataToVariable;
import com.alphatica.genotick.instructions.MoveDoubleToVariable;
import com.alphatica.genotick.instructions.MoveRelativeDataToVariable;
import com.alphatica.genotick.instructions.MoveVariableToVariable;
import com.alphatica.genotick.instructions.MultiplyVariableByDouble;
import com.alphatica.genotick.instructions.MultiplyVariableByVariable;
import com.alphatica.genotick.instructions.NaturalLogarithmOfData;
import com.alphatica.genotick.instructions.NaturalLogarithmOfVariable;
import com.alphatica.genotick.instructions.PercentileOfColumn;
import com.alphatica.genotick.instructions.ReturnVariableAsResult;
import com.alphatica.genotick.instructions.SqRootOfVariable;
import com.alphatica.genotick.instructions.SubtractDoubleFromVariable;
import com.alphatica.genotick.instructions.SubtractVariableFromVariable;
import com.alphatica.genotick.instructions.SumOfColumn;
import com.alphatica.genotick.instructions.SwapVariables;
import com.alphatica.genotick.instructions.TerminateInstructionList;
import com.alphatica.genotick.instructions.ZeroOutVariable;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotExecutor;
import com.alphatica.genotick.population.RobotExecutorSettings;

import java.util.Arrays;

public class SimpleProcessor extends Processor implements RobotExecutor {

    private final int processorInstructionLimit;

    private RobotData data;
    private int dataColumns;
    private double robotResult;
    private boolean finished;
    private InstructionList instructionList;
    private int instructionLimit;
    private boolean terminateInstructionList;
    private int jumpAddress;
    private int totalInstructionExecuted;
    private int maximumDataOffset;
    private int ignoreColumns;
    private ColumnAccess columnAccess;

    private SimpleProcessor(RobotExecutorSettings settings) {
        processorInstructionLimit = settings.maximumProcessorInstructionFactor;
    }
    
    public static SimpleProcessor getInstance(RobotExecutorSettings settings) {
        return new SimpleProcessor(settings);
    }

    @Override
    public Prediction executeRobot(RobotData robotData, Robot robot) {
        prepare(robotData, robot);
        try {
            return executeRobotMain();
        } catch (NotEnoughDataException |
                TooManyInstructionsExecuted |
                ArithmeticException ex) {
            return Prediction.OUT;
        }
    }

    private void prepare(RobotData robotData, Robot robot) {
        data = robotData;
        dataColumns = data.getTrainingColumnCount();
        robotResult = 0.0;
        finished = false;
        instructionList = robot.getInstructionList();
        instructionList.zeroOutVariables();
        instructionLimit = robot.getInstructionCount() * processorInstructionLimit;
        terminateInstructionList = false;
        jumpAddress = 0;
        totalInstructionExecuted = 0;
        maximumDataOffset = robot.getMaximumDataOffset();
        ignoreColumns = robot.getIgnoreColumns();
        columnAccess = robot.getColumnAccess();
    }

    private Prediction executeRobotMain()  {
        executeInstructionList();
        if (finished) {
            return Prediction.getPrediction(robotResult);
        } else {
            return Prediction.getPrediction(instructionList.getVariable(0));
        }
    }

//    private static int clamp(int val, int min, int max) {
//        return Math.max(min, Math.min(max, val));
//    }

    private void executeInstructionList()  {
        int instructionPointer = 0;
        do {
            Instruction instruction = instructionList.getInstruction(instructionPointer++);
            instruction.executeOn(this);
            totalInstructionExecuted++;
            if(totalInstructionExecuted > instructionLimit) {
                break;
            }
//            if (jumpAddress != 0) {
//                instructionPointer += jumpAddress;
//                int min = 0;
//                int max = instructionList.getInstructionCount() - 1;
//                instructionPointer = clamp(instructionPointer, min, max);
//                jumpAddress = 0;
//            }
        } while (!terminateInstructionList && !finished);
    }

    @Override
    public void execute(@SuppressWarnings("unused") TerminateInstructionList ins) {
        terminateInstructionList = true;
    }

    @Override
    public void execute(MultiplyVariableByVariable ins) {
        double var1 = instructionList.getVariable(ins.getVariable1Argument());
        double var2 = instructionList.getVariable(ins.getVariable2Argument());
        instructionList.setVariable(ins.getVariable1Argument(), var1 * var2);
    }

    @Override
    public void execute(MoveDoubleToVariable ins) {
        instructionList.setVariable(ins.getVariableArgument(), ins.getDoubleArgument());
    }

    @Override
    public void execute(DivideVariableByVariable ins) {
        double var1 = instructionList.getVariable(ins.getVariable1Argument());
        double var2 = instructionList.getVariable(ins.getVariable2Argument());
        instructionList.setVariable(ins.getVariable1Argument(), var1 / var2);
    }

    @Override
    public void execute(ReturnVariableAsResult ins) {
        robotResult = instructionList.getVariable(ins.getVariableArgument());
        finished = true;
    }

    @Override
    public void execute(MoveDataToVariable ins) {
        int offset = fixOffset(ins.getDataOffsetIndex());
        int column = fixColumn(ins.getDataColumnIndex());
        if(!columnAccess.setAccessedColumn(column)) throw new NotEnoughDataException();
        double value = data.getTrainingPriceData(column, offset);
        instructionList.setVariable(ins.getVariableArgument(),value);
    }

    @Override
    public void execute(MoveRelativeDataToVariable ins) {
        int varOffset = getRelativeOffset(ins);
        int column = fixColumn(ins.getDataColumnIndex());
        if(!columnAccess.setAccessedColumn(column)) throw new NotEnoughDataException();
        double value = data.getTrainingPriceData(column, varOffset);
        instructionList.setVariable(ins.getVariableArgument(), value);
    }

    private void jumpTo(int jumpAddress) {
        this.jumpAddress = jumpAddress;
    }

    @Override
    public void execute(JumpIfVariableGreaterThanVariable ins) {
        double variable1 = instructionList.getVariable(ins.getVariable1Argument());
        double variable2 = instructionList.getVariable(ins.getVariable2Argument());
        if(Double.compare(variable1, variable2) > 0) {
            jumpTo(ins.getAddress());
        }
    }

    @Override
    public void execute(JumpIfVariableLessThanVariable ins) {
        double variable1 = instructionList.getVariable(ins.getVariable1Argument());
        double variable2 = instructionList.getVariable(ins.getVariable2Argument());
        if(Double.compare(variable1, variable2) < 0) {
            jumpTo(ins.getAddress());
        }
    }

    @Override
    public void execute(JumpIfVariableGreaterThanDouble ins) {
        double variable = instructionList.getVariable(ins.getVariableArgument());
        if(Double.compare(variable, ins.getDoubleArgument()) > 0) {
            jumpTo(ins.getAddress());
        }
    }

    @Override
    public void execute(JumpIfVariableLessThanDouble ins) {
        double variable = instructionList.getVariable(ins.getVariableArgument());
        if(Double.compare(variable, ins.getDoubleArgument()) < 0) {
            jumpTo(ins.getAddress());
        }
    }

    @Override
    public void execute(JumpIfVariableGreaterThanZero ins) {
        double variable = instructionList.getVariable(ins.getVariableArgument());
        if(variable > 0.0) {
            jumpTo(ins.getAddress());
        }
    }

    @Override
    public void execute(JumpIfVariableLessThanZero ins) {
        double variable = instructionList.getVariable(ins.getVariableArgument());
        if(variable < 0.0) {
            jumpTo(ins.getAddress());
        }
    }

    @Override
    public void execute(NaturalLogarithmOfData ins) {
        int column = fixColumn(ins.getDataColumnIndex());
        int offset = fixOffset(ins.getDataOffsetIndex());
        if(!columnAccess.setAccessedColumn(column)) throw new NotEnoughDataException();
        double value = Math.log(data.getTrainingPriceData(column,offset));
        instructionList.setVariable(ins.getVariableArgument(), value);
    }

    @Override
    public void execute(NaturalLogarithmOfVariable ins) {
        double value = Math.log(instructionList.getVariable(ins.getVariable2Argument()));
        instructionList.setVariable(ins.getVariable1Argument(),value);
    }

    @Override
    public void execute(SqRootOfVariable ins) {
        double value = Math.pow(instructionList.getVariable(ins.getVariable2Argument()), 0.5);
        instructionList.setVariable(ins.getVariable1Argument(),value);
    }
 
    @Override
    public void execute(SwapVariables ins) {
        double var1 = instructionList.getVariable(ins.getVariable1Argument());
        double var2 = instructionList.getVariable(ins.getVariable2Argument());
        instructionList.setVariable(ins.getVariable1Argument(), var2);
        instructionList.setVariable(ins.getVariable2Argument(), var1);
    }

    @Override
    public void execute(DivideVariableByDouble ins) {
        double var = instructionList.getVariable(ins.getVariableArgument());
        double result = var / ins.getDoubleArgument();
        instructionList.setVariable(ins.getVariableArgument(), result);
    }

    @Override
    public void execute(MultiplyVariableByDouble ins) {
        double var = instructionList.getVariable(ins.getVariableArgument());
        double result = var * ins.getDoubleArgument();
        instructionList.setVariable(ins.getVariableArgument(), result);
    }

    @Override
    public void execute(ZeroOutVariable ins) {
        instructionList.setVariable(ins.getVariableArgument(), 0.0);
    }

    @Override
    public void execute(IncrementVariable ins) {
        double var = instructionList.getVariable(ins.getVariableArgument());
        var++;
        instructionList.setVariable(ins.getVariableArgument(), var);
    }

    @Override
    public void execute(DecrementVariable ins) {
        double var = instructionList.getVariable(ins.getVariableArgument());
        var--;
        instructionList.setVariable(ins.getVariableArgument(), var);
    }

    @Override
    public void execute(AddDoubleToVariable ins) {
        double var = instructionList.getVariable(ins.getVariableArgument());
        double result = var + ins.getDoubleArgument();
        instructionList.setVariable(ins.getVariableArgument(), result);
    }

    @Override
    public void execute(SubtractDoubleFromVariable ins) {
        double var = instructionList.getVariable(ins.getVariableArgument());
        double result = var - ins.getDoubleArgument();
        instructionList.setVariable(ins.getVariableArgument(), result);
    }

    @Override
    public void execute(AddVariableToVariable ins) {
        double var1 = instructionList.getVariable(ins.getVariable1Argument());
        double var2 = instructionList.getVariable(ins.getVariable2Argument());
        double result = var1 + var2;
        instructionList.setVariable(ins.getVariable1Argument(), result);
    }

    @Override
    public void execute(SubtractVariableFromVariable ins) {
        double var1 = instructionList.getVariable(ins.getVariable1Argument());
        double var2 = instructionList.getVariable(ins.getVariable2Argument());
        double result = var1 - var2;
        instructionList.setVariable(ins.getVariable1Argument(),result);
    }

    @Override
    public void execute(MoveVariableToVariable ins) {
        double var = instructionList.getVariable(ins.getVariable2Argument());
        instructionList.setVariable(ins.getVariable1Argument(),var);
    }

    private double getSum(int column, int length) {
        double sum = 0;
        if(!columnAccess.setAccessedColumn(column)) throw new NotEnoughDataException();
        for(int i = 0; i < length; i++) {
            sum += data.getTrainingPriceData(column,i);
        }
        return sum;
    }
    
    @Override
    public void execute(SumOfColumn ins) {
        int column = fixColumn(ins.getVariable1Argument());
        int length = fixOffset(instructionList.getVariable(ins.getVariable2Argument()));
        instructionList.setVariable(0, getSum(column,length));
    }

    @Override
    public void execute(AverageOfColumn ins) {
        int column = fixColumn(ins.getVariable1Argument());
        int length = fixOffset(instructionList.getVariable(ins.getVariable2Argument()));
        double sum = getSum(column, length);
        instructionList.setVariable(0, sum / length);
    }

    @Override
    public void execute(PercentileOfColumn ins) {
        int column = fixColumn(ins.getVariable1Argument());
        int length = fixOffset(instructionList.getVariable(ins.getVariable2Argument()));
        if(length == 0) {
            return;
        }
        if(!columnAccess.setAccessedColumn(column)) throw new NotEnoughDataException();
        double[] priceDataList = new double[length];
        for(int i = 0; i < length; i++) {
            priceDataList[i] = data.getTrainingPriceData(column,i);
        }
        Arrays.sort(priceDataList);
        int percentileIndex = (int)Math.ceil(((double)ins.getPercentile() / (double)100) * (double)priceDataList.length);
        instructionList.setVariable(0, priceDataList[percentileIndex-1]);
    }
    
    @Override
    public void execute(HighestOfColumn ins) {
        int column = fixColumn(ins.getVariable1Argument());
        int length = fixOffset(instructionList.getVariable(ins.getVariable2Argument()));
        if(!columnAccess.setAccessedColumn(column)) throw new NotEnoughDataException();
        double highest = data.getTrainingPriceData(column,0);
        for(int i = 1; i < length; i++) {
            double check = data.getTrainingPriceData(column,i);
            if(check > highest) {
                highest = check;
            }
        }
        instructionList.setVariable(0, highest);
    }

    @Override
    public void execute(LowestOfColumn ins) {
        int column = fixColumn(ins.getVariable1Argument());
        int length = fixOffset(instructionList.getVariable(ins.getVariable2Argument()));
        if(!columnAccess.setAccessedColumn(column)) throw new NotEnoughDataException();
        double lowest = data.getTrainingPriceData(column,0);
        for(int i = 1; i < length; i++) {
            double check = data.getTrainingPriceData(column,i);
            if(check < lowest) {
                lowest = check;
            }
        }
        instructionList.setVariable(0, lowest);
    }

    private int getRelativeOffset(DataInstruction ins) {
        double value = instructionList.getVariable(ins.getDataOffsetIndex());
        return fixOffset(value);
    }

    private int fixOffset(double value) {
        return Math.abs((int)value % maximumDataOffset);
    }
    private int fixColumn(int value) {
        return ignoreColumns + Math.abs(value % (dataColumns - ignoreColumns));
    }
}
