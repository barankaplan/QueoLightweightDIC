package com.queo.services;

import com.queo.enums.DirectoryType;
import com.queo.models.ModelForDirectory;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class DirectoryResolverImpl  {


    public ModelForDirectory resolveDirectory(Class<?> startupClass) {
        final String directory = this.getDirectory(startupClass);

        return new ModelForDirectory(directory, this.getDirectoryType(directory));
    }

    public ModelForDirectory resolveDirectory(File directory) {
        try {
            return new ModelForDirectory(directory.getCanonicalPath(), this.getDirectoryType(directory.getCanonicalPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDirectory(Class<?> cls) {
        return URLDecoder.decode(cls.getProtectionDomain().getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8);
    }


    private DirectoryType getDirectoryType(String directory) {
        return DirectoryType.DIRECTORY;
    }
}
