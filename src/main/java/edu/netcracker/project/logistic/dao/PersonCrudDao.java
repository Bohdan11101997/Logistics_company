package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.SearchForm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonCrudDao extends CrudDao<Person, Long> {
    Optional<Person> findOne(String username);

    Optional<Person> findByContactId(Long contactId);

    List<Person> findAll();

    List<Person> findAllEmployees();

    List<Person> findByRoleId(Long roleId);

    List<Person> search(SearchForm searchForm);
}
