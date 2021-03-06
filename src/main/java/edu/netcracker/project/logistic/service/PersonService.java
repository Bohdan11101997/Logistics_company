package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Person;

import java.util.Optional;

public interface PersonService {
    void savePerson(Person person);

    void delete(Long aLong);

    Optional<Person> findOne(Long aLong);

    Optional<Person> findOne(String username);

    boolean exists(Long aLong);

    Optional<Person> findOneByEmail(String email);
}
