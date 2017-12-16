package com.alphatica.genotick.data;

import java.util.BitSet;

import com.alphatica.genotick.genotick.RandomGenerator;

public interface ColumnAccessMergeStrategy {
    BitSet createAllowedColumns(final int columnCount, final int ignoreColumns, final RandomGenerator random);
    
    ColumnAccess merge(final ColumnAccess parent1, final ColumnAccess parent2);
}
