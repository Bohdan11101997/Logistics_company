package edu.netcracker.project.logistic.service.impl;


import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.ContactService;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {
    private ContactDao contactDao;

    @Autowired
    public ContactServiceImpl(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Override
    public void save(Contact contact) {
        contactDao.save(contact);
    }

    @Override
    public void delete(Long aLong) {
          contactDao.delete(aLong);
    }

    @Override
    public Optional<Contact> findOne(Long aLong) {
        return contactDao.findOne(aLong);
    }

    @Override
    public boolean exists(Long aLong) {
        return contactDao.contains(aLong);
    }
}
