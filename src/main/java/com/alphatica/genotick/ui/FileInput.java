package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.Application;
import com.alphatica.genotick.genotick.Main;
import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.genotick.TimePoint;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInput implements UserInput {
    private static final String DATA_DIRECTORY_KEY = "dataDirectory";
    private String fileName;

    public FileInput(String input) {
        String delimiter = ":";
        if(!input.contains(delimiter))
            throw new RuntimeException(String.format("Config file input format is: '%s'","input=file:/path/to/file"));
        fileName = input.split(delimiter)[1];
    }

    @Override
    public void show(Application application) {
        try {
            Map<String,String> map = buildFileMap();
            MainAppData data = createData(map,application);
            MainSettings settings = MainSettings.getSettings(data.getFirstTimePoint(),data.getLastTimePoint());
            applySettingsFromMap(settings,map);
            checkAllSettingsParsed(map);
            application.start(settings,data);
        } catch (IOException e) {
            RuntimeException re = new RuntimeException("Unable to read file " + fileName);
            re.initCause(e);
            throw re;
        } catch (IllegalAccessException e) {
            throw  new RuntimeException(e);
        }
    }

    private void checkAllSettingsParsed(Map<String, String> map) {
        if(map.isEmpty())
            return;
        throw new RuntimeException("Unable to match setting from config file: " + map.keySet().toArray()[0]);
    }

    private void applySettingsFromMap(MainSettings settings, Map<String, String> map) throws IllegalAccessException {
        Field[] fields = settings.getClass().getFields();
        for(Field field: fields) {
            applySettingFromField(settings,map,field);
        }
    }

    private void applySettingFromField(MainSettings settings, Map<String, String> map, Field field) throws IllegalAccessException {
        if(map.containsKey(field.getName())) {
            String value = map.get(field.getName());
            map.remove(field.getName());
            field.setAccessible(true);
            switch(field.getType().getName()) {
                case "java.lang.String": setString(field,settings,value); break;
                case "com.alphatica.genotick.genotick.TimePoint": setTimePoint(field,settings,value); break;
                case "boolean": setBoolean(field, settings, value); break;
                case "int": setInt(field, settings, value); break;
                case "double": setDouble(field,settings,value); break;
                case "long": setLong(field, settings, value); break;
                default: throw new RuntimeException("File config: Unable to match type " + field.getType().getName());
            }
            field.setAccessible(false);
        }
    }

    private void setLong(Field field, MainSettings settings, String value) throws IllegalAccessException {
        Long l = Long.parseLong(value);
        field.setLong(settings,l);
    }

    private void setDouble(Field field, MainSettings settings, String value) throws IllegalAccessException {
        Double d = Double.parseDouble(value);
        field.setDouble(settings,d);
    }

    private void setInt(Field field, MainSettings settings, String value) throws IllegalAccessException {
        Integer i = Integer.parseInt(value);
        field.setInt(settings,i);
    }

    private void setBoolean(Field field, MainSettings settings, String value) throws IllegalAccessException {
        Boolean b = Boolean.parseBoolean(value);
        field.setBoolean(settings,b);
    }

    private void setTimePoint(Field field, MainSettings settings, String value) throws IllegalAccessException {
        Long l = Long.valueOf(value);
        TimePoint tp = new TimePoint(l);
        field.set(settings,tp);
    }

    private void setString(Field field, MainSettings settings, String value) throws IllegalAccessException {
        field.set(settings,value);
    }


    private MainAppData createData(Map<String, String> map, Application application) {
        String dataDir = Main.DEFAULT_DATA_DIR;
        if(map.containsKey(DATA_DIRECTORY_KEY)) {
            dataDir = map.get(DATA_DIRECTORY_KEY);
            map.remove(DATA_DIRECTORY_KEY);
        }
        return application.createData(dataDir);
    }

    private Map<String, String> buildFileMap() throws IOException {
        List<String> lines = buildLines();
        stripComments(lines);
        return createLinesMap(lines);
    }

    private Map<String,String> createLinesMap(List<String> lines) {
        Iterator<String> iterator = lines.iterator();
        Map<String,String> map = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\s*)(\\S+)(\\s+)(\\S+)");
        while(iterator.hasNext()) {
            String line = iterator.next();
            Matcher matcher = pattern.matcher(line);
            while(matcher.find()) {
                String key = matcher.group(2);
                checkDuplicateKey(map,key);
                map.put(key,matcher.group(4));
            }
        }
        return map;
    }

    private void checkDuplicateKey(Map<String, String> map, String key) {
        if(map.containsKey(key)) {
            throw new RuntimeException("Duplicate value in config file: " + key);
        }
    }

    private void stripComments(List<String> lines) {
        String commentDelimiter = "#";
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if(line.contains(commentDelimiter)) {
                String correct = line.split(commentDelimiter)[0];
                lines.set(i,correct);
            }
        }
    }

    private List<String> buildLines() throws IOException {
        List<String> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            while((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }
}
