package com.dansiwiec.connectionremider.persistance;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersonsFileRepository implements PersonsRepository {

    private static final String FILENAME = "list.txt";
    private File filesDir;

    public PersonsFileRepository(File filesDir) {
        this.filesDir = filesDir;
    }

    @Override
    public List<String> listAll() {
        File listFile = new File(this.filesDir, FILENAME);
        try {
            return new ArrayList<String>(FileUtils.readLines(listFile));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void save(String person) {
        List<String> persons = listAll();
        persons.add(person);
        writeItems(persons);
    }

    @Override
    public void saveAll(List<String> persons) {
        writeItems(persons);
    }

    private void writeItems(List<String> items) {
        File listFile = new File(this.filesDir, FILENAME);
        try {
            FileUtils.writeLines(listFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
