package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.*;

@Component
public class RegistrationValidator {
    private ContactValidator contactValidator;
    private RoleCrudDao roleDao;
    private PersonCrudDao personDao;


    @Autowired
    public RegistrationValidator(PersonCrudDao personDao, RoleCrudDao roleDao, ContactValidator contactValidator) {
        this.personDao = personDao;
        this.roleDao = roleDao;
        this.contactValidator = contactValidator;
    }

    public void validate(Person user, Errors errors) {
        validateUserName(user, errors);
        contactValidator.validate(user.getContact(), errors, "contact");
        checkRoleData(user, errors);
    }

    private void validateUserName(Person user, Errors errors) {
        Optional<Person> opt = personDao.findOne(user.getUserName());
        if (opt.isPresent() &&
                !opt.get().getId().equals(user.getId())) {
            errors.rejectValue("userName", "Duplicate.username");
        }
    }

    private void checkRoleData(Person user, Errors errors) {
        List<Role> clientRoles =  roleDao.findClientRoles();
        Set<Role> userRoles = user.getRoles() != null ? user.getRoles() : Collections.emptySet();

        Set<Role> userRolesCopy = new HashSet<>(userRoles);
        userRolesCopy.removeAll(clientRoles);

        if (userRolesCopy.size() != 0) {
            errors.rejectValue("roles", "Invalid.Roles");
        }
    }
}
