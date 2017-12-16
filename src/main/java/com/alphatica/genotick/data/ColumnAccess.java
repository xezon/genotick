package com.alphatica.genotick.data;

import java.io.Serializable;
import java.util.BitSet;

import com.alphatica.genotick.genotick.RandomGenerator;

public class ColumnAccess implements Serializable {
    private static final long serialVersionUID = -32164662984L;

    private int ignoreColumns;
    private BitSet allowedColumns;
    private BitSet accessedColumns;
    private int lastSetColumnAccess = Integer.MIN_VALUE;
    private boolean lastSetColumnAccessResult = false;
    
    public ColumnAccess(int columnCount, int ignoreColumns, ColumnAccessMergeStrategy columnAccessMergeStrategy, RandomGenerator random) {
        this.ignoreColumns = ignoreColumns;
        this.allowedColumns = columnAccessMergeStrategy.createAllowedColumns(columnCount, ignoreColumns, random);
        this.accessedColumns = createAccessedColumns(columnCount);
    }
    
    public ColumnAccess(final BitSet allowedColumns, final BitSet accessedColumns) {
        this.allowedColumns = allowedColumns;
        this.accessedColumns = accessedColumns;
    }
    
    private static BitSet createAccessedColumns(final int columnCount) {
        BitSet accessedColumns = new BitSet(columnCount);
        int requiredColumns = Math.max(columnCount, Column.OHLC.OTHER);
        for(int column = 0; column < requiredColumns; column++) {
            accessedColumns.set(column);
        }
        return accessedColumns;
    }
    
    public BitSet getAllowedColumns() {
        return allowedColumns;
    }
    
    public BitSet getAccessedColumns() {
        return accessedColumns;
    }
    
    public boolean setAccessedColumn(final int column) {
        if(column != lastSetColumnAccess) {
            // Add a check to filter out as many redundant sets as possible
            lastSetColumnAccess = column;
            lastSetColumnAccessResult = allowedColumns.get(column);
            if(lastSetColumnAccessResult) {
                accessedColumns.set(column);
            }
        }
        return lastSetColumnAccessResult;
    }
    
    public boolean isAllowedColumn(int column) {
        return allowedColumns.get(ignoreColumns + (int)Math.abs(column % (allowedColumns.length() - ignoreColumns)));     
    }
}
