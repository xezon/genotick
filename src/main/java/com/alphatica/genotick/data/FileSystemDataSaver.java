package com.alphatica.genotick.data;

import static java.lang.String.format;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

public class FileSystemDataSaver implements DataSaver {
    private final UserOutput output = UserInputOutputFactory.getUserOutput();
    
    @Override
    public void saveAll(MainAppData data) {
        for (DataSet set : data.getDataSets()) {
            save(set);
        }
    }

    @Override
    public void save(DataSet set) {
        final String fileName = set.getName().getPath();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < set.getLinesCount(); ++i) {
                Number[] row = set.getLine(i);
                String rowString = makeString(row, ",");
                bw.write(rowString + "\n");
            }
            output.infoMessage(format("Saved data file '%s' successfully", fileName));
        }
        catch (IOException e) {
            output.errorMessage(format("Saving data file '%s' failed: %s", fileName, e.getMessage()));
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
