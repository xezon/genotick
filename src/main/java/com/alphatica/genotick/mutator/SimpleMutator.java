package com.alphatica.genotick.mutator;

import com.alphatica.genotick.genotick.RandomGenerator;
import com.alphatica.genotick.instructions.Instruction;
import com.alphatica.genotick.processor.Processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class SimpleMutator implements Mutator {
    private MutatorSettings settings;
    private RandomGenerator random;
    private final List<Constructor<? super Instruction>> instructionConstructorList;
    
    private int totalInstructions;

    private SimpleMutator() throws ClassNotFoundException {
        instructionConstructorList = buildInstructionList();
        totalInstructions = instructionConstructorList.size();
    }

    @SuppressWarnings("unchecked")
    private static List<Constructor<? super Instruction>> buildInstructionList() throws ClassNotFoundException {
        List<Constructor<? super Instruction>> instructionConstructorList = new ArrayList<>();
        Class<Processor> processorClass = Processor.class;
        Method[] methods = processorClass.getDeclaredMethods();
        for(Method m: methods) {
            Class<?> [] types = m.getParameterTypes();
            for(Class<?> t: types) {
                Class<Instruction> c = (Class<Instruction>)Class.forName(t.getName());
                try {
                    Constructor<? super Instruction> constructor = c.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    instructionConstructorList.add(constructor); 
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return instructionConstructorList;
    }

    static Mutator getInstance() {
        try {
            return new SimpleMutator();
        } catch(ClassNotFoundException ex) {
            throw new RuntimeException("Unable to get Class", ex);
        }
    }

    @Override
    public Instruction getRandomInstruction() {
        int index = random.nextInt(totalInstructions);
        return createNewInstruction(index);
    }

    private Instruction createNewInstruction(int index) {
        try {
            return (Instruction) instructionConstructorList.get(index).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getAllowInstructionMutation() {
        return random.nextDouble() < settings.instructionMutationProbability;
    }

    @Override
    public boolean getAllowNewInstruction() {
        return random.nextDouble() < settings.newInstructionProbability;
    }

    @Override
    public int getNextInt() {
        return random.nextInt();
    }

    @Override
    public double getNextDouble() {
        if(random.nextBoolean()) {
            return random.nextDouble();
        } else {
            return -random.nextDouble();
        }
    }

    @Override
    public byte getNextByte() {
        return (byte)random.nextInt();
    }

    @Override
    public void setSettings(MutatorSettings mutatorSettings) {
        this.settings = mutatorSettings;
        this.random = RandomGenerator.create(mutatorSettings.randomSeed);
    }

    @Override
    public boolean skipNextInstruction() {
        return random.nextDouble() < settings.skipInstructionProbability;
    }
}
