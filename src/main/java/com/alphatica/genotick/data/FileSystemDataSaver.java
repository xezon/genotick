package com.alphatica.genotick.data;

import static java.lang.String.format;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.alphatica.genotick.ui.UserOutput;

public class FileSystemDataSaver implements DataSaver {
    private final UserOutput output;
    
    public FileSystemDataSaver(UserOutput output) {
        this.output = output;
    }
    
    @Override
    public void saveAll(MainAppData data) {
        for (DataSet set : data.getDataSets()) {
            save(set);
        }
    }

    @Override
    public void save(DataSet set) {
        final String fileName = set.getName().getPath();
        final String lineSeparator = System.lineSeparator();
        final DataLines dataLines = set.getDataLinesCopy();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0, lineCount = dataLines.lineCount(); i < lineCount; ++i) {
                Number[] columns = dataLines.getColumnsCopy(i);
                String columnsString = makeString(columns, ",");
                bw.write(columnsString + lineSeparator);
            }
            if (output != null) {
                output.infoMessage(format("Saved data file '%s' successfully", fileName));
            }
        }
        catch (IOException e) {
            if (output != null) {
                output.errorMessage(format("Saving data file '%s' failed: %s", fileName, e.getMessage()));
            }
            e.printStackTrace();
        }
    }
    
    private static String makeString(Number[] rowNumbers, String string) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Number number : rowNumbers) {
            sb.append(number);
            count++;
            if (count < rowNumbers.length) {
                sb.append(string);
            }
        }
        return sb.toString();
    }
}
