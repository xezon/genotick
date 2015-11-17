package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.Debug;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class YahooFixer {
    private final String path;

    public YahooFixer(String yahooValue) {
        this.path = yahooValue;
    }

    public void fixFiles() {
        String extension = ".csv";
        String [] names = DataUtils.listFiles(path, extension);
        if(names == null) {
            throw new DataException("Unable to list files in " + path);
        }
        for(String name: names) {
            fixFile(name);
        }
    }

    private void fixFile(String name) {
        Debug.d("Fixing file", name);
        List<List<Number>> originalList = buildOriginalList(name);
        Collections.reverse(originalList);
        List<List<Number>> newList = fixList(originalList);
        saveListToFile(newList,name);
    }

    private void saveListToFile(List<List<Number>> newList, String name) {
        String filePath = path + File.separator + name;
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath)))) {
            writeList(newList,bw);
        } catch (IOException e) {
            DataException dataException = new DataException("Unable to write file " + filePath);
            dataException.initCause(e);
            throw dataException;
        }
    }

    private void writeList(List<List<Number>> newList, BufferedWriter bw) throws IOException {
        for(List<Number> line: newList) {
            writeLine(line,bw);
        }
    }

    private void writeLine(List<Number> line, BufferedWriter bw) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Number> iterator = line.iterator();
        while(iterator.hasNext()) {
            Number number = iterator.next();
            stringBuilder.append(String.valueOf(number));
            if(iterator.hasNext())
                stringBuilder.append(",");
        }
        stringBuilder.append("\n");
        bw.append(stringBuilder.toString());
    }

    private List<List<Number>> fixList(List<List<Number>> originalList) {
        List<List<Number>> newList = new ArrayList<>(originalList.size());
        for (List<Number> line : originalList) {
            List<Number> fixedLine = fixLine(line);
            newList.add(fixedLine);
        }
        return newList;
    }

    private List<List<Number>> buildOriginalList(String name) {
        String filePath = path + File.separator + name;
        try(BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            ignoreFirstLine(br);
            return DataUtils.createLineList(br);
        } catch (IOException e) {
            DataException dataException = new DataException("Unable to read file " + filePath);
            dataException.initCause(e);
            throw dataException;
        }
    }

    /*
    This is how it works:
    0th number is time - so it's unchanged
    1st number is open: calculate difference from open to close. Use adjusted close (number at index 6)
        to calculate new value.
    The same for numbers 2 and 3.
    4th number - replace with adjusted close
    5th number - volume. Recalcute according to adjusted close
     */
    private List<Number> fixLine(List<Number> line) {
        List<Number> newLine = new ArrayList<>(line.size());
        double originalClose = line.get(4).doubleValue();
        double adjustedClose = line.get(6).doubleValue();
        // Nothing with to do be done with Date
        newLine.add(line.get(0));
        double open = calculateNew(line.get(1),originalClose,adjustedClose);
        newLine.add(open);
        double high = calculateNew(line.get(2),originalClose,adjustedClose);
        newLine.add(high);
        double low = calculateNew(line.get(3),originalClose,adjustedClose);
        newLine.add(low);
        // add adjusted close as 'close'
        newLine.add(adjustedClose);
        // recalcute volume
        double volumeValue = originalClose * line.get(5).doubleValue();
        double volumeCount = volumeValue / adjustedClose;
        newLine.add(volumeCount);
        return newLine;
    }

    private double calculateNew(Number number, double originalClose, double adjustedClose) {
        double change = number.doubleValue() / originalClose;
        return adjustedClose * change;
    }

    private void ignoreFirstLine(BufferedReader br) throws IOException {
        br.readLine();
    }

}
