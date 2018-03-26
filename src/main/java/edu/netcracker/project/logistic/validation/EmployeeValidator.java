package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.HashSet;
import java.util.Set;

@Component
public class EmployeeValidator {
    private ContactValidator contactValidator;
    private RoleCrudDao roleDao;
    private PersonCrudDao personDao;
    private PersonValidator personValidator;

    @Autowired
    public EmployeeValidator(ContactValidator contactValidator, RoleCrudDao roleDao,
                             PersonCrudDao personDao, PersonValidator personValidator) {
        this.contactValidator = contactValidator;
        this.roleDao = roleDao;
        this.personDao = personDao;
        this.personValidator = personValidator;
    }

    private void checkRoleData(Person employee, Errors errors) {
        Set<Role> rolesCopy = new HashSet<>(employee.getRoles());
        Set<Role> employeeRoles = new HashSet<>(roleDao.findEmployeeRoles());
        rolesCopy.removeAll(employeeRoles);
        if (rolesCopy.size() != 0) {
            errors.rejectValue("person.roles", "Invalid.Roles");
        }
    }


    public void validateUpdateData(Person employee, Errors errors) {
        Set<Role> roles = employee.getRoles();
        if (roles == null || roles.size() < 1) {
            errors.rejectValue("roles", "Required.Roles");
        }
        personValidator.validate(employee, errors);
    }

    public void validateCreateData(Person employee, Errors errors) {
        Set<Role> roles = employee.getRoles();
        if (roles == null || roles.size() < 1) {
            errors.rejectValue("roles", "Required.Roles");
        }
        contactValidator.validate(employee, errors, "contact");
        personValidator.validate(employee, errors);
        checkRoleData(employee, errors);
    }
}
