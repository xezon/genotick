package com.alphatica.genotick.processor;


import com.alphatica.genotick.instructions.*;

abstract public class Processor {

    abstract public void execute(DivideVariableByDouble ins);

    abstract public void execute(MultiplyVariableByDouble ins);

    abstract public void execute(SwapVariables ins);

    abstract public void execute(MoveDoubleToVariable ins);

    abstract public void execute(ZeroOutVariable ins);

    abstract public void execute(IncrementVariable ins);

    abstract public void execute(DecrementVariable ins);

    abstract public void execute(AddDoubleToVariable ins);

    abstract public void execute(SubtractDoubleFromVariable ins);

    abstract public void execute(DivideVariableByVariable ins);

    abstract public void execute(MultiplyVariableByVariable ins);

    abstract public void execute(AddVariableToVariable ins);

    abstract public void execute(SubtractVariableFromVariable ins);

    abstract public void execute(MoveVariableToVariable ins);

    abstract public void execute(ReturnVariableAsResult ins);

    abstract public void execute(TerminateInstructionList ins);

    abstract public void execute(MoveDataToVariable ins);

    abstract public void execute(MoveRelativeDataToVariable ins);

    abstract public void execute(JumpIfVariableGreaterThanVariable ins);

    abstract public void execute(JumpIfVariableLessThanVariable ins);

    abstract public void execute(JumpIfVariableGreaterThanDouble ins);

    abstract public void execute(JumpIfVariableLessThanDouble ins);

    abstract public void execute(JumpIfVariableGreaterThanZero ins);

    abstract public void execute(JumpIfVariableLessThanZero ins);

    abstract public void execute(NaturalLogarithmOfData ins);

    abstract public void execute(NaturalLogarithmOfVariable ins);

    abstract public void execute(SqRootOfVariable ins);

    abstract public void execute(SumOfColumn ins);

    abstract public void execute(AverageOfColumn ins);

    abstract public void execute(PercentileOfColumn ins);

    abstract public void execute(HighestOfColumn ins);

    abstract public void execute(LowestOfColumn ins);
}
