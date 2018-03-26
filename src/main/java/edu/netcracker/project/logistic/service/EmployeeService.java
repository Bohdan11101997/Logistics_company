package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.SearchForm;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Person create(Person employee) throws MessagingException;
    Person update(Person employee);
    void delete(Long id);
    Optional<Person> findOne(Long id);
    Optional<Person> findOne(String userName);
    List<Person> findAll();
    List<Person> findCallCenterAgents();
    List<Person> findCouriers();
    boolean contains(Long id);
    List<Person> search(SearchForm searchForm);
}
