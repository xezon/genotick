package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;
import com.alphatica.genotick.processor.Processor;

public class PercentileOfColumn extends RegRegInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -329518949586814597L;

	protected int percentile = 95;
		
    @SuppressWarnings("unused")
    public PercentileOfColumn() {
    }

    private PercentileOfColumn(PercentileOfColumn percentileOfColumn) {
        this.setRegister1(percentileOfColumn.getRegister1());
        this.setRegister2(percentileOfColumn.getRegister2());
        this.percentile = percentileOfColumn.percentile;
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new PercentileOfColumn(this);
    }
    
    @Override
    public void mutate(Mutator mutator) {
        super.mutate(mutator);
        percentile = (Math.abs(mutator.getNextInt()) % 100) + 1;
    }

    public int getPercentile() {
        return percentile;
    }
}
