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
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
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
    private Environment env;
    private JavaMailSender sender;

    @Autowired
    public EmployeeServiceImpl(ContactDao contactDao, PersonCrudDao personDao,
                               PersonRoleDao personRoleDao, RoleCrudDao roleDao,
                               Environment env, JavaMailSender sender) {
        this.contactDao = contactDao;
        this.personDao = personDao;
        this.personRoleDao = personRoleDao;
        this.roleDao = roleDao;
        this.env = env;
        this.sender = sender;
    }

    @Transactional(rollbackFor = {NonUniqueRecordException.class, DataIntegrityViolationException.class, MessagingException.class})
    @Override
    public Person create(Person employee) throws MessagingException {
        employee.setRegistrationDate(LocalDateTime.now());

        contactDao.save(employee.getContact());

        String temporaryPassword = generateRandomPasswordWithSpecifiedLength(12);
        // password encoder encode!!!
        employee.setPassword(temporaryPassword);
        personDao.save(employee);

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        msg.setSubject("Successful registration!");
        String from = env.getProperty("spring.mail.username");
        msg.setFrom(from);
        msg.setTo(employee.getContact().getEmail());
        msg.setText("You have been registered in our logistic company service!\n" +
                "You can login with credentials below.\n" +
                "Username: " + employee.getUserName() + "\n" +
                "Password: " + temporaryPassword + "\n" +
                "We recommend you log in and change temporary password!", false);

        sender.send(mimeMessage);

        return employee;
    }

    private String generateRandomPasswordWithSpecifiedLength(int passwordLength){

        final String allowedSymbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < passwordLength; i++ )
            sb.append( allowedSymbols.charAt( rnd.nextInt(allowedSymbols.length()) ) );
        return sb.toString();
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
        Contact updatedContact = employee.getContact();
        updatedContact.setContactId(contactId);
        existing.setRoles(employee.getRoles());
        contactDao.save(updatedContact);
        personDao.save(existing);
        return existing;
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
    public List<Person> findCallCenterAgents() {
        Optional<Role> callCentreAgent = roleDao.getByName("ROLE_CALL_CENTER");
        if (!callCentreAgent.isPresent()) {
            String errorMsg = "Role for call center agents not found";
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        return personDao.findByRoleId(callCentreAgent.get().getRoleId());
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
