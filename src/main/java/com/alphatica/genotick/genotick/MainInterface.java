package com.alphatica.genotick.genotick;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alphatica.genotick.data.MainAppData;

public class MainInterface {
    private static class Session {
        final MainSettings settings = MainSettings.getSettings();
        final MainAppData data = new MainAppData();
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
        if (!hasSession(sessionId)) {
            return ErrorCode.INVALID_SESSION.getValue();
        }
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
    
    public static void createSession(int sessionId) {
        sessions.put(sessionId, new Session());
    }
    
    public static void clearSession(int sessionId) {
        sessions.remove(sessionId);
    }
    
    public static void clearSessions() {
        sessions.clear();
    }
    
    private static void printStart(int sessionId, String[] args) {
        System.out.println(String.format("Starting session %d with arguments:", sessionId));
        for (String arg : args) {
            System.out.println(arg);
        }
    }
    
    private static boolean hasSession(int sessionId) {
        return sessions.get(sessionId) != null;
    }
}
