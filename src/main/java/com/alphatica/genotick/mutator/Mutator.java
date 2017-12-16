package com.alphatica.genotick.mutator;

import com.alphatica.genotick.data.ColumnAccess;
import com.alphatica.genotick.instructions.Instruction;

public interface Mutator {
    Instruction getRandomInstruction();

    boolean getAllowInstructionMutation();

    boolean getAllowNewInstruction();

    int getNextInt();
    
    int getNextColumn();

    double getNextDouble();

    byte getNextByte();

    void setSettings(MutatorSettings mutatorSettings);
    
    void setColumnAccess(ColumnAccess columnAccess);

    boolean skipNextInstruction();

}
