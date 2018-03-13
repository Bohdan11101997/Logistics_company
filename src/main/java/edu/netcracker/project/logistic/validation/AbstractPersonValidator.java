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
    protected ContactDao contactDao;

    public AbstractPersonValidator(RoleCrudDao roleDao, PersonCrudDao personDao, ContactDao contactDao) {
        this.roleDao = roleDao;
        this.personDao = personDao;
        this.contactDao = contactDao;
    }

    protected void checkPersonData(Person person, Errors errors) {
        Optional<Person> opt = personDao.findOne(person.getUserName());
        if (opt.isPresent() &&
                !opt.get().getId().equals(person.getId())) {
            errors.rejectValue("userName", "Already exists.");
        }
    }

    protected void checkContactData(Person employee, Errors errors) {
        Person person = personDao.findOne(employee.getId()).orElse(employee);
        employee.getContact().setContactId(person.getContact().getContactId());

        String phoneNumber = employee.getContact().getPhoneNumber();
        phoneNumber = phoneNumber.replaceAll("[()-]", "").replaceAll("\\s+", "");
        employee.getContact().setPhoneNumber(phoneNumber);

        Contact contact = employee.getContact();
        List<Contact> duplicates =
                contactDao.findByPhoneNumberOrEmail(contact.getPhoneNumber(), contact.getEmail());
        for (Contact d : duplicates) {
            if (!d.getContactId().equals(contact.getContactId()) && d.getEmail().equals(contact.getEmail())) {
                errors.rejectValue("contact.email", "Already exists.");
            } else if (!d.getContactId().equals(contact.getContactId()) && d.getPhoneNumber().equals(contact.getPhoneNumber())) {
                errors.rejectValue("contact.phoneNumber", "Already exists.");
            }
        }

        String regex = "^\\+[1-9]{1}[0-9]{3,14}$";
        Matcher matcher = Pattern.compile(regex).matcher(contact.getPhoneNumber());
        if (!matcher.matches()){
            errors.rejectValue("contact.phoneNumber", "Incorrect.phone");
        }
    }
}
