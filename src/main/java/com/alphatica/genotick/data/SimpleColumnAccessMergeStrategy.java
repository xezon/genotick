package com.alphatica.genotick.data;

import java.util.BitSet;

import com.alphatica.genotick.genotick.RandomGenerator;

public class SimpleColumnAccessMergeStrategy implements ColumnAccessMergeStrategy {

    @Override
    public BitSet createAllowedColumns(final int columnCount, final int ignoreColumns, final RandomGenerator random) {
        BitSet allowedColumns = new BitSet(columnCount);
        int optionalColumns = columnCount - Column.OHLC.OTHER;
        for(int column = 0; column < columnCount; column++) {
            if(column < Column.OHLC.OTHER) {
                allowedColumns.set(column);
            } else if(column < ignoreColumns) {
                continue;
            } else {
                if(random.nextInt(optionalColumns+1) == 0) {
                    allowedColumns.set(column);
                }
            }
        }
        return allowedColumns;
    }

    @Override
    public ColumnAccess merge(ColumnAccess parent1, ColumnAccess parent2) {
        // OR together the columns that were accessed by the robot.
        BitSet childAccessColumns = (BitSet)parent1.getAccessedColumns().clone();
        childAccessColumns.or(parent2.getAccessedColumns());

        // OR together the columns that are allowed to be accessed
        BitSet childAllowedColumns = (BitSet)parent1.getAllowedColumns().clone();
        childAllowedColumns.or(parent2.getAllowedColumns());
        
        // AND the sets together to get an update allowed column list
        childAllowedColumns.and(childAccessColumns);

        // Finally clear the accessed columns for this next run
        if(childAccessColumns.length() > Column.OHLC.OTHER) {
            childAccessColumns.clear(Column.OHLC.OTHER, childAccessColumns.length());
        }

        return new ColumnAccess(childAllowedColumns, childAccessColumns);
    }
}
