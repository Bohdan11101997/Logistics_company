package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {

    private PersonCrudDao personDao;
    private ContactValidator contactValidator;

    @Autowired
    public PersonValidator(PersonCrudDao personDao, ContactValidator contactValidator) {
        this.personDao = personDao;
        this.contactValidator = contactValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;
        checkDuplicates(person, errors);
        contactValidator.validate(person.getContact(), errors, "contact");
    }

    private void checkDuplicates(Person person, Errors errors) {
        Optional<Person> opt = personDao.findOne(person.getUserName());
        if (opt.isPresent() &&
                !opt.get().getId().equals(person.getId())) {
            errors.rejectValue("userName", "Duplicate.username");
        }
    }
}
