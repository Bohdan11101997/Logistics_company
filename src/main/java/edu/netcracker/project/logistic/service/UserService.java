package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.order.OrderContactData;

import java.util.Optional;

public interface UserService {
    Person update(Person person);
    void delete(Long id);
    Optional<Person> findOne(Long id);
    Optional<Person> findOne(String id);
    void createOrder(OrderContactData order);
}
