package com.alphatica.genotick.data;

import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.io.File;
import java.util.List;

public class YahooFixer {
    private final String path;
    private final UserOutput output = UserInputOutputFactory.getUserOutput();

    public YahooFixer(String yahooValue) {
        this.path = yahooValue;
    }

    public void fixFiles() {
        String extension = ".csv";
        List<String> names = DataUtils.listFiles(extension,path);
        for (String name : names) {
            fixFile(name);
        }
    }

    private void fixFile(String name) {
        output.infoMessage("Fixing file: " + name);
        DataLines dataLines = new DataLines(new File(name), false);
        DataLines fixedDataLines = getFixedLines(dataLines);
        DataSaver dataSaver = DataFactory.getDefaultSaver();
        dataSaver.save(new DataSet(name, fixedDataLines));
    }

    private DataLines getFixedLines(DataLines dataLines) {
        final int lineCount = dataLines.lineCount();
        final int columnCount = dataLines.tohlcColumnCount();
        final DataLines fixedDataLines = new DataLines(lineCount, columnCount, dataLines.firstLineIsNewest());
        for (int line = 0; line < lineCount; ++line) {
            final Number[] columns = dataLines.getColumnsCopy(line);
            fixColumns(columns);
            dataLines.setColumns(line, columns);
        }
        return fixedDataLines;
    }

    /*
    This is how it works:
    0th number is time - so it's unchanged
    1st number is open: calculate difference from open to close. Use adjusted close (number at index 6)
        to calculate new value.
    The same for numbers 2 and 3.
    4th number - replace with adjusted close
    5th number - volume. Recalculate according to adjusted close
     */
    private void fixColumns(Number[] columns) {
        double originalClose = columns[4].doubleValue();
        double adjustedClose = columns[6].doubleValue();
        double volumeValue = originalClose * columns[5].doubleValue();
        double volumeCount = volumeValue / adjustedClose;
        double open = calculateNew(columns[1],originalClose,adjustedClose);
        double high = calculateNew(columns[2],originalClose,adjustedClose);
        double low  = calculateNew(columns[3],originalClose,adjustedClose);
        columns[1] = open;
        columns[2] = high;
        columns[3] = low;
        columns[4] = adjustedClose;
        columns[5] = volumeCount;
    }

    private double calculateNew(Number number, double originalClose, double adjustedClose) {
        double change = number.doubleValue() / originalClose;
        return adjustedClose * change;
    }
}
