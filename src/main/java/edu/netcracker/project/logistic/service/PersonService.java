package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Person;

import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public interface PersonService {
    void savePerson(Person person);

    void delete(Long aLong);

    Optional<Person> findOne(Long aLong);

    Optional<Person> findOne(String username);

    boolean exists(Long aLong);
}
