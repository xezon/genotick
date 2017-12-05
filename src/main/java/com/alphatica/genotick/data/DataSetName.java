package com.alphatica.genotick.data;

import org.apache.commons.io.FilenameUtils;

import java.io.Serializable;

import static com.alphatica.genotick.utility.Assert.gassert;

public class DataSetName implements Serializable {
    
    private static final long serialVersionUID = -7504682119928833833L;
    public static final String REVERSE_DATA_IDENTIFIER = "reverse_";
    private final String path;
    private final String name;
    private final boolean isReversed;
    
    public DataSetName(String path) {
        gassert(!path.isEmpty());
        boolean isFilename = !FilenameUtils.getExtension(path).isEmpty();
        this.path = path;
        this.name = isFilename ? FilenameUtils.getBaseName(path) : path;
        this.isReversed = name.startsWith(REVERSE_DATA_IDENTIFIER);
    }

    @Override
    public String toString() {
        return path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
    
    public boolean isReversed() {
        return isReversed;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        return path.equals(((DataSetName)other).path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
