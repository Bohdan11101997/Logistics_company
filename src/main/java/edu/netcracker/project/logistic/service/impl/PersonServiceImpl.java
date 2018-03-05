package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    private PasswordEncoder passwordEncoder;
    private PersonCrudDao personCrudDao;

    @Autowired
    public PersonServiceImpl(PersonCrudDao personCrudDao, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.personCrudDao = personCrudDao;
    }

    @Override
    public void savePerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personCrudDao.save(person);
    }

    @Override
    public void delete(Long aLong) {
        personCrudDao.delete(aLong);
    }

    @Override
    public Optional<Person> findOne(Long aLong) {
        return personCrudDao.findOne(aLong);
    }

    @Override
    public Optional<Person> findOne(String username) {
        return personCrudDao.findOne(username);
    }

    @Override
    public boolean exists(Long aLong) {
        return personCrudDao.contains(aLong);
    }
}
