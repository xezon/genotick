package com.alphatica.genotick.data;

import org.apache.commons.io.FilenameUtils;

import java.io.Serializable;

import static com.alphatica.genotick.utility.Assert.gassert;

public class DataSetName implements Serializable {
    private static final long serialVersionUID = -7504682119928833833L;
    private final String path;
    private final String name;
    public DataSetName(String path) {
        gassert(!path.isEmpty());
        this.path = path;
        this.name = FilenameUtils.getBaseName(path);
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
