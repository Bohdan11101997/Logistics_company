package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.*;
import edu.netcracker.project.logistic.exception.NonUniqueRecordException;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.EmployeeService;
import edu.netcracker.project.logistic.service.MessageService;
import edu.netcracker.project.logistic.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private ContactDao contactDao;
    private PersonCrudDao personDao;
    private PersonRoleDao personRoleDao;
    private RoleCrudDao roleDao;
    private PersonService personService;
    private MessageService messageService;
    private WorkDayDao workDayDao;

    @Autowired
    public EmployeeServiceImpl(ContactDao contactDao, PersonCrudDao personDao,
                               PersonRoleDao personRoleDao, RoleCrudDao roleDao,
                               MessageService messageService, WorkDayDao workDayDao) {
        this.contactDao = contactDao;
        this.personDao = personDao;
        this.personRoleDao = personRoleDao;
        this.roleDao = roleDao;
        this.messageService = messageService;
        this.workDayDao = workDayDao;
    }

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Transactional(rollbackFor = {NonUniqueRecordException.class, DataIntegrityViolationException.class, MessagingException.class})
    @Override
    public Person create(Person employee) throws MessagingException {
        employee.setRegistrationDate(LocalDateTime.now());

        addUserRoleToPerson(employee);

        contactDao.save(employee.getContact());
        String temporaryPassword = employee.getPassword();
        personService.savePerson(employee);

        List<WorkDay> workDays = new ArrayList<>();
        workDays.add(new WorkDay(employee.getId(), WeekDay.MONDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)));
        workDays.add(new WorkDay(employee.getId(), WeekDay.TUESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)));
        workDays.add(new WorkDay(employee.getId(), WeekDay.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)));
        workDays.add(new WorkDay(employee.getId(), WeekDay.THURSDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)));
        workDays.add(new WorkDay(employee.getId(), WeekDay.FRIDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)));
        workDayDao.saveMany(workDays);

        String email = employee.getContact().getEmail();
        String username = employee.getUserName();
        sendMessageWithNewCredentialsOnMail(email, username, temporaryPassword);

        return employee;
    }

    private void addUserRoleToPerson(Person person){
        Optional<Role> optionalUserRole = roleDao.getByName("ROLE_USER");
        if (!optionalUserRole.isPresent()){
            logger.error("No role with name ROLE_USER");
        }

        Role userRole = optionalUserRole.get();
        person.getRoles().add(userRole);
    }

    private void sendMessageWithNewCredentialsOnMail(String email, String username, String temporaryPassword) {

        Message message = new Message();
        String subject = "Successful registration!";
        message.setSubject(subject);
        String to = email;
        message.setTo(to);
        String text = "You have been registered in our logistic company service!\n" +
                "You can login with credentials below.\n" +
                "Username: " + username + "\n" +
                "Password: " + temporaryPassword + "\n" +
                "We recommend you log in and change temporary password!";
        message.setText(text);

        messageService.sendMessage(message);
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
    public List<Person> findCouriers() {
        Optional<Role> courier = roleDao.getByName("ROLE_COURIER");
        if (!courier.isPresent()) {
            String errorMsg = "Role for couriers not found";
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        return personDao.findByRoleId(courier.get().getRoleId());
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
