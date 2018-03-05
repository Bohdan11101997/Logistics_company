package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Contact;

import java.util.Optional;

public interface ContactService {

    void saveContact(Contact contact);

    void delete(Long aLong);

    Optional<Contact> findOne(Long aLong);

    boolean exists(Long aLong);
}
