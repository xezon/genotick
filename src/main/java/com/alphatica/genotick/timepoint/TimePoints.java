package com.alphatica.genotick.timepoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimePoints {
    private List<TimePoint> timePoints;
    
    public TimePoints() {
        timePoints = new ArrayList<TimePoint>();
    }
    
    public TimePoints(int capacity) {
        timePoints = new ArrayList<TimePoint>(capacity);
    }
    
    public void add(TimePoint timePoint) {
        timePoints.add(timePoint);
    }
    
    public void add(TimePoints other) {
        timePoints.addAll(other.timePoints);
    }
    
    public void merge(TimePoints other) {
        timePoints.addAll(other.timePoints);
        timePoints = timePoints.stream().distinct().collect(Collectors.toList());
        timePoints.sort(TimePoint::compareTo);
    }
    
    public TimePoint get(int index) {
        return timePoints.get(index);
    }
    
    public boolean isValidIndex(int index) {
        return (index >= 0) && (index < size());
    }
        
    public int getIndex(TimePoint timePoint) {
        Comparator<TimePoint> comparator = (TimePoint a, TimePoint b) -> { return a.compareTo(b); };
        return Collections.binarySearch(timePoints, timePoint, comparator);
    }
    
    public int getNearestIndex(TimePoint timePoint) {
        int index = getIndex(timePoint);
        return (index >= 0) ? index : -(index + 1);
    }
    
    public int size() {
        return timePoints.size();
    }
    
    public boolean isEmpty() {
        return timePoints.isEmpty();
    }
    
    public Stream<TimePoint> getSelection(final TimePoint startTime, final TimePoint endTime) {
        return timePoints.stream().filter(time -> time.isGreaterOrEqual(startTime) && time.isLessOrEqual(endTime));
    }
}
