package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import org.springframework.validation.Errors;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractPersonValidator {
    protected RoleCrudDao roleDao;
    protected PersonCrudDao personDao;

    public AbstractPersonValidator(RoleCrudDao roleDao, PersonCrudDao personDao) {
        this.roleDao = roleDao;
        this.personDao = personDao;
    }

    protected void checkPersonData(Person person, Errors errors) {
        Optional<Person> opt = personDao.findOne(person.getUserName());
        if (opt.isPresent() &&
                !opt.get().getId().equals(person.getId())) {
            errors.rejectValue("userName", "Already exists.");
        }
    }
}
