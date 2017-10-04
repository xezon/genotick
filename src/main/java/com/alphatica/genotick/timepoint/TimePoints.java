package com.alphatica.genotick.timepoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

import com.alphatica.genotick.data.DataException;

public class TimePoints {
    private final ArrayList<TimePoint> timePoints;
    private final Comparator<TimePoint> comparator;
    private final boolean firstTimeIsNewest;
    
    public TimePoints(boolean firstTimeIsNewest) {
        this(100, firstTimeIsNewest, false);
    }
    
    public TimePoints(int size, boolean firstTimeIsNewest) {
        this(size, firstTimeIsNewest, true);
    }
    
    public TimePoints(TimePoints other) {
        this(other.size(), other.firstTimeIsNewest, true);
        copy(other);
    }
    
    public TimePoints(TimePoints other, boolean firstTimeIsNewest) {
        this(other.size(), firstTimeIsNewest, true);
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
        timePoints.sort(comparator);
        removeDuplicates();
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
    
    private TimePoints(int capacity, boolean firstTimeIsNewest, boolean resize) {
        this.timePoints = new ArrayList<TimePoint>(capacity);
        this.comparator = firstTimeIsNewest ? Collections.reverseOrder(TimePoint::compareTo) : TimePoint::compareTo;
        this.firstTimeIsNewest = firstTimeIsNewest;
        if (resize) {
            resize(capacity);
        }
    }
    
    private void resize(int size) {
        timePoints.ensureCapacity(size);
        while (timePoints.size() > size) {
            timePoints.remove(timePoints.size() - 1);
        }
        while (timePoints.size() < size) {
            timePoints.add(null);
        }
    }
    
    private void resizeIfNecessary(int expectedSize) {
        if (size() != expectedSize) {
            resize(expectedSize);
        }
    }
    
    private void copyStraight(int toIndex, TimePoints other) {
        final int size = other.size();
        for (int fromIndex = 0; fromIndex < size; ++toIndex, ++fromIndex) {
            timePoints.set(toIndex, other.get(fromIndex));
        }
    }
    
    private void copyReversed(int toIndex, TimePoints other) {
        final int size = other.size();
        for (int fromIndex = 0; fromIndex < size; ++toIndex, ++fromIndex) {
            timePoints.set(toIndex, other.get(size - fromIndex - 1));
        }
    }
    
    private void moveRight(int indexCount, int moveCount) {
        for (int i = indexCount + moveCount - 1; i >= indexCount; --i) {
            TimePoint timePoint = timePoints.get(i - indexCount);
            timePoints.set(i, timePoint);
        }
    }
    
    private void copyStraight(TimePoints other) {
        resizeIfNecessary(other.size());
        copyStraight(0, other);
    }
    
    private void copyReversed(TimePoints other) {
        resizeIfNecessary(other.size());
        copyReversed(0, other);
    }
    
    private void addStraight(TimePoints other) {
        final int thisSize = size();
        final int otherSize = other.size();
        final int expectedSize = thisSize + otherSize;
        resizeIfNecessary(expectedSize);
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
        resizeIfNecessary(expectedSize);
        if (firstTimeIsNewest) {
            moveRight(thisSize, otherSize);
            copyReversed(0, other);
        }
        else {
            copyReversed(thisSize, other);
        }
    }
    
    private void removeDuplicates() {
        for (int i = size()-1; i > 0; --i) {
            TimePoint a = timePoints.get(i);
            TimePoint b = timePoints.get(i-1);
            if (a.equals(b)) {
                timePoints.remove(i);
            }
        }
    }
}
