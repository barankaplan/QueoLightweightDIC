package com.queo.models;

import com.queo.enums.DirectoryType;


public class ModelForDirectory {


    private final String directory;


    private final DirectoryType directoryType;

    public ModelForDirectory(String directory, DirectoryType directoryType) {
        this.directory = directory;
        this.directoryType = directoryType;
    }

    public String getDirectory() {
        return this.directory;
    }

    public DirectoryType getDirectoryType() {
        return this.directoryType;
    }
}
