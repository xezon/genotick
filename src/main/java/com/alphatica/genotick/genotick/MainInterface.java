package com.alphatica.genotick.genotick;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.timepoint.TimePoints;
import static com.alphatica.genotick.utility.Assert.gassert;

public class MainInterface {
    
    private static class SessionResult {
        final TimePoints timePoints;
        final Map<String, Prediction[]> predictionsMap;
        
        SessionResult(MainSettings settings, MainAppData data) {
            final Set<DataSetName> dataSetNames = data.getDataSetNames();
            this.timePoints = data.createTimePointsCopy(settings.startTimePoint, settings.endTimePoint);
            this.predictionsMap = new HashMap<String, Prediction[]>(dataSetNames.size());
            final int timePointCount = this.timePoints.size();
            for (DataSetName dataSetName : dataSetNames) {
                final Prediction[] predictions = new Prediction[timePointCount];
                Arrays.fill(predictions, Prediction.OUT);
                this.predictionsMap.put(dataSetName.getPath(), predictions);
            }
        }
        
        private Prediction[] findPredictions(DataSetName dataSetName) {
            return this.predictionsMap.get(dataSetName.getPath());
        }
        
        void savePrediction(TimePoint timePoint, DataSetName dataSetName, Prediction prediction) {
            final Prediction[] predictions = findPredictions(dataSetName);
            if (predictions != null) {
                final int index = this.timePoints.getIndex(timePoint);
                gassert(index >= 0);
                predictions[index] = prediction;
            }
        }
    }
    
    private static class Session {
        final MainSettings settings;
        final MainAppData data;
        SessionResult result;
        
        Session() {
            this.settings = MainSettings.getSettings();
            this.data = new MainAppData();
            this.result = null;
        }
        
        void prepareNewSessionResult() {
            this.result = new SessionResult(settings, data);
        }
    }
    
    private static final int INTERFACE_VERSION = 1;
    private static final Map<Integer, Session> sessions = new HashMap<Integer, Session>();
    private static int currentSessionId = 0;
    
    public static int getInterfaceVersion() {
        return INTERFACE_VERSION;
    }
    
    public static int getCurrentSessionId() {
        return currentSessionId;
    }
    
    public static int start(int sessionId, String[] args) throws IOException, IllegalAccessException {
        printStart(sessionId, args);
        final Session session = sessions.get(sessionId);
        if (session == null) {
            return printAndReturnErrorValue(ErrorCode.INVALID_SESSION);
        }
        if (session.data.isEmpty()) {
            return printAndReturnErrorValue(ErrorCode.INSUFFICIENT_DATA);
        }
        session.prepareNewSessionResult();
        currentSessionId = sessionId;
        final ErrorCode error = Main.init(args);
        return error.getValue();
    }
            
    public static MainSettings getSettings(int sessionId) {
        Session session = sessions.get(sessionId);
        return (session != null) ? session.settings : null;
    }
    
    public static MainAppData getData(int sessionId) {
        Session session = sessions.get(sessionId);
        return (session != null) ? session.data : null;
    }
    
    public static TimePoints getTimePoints(int sessionId) {
        SessionResult sessionResult = getSessionResult(sessionId);
        return (sessionResult != null) ? sessionResult.timePoints : null;
    }
    
    public static Prediction[] getPredictions(int sessionId, String dataSetName) {
        SessionResult sessionResult = getSessionResult(sessionId);
        return (sessionResult != null) ? sessionResult.predictionsMap.get(dataSetName) : null;
    }
    
    public static void savePrediction(TimePoint timePoint, DataSetName dataSetName, Prediction prediction) {
        Session currentSession = getCurrentSession();
        if (currentSession != null) {
            currentSession.result.savePrediction(timePoint, dataSetName, prediction);
        }
    }
    
    public static void createSession(int sessionId) {
        sessions.put(sessionId, new Session());
    }
    
    public static void clearSession(int sessionId) {
        sessions.remove(sessionId);
    }
    
    public static void clearSessions() {
        sessions.clear();
    }
    
    private static Session getCurrentSession() {
        return sessions.get(currentSessionId);
    }
    
    private static SessionResult getSessionResult(int sessionId) {
        Session session = sessions.get(sessionId);
        return (session != null) ? session.result : null;
    }
    
    private static void printStart(int sessionId, String[] args) {
        System.out.println(String.format("Starting session %d with arguments:", sessionId));
        for (String arg : args) {
            System.out.println(arg);
        }
    }
    
    private static int printAndReturnErrorValue(final ErrorCode error) {
        Main.printError(error);
        return error.getValue();
    }
}
