package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.*;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private PersonService personService;
    private PersonCrudDao personCrudDao;
    private ContactDao contactDao;
    private AddressDao addressDao;
    private OrderDao orderDao;
    private OrderStatusDao orderStatusDao;
    private TaskProcessor taskProcessor;

    @Autowired
    public UserServiceImpl(PersonService personService, PersonCrudDao personCrudDao,
                           ContactDao contactDao, TaskProcessor taskProcessor,
                           AddressDao addressDao, OrderDao orderDao, OrderStatusDao orderStatusDao) {
        this.personService = personService;
        this.personCrudDao = personCrudDao;
        this.contactDao = contactDao;
        this.taskProcessor = taskProcessor;
        this.addressDao = addressDao;
        this.orderDao = orderDao;
        this.orderStatusDao = orderStatusDao;
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
    public void createOrder(Order order) {
        Address senderAddress = order.getSenderAddress();
        Address receiverAddress = order.getReceiverAddress();
        addressDao.save(senderAddress);
        addressDao.save(receiverAddress);
        contactDao.save(order.getReceiverContact());
        if (order.getWeight() == null) {
            order.setWeight(new BigDecimal(1));
        }
        LocalDateTime now = LocalDateTime.now();
        order.setCreationTime(now);
        order.setOrderStatusTime(now);
        OrderStatus processing = orderStatusDao.findByName("PROCESSING")
                    .orElseThrow(() -> new IllegalStateException("Can't find order status 'PROCESSING'"));
        order.setOrderStatus(processing);
        orderDao.save(order);
        taskProcessor.createTask(order);
    }
}
