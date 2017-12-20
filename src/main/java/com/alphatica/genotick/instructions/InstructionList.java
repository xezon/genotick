package com.alphatica.genotick.instructions;

import com.alphatica.genotick.genotick.RandomGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InstructionList implements Serializable {

    private static final long serialVersionUID = 267402795981161615L;

    private final RandomGenerator random;
    private final List<Instruction> list;
    private final double[] variables;

    private InstructionList(RandomGenerator random, int minVariables, int maxVariables) {
        int variablesRange = maxVariables - minVariables;
        int variablesCount = minVariables + Math.abs(random.nextInt() % variablesRange);
        this.random = random;
        this.list = new ArrayList<>();
        this.variables = new double[variablesCount];
    }

    public static InstructionList create(RandomGenerator random, int minVariables, int maxVariables) {
        return new InstructionList(random, minVariables, maxVariables);
    }

    public Instruction getInstruction(int index) {
        if (index < 0 || index >= list.size())
            return new TerminateInstructionList();
        else
            return list.get(index);
    }

    public double getVariable(int index) {
        return variables[validateVariableNumber(index)];
    }

    public void setVariable(int index, double value) {
        variables[validateVariableNumber(index)] = value;
    }

    public void zeroOutVariables() {
        for(int i = 0; i < variables.length; i++)
            variables[i] = 0;
    }

    public void addInstruction(Instruction instruction) {
        list.add(instruction);
    }
    
    public int getInstructionCount() {
        return list.size();
    }

    public int getVariableCount() {
        return variables.length;
    }

    @SuppressWarnings("unused")
    public void addInstructionAtPosition(Instruction instruction, int position) {
        position = fixPosition(position);
        list.add(position,instruction);
    }

    private int validateVariableNumber(int index) {
    	if(index < 0 || index >= variables.length) {
        	return Math.abs(index % variables.length);
        } else {
        	return index;       
        }
    }

    private int fixPosition(int position) {
        if(position >= 0 && position < list.size())
            return position;
        else
            return random.nextInt(list.size());
    }
}
