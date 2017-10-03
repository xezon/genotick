package com.alphatica.genotick.timepoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alphatica.genotick.data.DataException;

public class TimePoints {
    private List<TimePoint> timePoints;
    private Comparator<TimePoint> comparator;
    private boolean firstTimeIsNewest;
    
    public TimePoints(boolean firstTimeIsNewest) {
        init(100, firstTimeIsNewest);
    }
    
    public TimePoints(int size, boolean firstTimeIsNewest) {
        init(size, firstTimeIsNewest);
        allocate(size);
    }
    
    public TimePoints(TimePoints other) {
        this.firstTimeIsNewest = other.firstTimeIsNewest;
        copy(other);
    }
    
    public TimePoints(TimePoints other, boolean firstTimeIsNewest) {
        this.firstTimeIsNewest = firstTimeIsNewest;
        copy(other);
    }
    
    public TimePoints getCopy() {
        return new TimePoints(this);
    }
    
    public TimePoints getReversedCopy() {
        return new TimePoints(this, !firstTimeIsNewest);
    }
      
    public void copy(TimePoints other) {
        if (firstTimeIsNewest == other.firstTimeIsNewest) {
            copyStraight(other);
        }
        else {
            copyReversed(other);
        }
    }
    
    public void add(TimePoints other) {
        if (firstTimeIsNewest == other.firstTimeIsNewest) {
            addStraight(other);
        }
        else {
            addReversed(other);
        }
    }
    
    public void merge(TimePoints other) {
        add(other);
        timePoints = timePoints.stream().distinct().collect(Collectors.toList());
        timePoints.sort(comparator);
    }
    
    public void set(int index, TimePoint timePoint) {
        timePoints.set(index, timePoint);
    }
    
    public TimePoint get(int index) {
        return timePoints.get(index);
    }
    
    public boolean isValidIndex(int index) {
        return (index >= 0) && (index < size());
    }
        
    public int getIndex(TimePoint timePoint) {
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
    
    public boolean firstTimeIsNewest() {
        return firstTimeIsNewest;
    }
    
    public void verifyOrder() {
        int offset1 = firstTimeIsNewest ? -1 :  0;
        int offset2 = firstTimeIsNewest ?  0 : -1;
        for (int i = 1, count = size(); i < count; ++i) {
            final TimePoint timePoint = timePoints.get(i + offset1);
            final TimePoint previousTimePoint = timePoints.get(i + offset2);
            if (timePoint.compareTo(previousTimePoint) <= 0) {
                throw new DataException(String.format("TimePoint '%d' at index '%d' is unexpectingly not greater than TimePoint '%d' at index '%d'."
                        , timePoint.getValue(), i + offset1
                        , previousTimePoint.getValue(), i + offset2));
            }
        }
    }
    
    private void init(int capacity, boolean firstTimeIsNewest) {
        this.timePoints = new ArrayList<TimePoint>(capacity);
        this.comparator = firstTimeIsNewest ? Collections.reverseOrder(TimePoint::compareTo) : TimePoint::compareTo;
        this.firstTimeIsNewest = firstTimeIsNewest;
    }
    
    private void allocate(int size) {
        while (timePoints.size() > size) {
            timePoints.remove(timePoints.size() - 1);
        }
        while (timePoints.size() < size) {
            timePoints.add(null);
        }
    }
    
    private void allocateIfNecessary(int expectedSize) {
        if (size() != expectedSize) {
            allocate(expectedSize);
        }
    }
    
    private void copyStraight(int index, TimePoints other) {
        final int size = other.size();
        for (int i = 0; i < size; ++index, ++i) {
            timePoints.set(index, other.get(i));
        }
    }
    
    private void copyReversed(int index, TimePoints other) {
        final int size = other.size();
        for (int i = 0; i < size; ++index, ++i) {
            timePoints.set(index, other.get(size - i - 1));
        }
    }
    
    private void moveRight(int indexCount, int moveCount) {
        for (int i = indexCount + moveCount - 1; i >= indexCount; --i) {
            TimePoint timePoint = timePoints.get(i - indexCount);
            timePoints.set(i, timePoint);
        }
    }
    
    private void copyStraight(TimePoints other) {
        allocateIfNecessary(other.size());
        copyStraight(0, other);
    }
    
    private void copyReversed(TimePoints other) {
        allocateIfNecessary(other.size());
        copyReversed(0, other);
    }
    
    private void addStraight(TimePoints other) {
        final int thisSize = size();
        final int otherSize = other.size();
        final int expectedSize = thisSize + otherSize;
        allocateIfNecessary(expectedSize);
        if (firstTimeIsNewest) {
            moveRight(thisSize, otherSize);
            copyStraight(0, other);
        }
        else {
            copyStraight(thisSize, other);
        }
    }
    
    private void addReversed(TimePoints other) {
        final int thisSize = size();
        final int otherSize = other.size();
        final int expectedSize = thisSize + otherSize;
        allocateIfNecessary(expectedSize);
        if (firstTimeIsNewest) {
            moveRight(thisSize, otherSize);
            copyReversed(0, other);
        }
        else {
            copyReversed(thisSize, other);
        }
    }
}
