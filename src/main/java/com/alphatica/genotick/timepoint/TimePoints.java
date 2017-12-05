package com.alphatica.genotick.timepoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alphatica.genotick.utility.JniExport;

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
        
    public TimePoints createCopy() {
        return new TimePoints(this);
    }
    
    public TimePoints createReversedCopy() {
        return new TimePoints(this, !firstTimeIsNewest);
    }
    
    public TimePoints createSelectionCopy(TimePoint startTime, TimePoint endTime) {
        ArrayList<TimePoint> selection = getSelection(startTime, endTime).collect(Collectors.toCollection(ArrayList::new));
        return new TimePoints(selection, this.firstTimeIsNewest);        
    }
    
    private void copy(TimePoints other) {
        if (firstTimeIsNewest == other.firstTimeIsNewest) {
            copyStraight(other);
        }
        else {
            copyReversed(other);
        }
    }
    
    private void add(TimePoints other) {
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
    
    @JniExport
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
    
    public TimePoint getNewest() {
        return firstTimeIsNewest ? getFirst() : getLast();
    }
    
    public TimePoint getOldest() {
        return firstTimeIsNewest ? getLast() : getFirst();
    }
    
    @JniExport
    public int size() {
        return timePoints.size();
    }
    
    public boolean isEmpty() {
        return timePoints.isEmpty();
    }
    
    public Stream<TimePoint> getSelection(TimePoint startTime, TimePoint endTime) {
        return timePoints.stream().filter(time -> time.isGreaterOrEqual(startTime) && time.isLessOrEqual(endTime));
    }
    
    public boolean firstTimeIsNewest() {
        return firstTimeIsNewest;
    }
    
    private TimePoints(ArrayList<TimePoint> timePoints, boolean firstTimeIsNewest) {
        this.timePoints = timePoints;
        this.comparator = getComparator(firstTimeIsNewest);
        this.firstTimeIsNewest = firstTimeIsNewest;
    }
    
    private TimePoints(int capacity, boolean firstTimeIsNewest, boolean resize) {
        this.timePoints = new ArrayList<TimePoint>(capacity);
        this.comparator = getComparator(firstTimeIsNewest);
        this.firstTimeIsNewest = firstTimeIsNewest;
        if (resize) {
            resize(capacity);
        }
    }
    
    private Comparator<TimePoint> getComparator(boolean firstTimeIsNewest) {
        return firstTimeIsNewest ? Collections.reverseOrder(TimePoint::compareTo) : TimePoint::compareTo;
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
    
    private void copyStraight(final int toIndex, final TimePoints other) {
        final int size = other.size();
        for (int fromIndex = 0; fromIndex < size; ++fromIndex) {
            timePoints.set(toIndex+fromIndex, other.get(fromIndex));
        }
    }
    
    private void copyReversed(final int toIndex, final TimePoints other) {
        final int size = other.size();
        for (int fromIndex = 0; fromIndex < size; ++fromIndex) {
            timePoints.set(toIndex+fromIndex, other.get(size - fromIndex - 1));
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
    
    private TimePoint getFirst() {
        return !timePoints.isEmpty() ? timePoints.get(0) : null;
    }
    
    private TimePoint getLast() {
        return !timePoints.isEmpty() ? timePoints.get(timePoints.size()-1) : null;
    }
}
