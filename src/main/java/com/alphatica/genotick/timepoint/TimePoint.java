package com.alphatica.genotick.timepoint;

import java.io.Serializable;

public class TimePoint implements Comparable<TimePoint>, Serializable {

    private static final long serialVersionUID = -6541300869299964665L;
    private final long value;
    
    public TimePoint(long value) {
        this.value = value;
    }

    public TimePoint(TimePoint timePoint) {
        this(timePoint.value);
    }

    public long getValue() {
        return value;
    }

    public boolean isLessThan(TimePoint other) {
        return this.compareTo(other) < 0;
    }
    
    public boolean isGreaterThan(TimePoint other) {
        return this.compareTo(other) > 0;
    }
    
    public boolean isLessOrEqual(TimePoint other) {
        return this.compareTo(other) <= 0;
    }
    
    public boolean isGreaterOrEqual(TimePoint other) {
        return this.compareTo(other) >= 0;
    }
    
    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") TimePoint timePoint) {
        return Long.compare(this.value, timePoint.value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        TimePoint timePoint = (TimePoint)other;
        return value == timePoint.value;
    }

    @Override
    public int hashCode() {        
        return Long.hashCode(value);
    }
}
