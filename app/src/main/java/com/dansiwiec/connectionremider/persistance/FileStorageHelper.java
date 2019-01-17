package com.dansiwiec.connectionremider.persistance;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStorageHelper {

    private File fileDirectory;
    private String filename = "list.txt";;

    public FileStorageHelper(File fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public List<String> readItems() {
        File listFile = new File(fileDirectory, filename);
        try {
            return new ArrayList<String>(FileUtils.readLines(listFile));
        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }

    public void writeItems(List<String> items) {
        File listFile = new File(fileDirectory, filename);
        try {
            FileUtils.writeLines(listFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
