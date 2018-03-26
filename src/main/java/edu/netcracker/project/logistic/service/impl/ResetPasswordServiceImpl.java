package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.ResetPasswordTokenDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.ResetPasswordToken;
import edu.netcracker.project.logistic.service.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Service
@PropertySource("classpath:mail.properties")
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private PersonCrudDao personService;
    private ResetPasswordTokenDao resetPasswordTokenDao;
    private HttpServletRequest request;
    private Environment env;
    private JavaMailSender sender;

    @Autowired
    public ResetPasswordServiceImpl(PersonCrudDao personService, ResetPasswordTokenDao resetPasswordTokenDao, HttpServletRequest request, Environment env, JavaMailSender sender) {
        this.personService = personService;
        this.resetPasswordTokenDao = resetPasswordTokenDao;
        this.request = request;
        this.env = env;
        this.sender = sender;
    }

    @Transactional
    @Override
    public void generateAndSendOnEmailResetToken(String email) throws MessagingException {
        Optional<Person> optionalPerson = personService.findOneByEmail(email);

        if (!optionalPerson.isPresent()){
            throw new IllegalArgumentException("Can't find user with given email");
        }

        Person person = optionalPerson.get();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setPerson(person);

        // generate reset_token
        String resetToken = UUID.randomUUID().toString();
        resetPasswordToken.setResetToken(resetToken);

        // save to database
        resetPasswordTokenDao.save(resetPasswordToken);

        // send it in e-mail
        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        msg.setSubject("Verify email address");
        String from = env.getProperty("spring.mail.username");
        msg.setFrom(from);
        msg.setTo(resetPasswordToken.getPerson().getContact().getEmail());
        msg.setText("To reset your password, click the link below:\n" + appUrl
                + "/password/reset?token=" + resetPasswordToken.getResetToken(), false);

        sender.send(mimeMessage);

    }

    @Override
    public Optional<ResetPasswordToken> findResetPasswordTokenByResetToken(String resetToken) {
        return resetPasswordTokenDao.findOneByToken(resetToken);
    }

    @Transactional
    @Override
    public void deleteResetPasswordTokenRowById(Long personId) {
        resetPasswordTokenDao.delete(personId);
    }


}
