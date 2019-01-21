package com.dansiwiec.connectionremider.persistance;

import java.io.File;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PersonsRepository {

    private final FileStorageHelper fileStorageHelper;

    public PersonsRepository(File filesDir) {
        this.fileStorageHelper = new FileStorageHelper(filesDir);
    }

    public MutableLiveData<List<String>> listAll() {
        final MutableLiveData<List<String>> data = new MutableLiveData<>();
        data.setValue(fileStorageHelper.readItems());
        return data;
    }

    public void save(String person) {
        List<String> persons = fileStorageHelper.readItems();
        persons.add(person);
        fileStorageHelper.writeItems(persons);
    }

    public void saveAll(List<String> persons) {
        fileStorageHelper.writeItems(persons);
    }
}
