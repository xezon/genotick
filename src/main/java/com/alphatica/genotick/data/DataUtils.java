package com.alphatica.genotick.data;

import com.alphatica.genotick.ui.UserInputOutputFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@SuppressWarnings("WeakerAccess")
public class DataUtils {

    public static List<String> listFiles(final String extension, final String... paths) {
        List<String> list = new ArrayList<>();
        for(String path: paths) {
            list.addAll(namesFromPath(path,extension));
        }
        return list;
    }

    private static List<String> namesFromPath(String path, final String extension) {
        List<String> list = new ArrayList<>();
        File file = new File(path);
        if(file.isDirectory()) {
            list.addAll(getFullPaths(path,extension));
        } else {
            list.add(path);
        }
        return list;
    }

    private static List<String> getFullPaths(String path, final String extension) {
        File directory = new File(path);
        String[] names = getFilesNames(extension, directory);
        List<String> list = new ArrayList<>();
        for(String name: names) {
            list.add(path + File.separator + name);
        }
        return list;
    }

    private static String[] getFilesNames(String extension, File directory) {
        String[] list = directory.list((dir, name) -> name.endsWith(extension));
        if (isNull(list)) {
            UserInputOutputFactory.getUserOutput().errorMessage("Unable to list files ");
            throw new DataException("Unable to list files in " + directory.getAbsolutePath());
        }
        return list;
    }
}
