package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.*;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private PersonService personService;
    private PersonCrudDao personCrudDao;
    private ContactDao contactDao;

    @Autowired
    public UserServiceImpl(PersonService personService, PersonCrudDao personCrudDao,
                           ContactDao contactDao) {
        this.personService = personService;
        this.personCrudDao = personCrudDao;
        this.contactDao = contactDao; }

    @Override
    public Person update(Person person) {
        personCrudDao.save(person);
        Contact contact = person.getContact();
        contactDao.save(contact);
        return person;
    }

    @Override
    public void delete(Long id) {
        personService.delete(id);
    }

    @Override
    public Optional<Person> findOne(Long id) {
        return personService.findOne(id);
    }

    @Override
    public Optional<Person> findOne(String username) {
        return personService.findOne(username);
    }
}
