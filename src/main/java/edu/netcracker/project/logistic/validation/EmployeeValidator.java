package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import java.util.HashSet;
import java.util.Set;

@Component
public class EmployeeValidator extends AbstractPersonValidator {
    private SmartValidator validator;
    private ContactValidator contactValidator;

    @Autowired
    public EmployeeValidator(SmartValidator validator, RoleCrudDao roleDao, PersonCrudDao personDao, ContactValidator contactValidator) {
        super(roleDao, personDao);
        this.validator = validator;
        this.contactValidator = contactValidator;
    }

    private void checkRoleData(Person employee, Errors errors) {
        Set<Role> rolesCopy = new HashSet<>(employee.getRoles());
        Set<Role> employeeRoles = new HashSet<>(roleDao.findEmployeeRoles());
        rolesCopy.removeAll(employeeRoles);
        if (rolesCopy.size() != 0) {
            errors.rejectValue("person.roles", "", "Only employee roles can be set.");
        }
    }


    public void validateUpdateData(Person employee, Errors errors) {
        Set<Role> roles = employee.getRoles();
        if (roles == null || roles.size() < 1) {
            errors.rejectValue("roles", "Employee must have at least one role");
        }
        Contact contact = employee.getContact();
        validator.validate(contact, errors);
        contactValidator.validate(employee, errors, "contact");
    }

    public void validateCreateData(Person employee, Errors errors) {
        validator.validate(employee, errors);

        Contact contact = employee.getContact();
        validator.validate(contact, errors);

        Set<Role> roles = employee.getRoles();
        if (roles == null || roles.size() < 1) {
            errors.rejectValue("roles", "", "At least one role must be selected");
        }
        checkPersonData(employee, errors);
        contactValidator.validate(employee, errors, "contact");
        checkRoleData(employee, errors);
    }
}
