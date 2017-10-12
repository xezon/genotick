package com.alphatica.genotick.genotick;

import java.util.Arrays;
import com.alphatica.genotick.utility.JniExport;

class Predictions {
    private Prediction[] predictions;
    boolean firstPredictionIsNewest;
    
    Predictions(int size, boolean firstPredictionIsNewest) {
        this.predictions = new Prediction[size];
        this.firstPredictionIsNewest = firstPredictionIsNewest;
    }
    
    void fill(Prediction prediction) {
        Arrays.fill(predictions, Prediction.OUT);
    }
    
    void set(int index, Prediction prediction) {
        predictions[index] = prediction;
    }
    
    @JniExport
    Prediction get(int index) {
        return predictions[index];
    }
    
    Prediction getNewest() {
        return firstPredictionIsNewest ? getFirst() : getLast();
    }
    
    Prediction getOldest() {
        return firstPredictionIsNewest ? getLast() : getFirst();
    }
    
    boolean firstPredictionIsNewest() {
        return firstPredictionIsNewest;
    }
    
    @JniExport
    int size() {
        return predictions.length;
    }
    
    boolean isEmpty() {
        return predictions.length == 0;
    }
    
    private Prediction getFirst() {
        return !isEmpty() ? predictions[0] : null;
    }
    
    private Prediction getLast() {
        return !isEmpty() ? predictions[size()-1] : null;
    }
}
