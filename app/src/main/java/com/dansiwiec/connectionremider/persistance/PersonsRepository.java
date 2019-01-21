package com.dansiwiec.connectionremider.persistance;

import java.util.List;

interface PersonsRepository {

    List<String> listAll();
    void save(String person);
    void saveAll(List<String> persons);
}
