package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import edu.netcracker.project.logistic.model.order.OrderContactData;
import edu.netcracker.project.logistic.processing.TaskProcessor;
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
    private TaskProcessor taskProcessor;

    @Autowired
    public UserServiceImpl(PersonService personService, PersonCrudDao personCrudDao,
                           ContactDao contactDao, TaskProcessor taskProcessor) {
        this.personService = personService;
        this.personCrudDao = personCrudDao;
        this.contactDao = contactDao;
        this.taskProcessor = taskProcessor;
    }

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

    @Override
    public void createOrder(OrderContactData order) {
        taskProcessor.createTask(order);
    }
}
