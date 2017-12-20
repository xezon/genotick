package com.alphatica.genotick.timepoint;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimePoint implements Comparable<TimePoint>, Serializable {

    private static final long serialVersionUID = -6541300869299964665L;
    private final long value;
        
    public TimePoint(long value) {
        this.value = value;
    }

    public TimePoint(TimePoint timePoint) {
        this(timePoint.value);
    }
    
    public TimePoint(String string) {
        this.value = Long.parseLong(string);
    }

    public long getValue() {
        return value;
    }
    
    private LocalDateTime parseDateTime(String timeString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.from(formatter.parse(timeString));
    }
    
    private LocalDateTime parseDate(String timeString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDate date = LocalDate.from(formatter.parse(timeString));
        return date.atStartOfDay();
    }
    
    public LocalDateTime asLocalDateTime() {       
        String timeString = Long.toString(value);
        int length = timeString.length();
        switch (length) {
            case 8:  return parseDate(timeString, "yyyyMMdd");
            case 10: return parseDateTime(timeString, "yyyyMMddkk");
            case 12: return parseDateTime(timeString, "yyyyMMddkkmm");
            case 14: return parseDateTime(timeString, "yyyyMMddkkmmss");
            default: throw new RuntimeException("Unknown time point format");
        }
    }
    
    public Date asDate() {
        LocalDateTime ldt = asLocalDateTime();
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
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
