package com.dansiwiec.connectionremider;

import com.dansiwiec.connectionremider.persistance.PersonsFileRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PersonsViewModel extends ViewModel {

    private MutableLiveData<List<String>> persons;
    private PersonsFileRepository repository;

    public PersonsViewModel(PersonsFileRepository repository) {
        this.repository = repository;
    }

    public void init() {
        final MutableLiveData<List<String>> data = new MutableLiveData<>();
        data.setValue(repository.listAll());
        this.persons = data;
    }

    public void add(String person) {
        persons.getValue().add(person);
        repository.save(person);
        persons.setValue(persons.getValue());
    }

    public LiveData<List<String>> list() {
        return persons;
    }

    public void remove(String person) {
        persons.getValue().remove(person);
        repository.saveAll(persons.getValue());
        persons.setValue(persons.getValue());
    }

    public void pushToBottom(int position) {
        String item = persons.getValue().get(position);
        if (persons.getValue().contains(item)) {
            persons.getValue().remove(position);
            persons.getValue().add(item);
            repository.saveAll(persons.getValue());
            persons.setValue(persons.getValue());
        }
    }
}
