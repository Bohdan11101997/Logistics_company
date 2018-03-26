package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.*;
import edu.netcracker.project.logistic.exception.NonUniqueRecordException;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@PropertySource("classpath:application.properties")
public class RegistrationServiceImpl implements RegistrationService {
    private final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private PersonCrudDao personDao;
    private RegistrationLinkDao registrationLinkDao;
    private RoleCrudDao roleDao;
    private ContactDao contactDao;
    private PasswordEncoder passwordEncoder;

    private JavaMailSender sender;
    private TemplateEngine templateEngine;
    private Environment env;
    private HttpServletRequest request;

    @Autowired
    public RegistrationServiceImpl(JavaMailSender sender,
                                   TemplateEngine templateEngine, Environment env,
                                   HttpServletRequest request, PersonCrudDao personDao,
                                   RegistrationLinkDao registrationLinkDao, RoleCrudDao roleDao,
                                   ContactDao contactDao, PasswordEncoder passwordEncoder) {
        this.personDao = personDao;
        this.registrationLinkDao = registrationLinkDao;
        this.roleDao = roleDao;
        this.contactDao = contactDao;
        this.sender = sender;
        this.templateEngine = templateEngine;
        this.env = env;
        this.request = request;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Person user) throws MessagingException, NonUniqueRecordException {
        user.setRegistrationDate(LocalDateTime.now());
        Role unconfirmedRole = roleDao.getByName("ROLE_UNCONFIRMED")
                .orElseThrow(() -> new IllegalStateException("Can't find 'Unconfired person' role"));

        user.setRoles(Collections.singleton(unconfirmedRole));
        contactDao.save(user.getContact());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        personDao.save(user);

        UUID id = generateId();
        RegistrationLink link = new RegistrationLink();
        link.setRegistrationLinkId(id);
        link.setPersonId(user.getId());
        registrationLinkDao.save(link);

        sendConfirmationRequest(user, link);
    }

    private void sendConfirmationRequest(Person user, RegistrationLink link) throws MessagingException {
        Contact contact = user.getContact();

        Context ctx = new Context();
        ctx.setVariable("firstName", contact.getFirstName());
        ctx.setVariable("lastName", contact.getLastName());
        ctx.setVariable("id", link.getRegistrationLinkId());

        String requestBaseAddress = request.getRequestURL().
                substring(0, request.getRequestURL().length() - request.getServletPath().length());

        String confirmationAddress = String.format(
                "%s/registration/confirm?id=%s",
                requestBaseAddress,
                link.getRegistrationLinkId()
        );
        ctx.setVariable("confirmUrl", confirmationAddress);

        String htmlContent = templateEngine.process(
                "mail/verify_email", ctx);

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        msg.setSubject("Verify email address");
        String from = env.getProperty("spring.mail.username");
        msg.setFrom(from);
        msg.setTo(contact.getEmail());
        msg.setText(htmlContent, true);

        sender.send(mimeMessage);
    }

    @Transactional
    public void confirmAccount(UUID linkId) {
        RegistrationLink data = registrationLinkDao.findOne(linkId).orElseThrow(
                () -> new IllegalArgumentException("Can't find user with given id")
        );

        Optional<Person> opt = personDao.findOne(data.getPersonId());
        if (!opt.isPresent()) {
            logger.error("Can't find person for link %s", data.getRegistrationLinkId());
            throw new IllegalStateException("Invalid registration link");
        }
        Person user = opt.get();

        List<Role> roles = roleDao.getByPersonId(data.getPersonId());
        boolean unconfirmedUser =
                roles
                        .stream()
                        .anyMatch(r -> r.getRoleName().equals("ROLE_UNCONFIRMED"));

        if (!unconfirmedUser) {
            logger.error("User #%s already confirmed account", user.getId());
            throw new IllegalStateException("Account already confirmed");
        }

        Role clientRole = roleDao.getByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Can't find 'User' role"));

        user.setRoles(Collections.singleton(clientRole));
        personDao.save(user);
    }

    private UUID generateId() {
        UUID id = UUID.randomUUID();
        while (registrationLinkDao.contains(id)) {
            id = UUID.randomUUID();
        }
        return id;
    }
}
