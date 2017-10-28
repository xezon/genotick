package com.alphatica.genotick.data;

import com.alphatica.genotick.ui.UserOutput;

import java.io.File;
import java.util.List;

import static java.lang.String.format;

public class FileSystemDataLoader implements DataLoader {
    
    private final UserOutput output;
    
    public FileSystemDataLoader(UserOutput output) {
        this.output = output;
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
        try {
            DataLines dataLines = new DataLines(file, false);
            output.infoMessage(format("Read '%s' lines", dataLines.lineCount()));
            return new DataSet(fileName, dataLines);
        }
        catch (DataException e) {
            throw new DataException(format("Unable to process file '%s'", fileName), e);
        }
    }

    private void dataFileSanityCheck(File file) {
        if (!file.isFile()) {
            String message = format("Unable to process file '%s' - not a file.", file.getName());
            output.errorMessage(message);
            throw new DataException(message);
        }
    }
}
