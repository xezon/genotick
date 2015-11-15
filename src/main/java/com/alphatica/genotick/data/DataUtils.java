package com.alphatica.genotick.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class DataUtils {

    public static String[] listFiles(final String path, final String extension) {
        return new File(path).list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(extension);
            }
        });
    }

    public static List<List<Number>> createLineList(BufferedReader br) {
        List<List<Number>> list = new ArrayList<>();
        int linesRead = 1;
        try {
            String line;
            while ((line = br.readLine())!=null){
                List<Number> lineList = processLine(line);
                list.add(lineList);
                linesRead++;
            }
            return list;
        } catch(IOException | NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            DataException de = new DataException("Error reading line " + linesRead);
            de.initCause(ex);
            throw de;
        }
    }


    public static List<Number> processLine(String line) {
        String separator = ",";
        String[] fields = line.split(separator);
        List<Number> list = new ArrayList<>(fields.length);
        String timePointString = getTimePointString(fields[0]);
        list.add(Long.valueOf(timePointString));
        for(int i = 1; i < fields.length; i++) {
            list.add(Double.valueOf(fields[i]));
        }
        return list;
    }

    public static String getTimePointString(String field) {
        if(field.contains("-"))
            return field.replaceAll("-","");
        else
            return field;

    }

    public static String getDateTimeString() {
        DateFormat format = new SimpleDateFormat("yyyy_MM_dd_kk_mm");
        return format.format(Calendar.getInstance().getTime());
    }
}
