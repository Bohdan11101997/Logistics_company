package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.PersonRoleDao;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.exception.NonUniqueRecordException;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private ContactDao contactDao;
    private PersonCrudDao personDao;
    private PersonRoleDao personRoleDao;
    private RoleCrudDao roleDao;

    @Autowired
    public EmployeeServiceImpl(ContactDao contactDao, PersonCrudDao personDao,
                               PersonRoleDao personRoleDao, RoleCrudDao roleDao) {
        this.contactDao = contactDao;
        this.personDao = personDao;
        this.personRoleDao = personRoleDao;
        this.roleDao = roleDao;
    }

    @Transactional(rollbackFor = {NonUniqueRecordException.class, DataIntegrityViolationException.class})
    @Override
    public Person create(Person employee) {
        employee.setRegistrationDate(LocalDateTime.now());
        contactDao.save(employee.getContact());
        personDao.save(employee);
        return employee;
    }

    @Override
    public Person update(Person employee) {
        Long personId = employee.getId();
        Optional<Person> opt = personDao.findOne(personId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException(String.format("Can't find person #%s", personId));
        }
        Person existing = opt.get();
        Long contactId = existing.getContact().getContactId();
        Contact updatedContact = existing.getContact();
        updatedContact.setContactId(contactId);
        contactDao.save(updatedContact);
        return employee;
    }

    @Transactional
    @Override
    public void delete(Long employeeId) {
        boolean hasUserRole = false;

        List<Role> roles = roleDao.getByPersonId(employeeId);
        List<PersonRole> employeeRoles = new ArrayList<>();

        for (Role r : roles) {
            if (!r.isEmployeeRole()) {
                hasUserRole = true;
                continue;
            }
            PersonRole pr = new PersonRole();
            pr.setRoleId(r.getRoleId());
            pr.setPersonId(employeeId);
            employeeRoles.add(pr);
        }
        personRoleDao.deleteMany(employeeRoles);
        if (!hasUserRole) personDao.delete(employeeId);
    }

    @Override
    public Optional<Person> findOne(Long id) {
        boolean hasEmployeeRoles = roleDao.getByPersonId(id)
                .stream()
                .anyMatch(Role::isEmployeeRole);

        if (!hasEmployeeRoles) {
            return Optional.empty();
        }

        return personDao.findOne(id);
    }

    @Override
    public Optional<Person> findOne(String userName) {
        Optional<Person> opt =  personDao.findOne(userName);
        if (!opt.isPresent()) {
            return Optional.empty();
        }
        Person emp = opt.get();
        for (Role r: emp.getRoles()) {
            if (r.isEmployeeRole()) {
                return opt;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Person> findAll() {
        return personDao.findAllEmployees();
    }

    @Override
    public boolean contains(Long id) {
        return findOne(id).isPresent();
    }

    @Override
    public List<Person> search(SearchForm searchForm) {
        Set<Long> availableRoleIds =
                roleDao
                        .findEmployeeRoles()
                        .stream()
                        .map(Role::getRoleId)
                        .collect(Collectors.toSet());
        List<Long> searchRoleIds = (searchForm.getRoleIds() != null) ? searchForm.getRoleIds() : new ArrayList<>();
        // Leave only employee roles
        for (Long id : searchRoleIds) {
            if (!availableRoleIds.contains(id)) {
                searchRoleIds.remove(id);
            }
        }
        if (searchRoleIds.isEmpty()) {
            searchRoleIds.addAll(availableRoleIds);
        }
        searchForm.setRoleIds(searchRoleIds);
        return personDao.search(searchForm);
    }
}
