package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.Debug;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSystemDataLoader implements DataLoader {
    private final String path;
    private final String extension = ".csv";

    public FileSystemDataLoader(String args) {
        path = args;
    }

    @Override
    public MainAppData createProgramData() throws DataException {
        return loadData();
    }

    private MainAppData loadData() {
        MainAppData data = new MainAppData();
        String[] names = DataUtils.listFiles(path,extension);
        if(names == null) {
            throw new DataException("Unable to list files in " + path);
        }
        for (String name : names) {
            Debug.d("Reading file", name);
            data.addDataSet(createDataSet(name));
        }
        return data;

    }
    private DataSet createDataSet(String name) {
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path + File.separator + name)))) {
            List<List<Number>> lines = DataUtils.createLineList(br);
            Debug.d("Got",lines.size(),"lines");
            return new DataSet(lines,name);
        } catch (IOException  | DataException e) {
            DataException de = new DataException("Unable to process file: " + name);
            de.initCause(e);
            throw de;
        }
    }
}


