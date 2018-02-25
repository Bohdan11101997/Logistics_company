package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.exception.NonUniqueRecordException;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Person create(Person employee);
    Person update(Person employee);
    void delete(Long id);
    Optional<Person> findOne(Long id);
    List<Person> findAll();
    boolean contains(Long id);
}
