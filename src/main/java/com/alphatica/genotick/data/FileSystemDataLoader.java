package com.alphatica.genotick.data;

import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class FileSystemDataLoader implements DataLoader {
    private final UserOutput output = UserInputOutputFactory.getUserOutput();
    
    public FileSystemDataLoader() {
    }

    @Override
    public MainAppData loadAll(String... sources) throws DataException {
        return loadData(sources);
    }

    private MainAppData loadData(String... paths) {
        MainAppData data = new MainAppData();
        String extension = ".csv";
        List<String> names = DataUtils.listFiles(extension,paths);
        if (names == null) {
            throw new DataException("Unable to list files");
        }
        for (String fileName : names) {
            DataSet dataSet = load(fileName);
            data.put(dataSet);
        }
        if (data.isEmpty()) {
            throw new DataException("No files to read!");
        }
        return data;
    }
    
    @Override
    public DataSet load(String fileName) {
        output.infoMessage(format("Loading file '%s'", fileName));
        File file = new File(fileName);
        dataFileSanityCheck(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<Number []> lines = DataUtils.createLineList(br);
            output.infoMessage(format("Read '%s' lines", lines.size()));
            return new DataSet(fileName, lines);
        } catch (IOException | DataException | AssertionError e) {
            DataException de = new DataException(format("Unable to process file '%s'", fileName));
            de.initCause(e);
            throw de;
        }
    }

    private void dataFileSanityCheck(File file) {
        if (!file.isFile()) {
            String message = String.format("Unable to process file '%s' - not a file.", file.getName());
            output.errorMessage(message);
            throw new DataException(message);
        }
    }
}
