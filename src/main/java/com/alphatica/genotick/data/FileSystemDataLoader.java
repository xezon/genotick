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
        MainAppData data = new MainAppData();
        String extension = ".csv";
        List<String> names = DataUtils.listFiles(extension,sources);
        if (names == null) {
            throw new DataException("Unable to list files");
        }
        for (String fileName : names) {
            DataSet dataSet = load(fileName);
            data.put(dataSet);
        }
        if (data.isEmpty()) {
            throw new DataException("No files to read");
        }
        return data;
    }
    
    @Override
    public DataSet load(String fileName) {
        output.infoMessage(format("Loading file '%s'", fileName));
        File file = new File(fileName);
        dataFileSanityCheck(file);
        DataLines dataLines = createDataLines(file);
        output.infoMessage(format("Read '%s' lines", dataLines.lineCount()));
        return createDataSet(fileName, dataLines);
    }

    private void dataFileSanityCheck(File file) {
        if (!file.isFile()) {
            throw new DataException(format("Unable to process file '%s' - not a file", file.getName()));
        }
    }
    
    private DataLines createDataLines(File file) throws DataException {
        try {
            return new DataLines(file, false);
        }
        catch (DataException ex) {
            // do nothing yet...
        }
        return createDataLinesSecondTry(file);
    }
    
    private DataLines createDataLinesSecondTry(File file) throws DataException {
        try {
            return new DataLines(file, true);
        }
        catch (DataException ex)
        {
            throw new DataException(format("Unable to process file '%s'", file.getAbsolutePath()), ex);
        }
    }
    
    private DataSet createDataSet(String fileName, DataLines dataLines) throws DataException {
        try {
            return new DataSet(fileName, dataLines);
        }
        catch (DataException ex) {
            throw new DataException(format("Unable to process file '%s'", fileName), ex);
        }
    }
}
