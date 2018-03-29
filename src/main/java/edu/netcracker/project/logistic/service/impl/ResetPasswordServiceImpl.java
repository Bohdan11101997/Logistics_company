package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.ResetPasswordTokenDao;
import edu.netcracker.project.logistic.model.Message;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.ResetPasswordToken;
import edu.netcracker.project.logistic.service.MessageService;
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
    private MessageService messageService;

    @Autowired
    public ResetPasswordServiceImpl(PersonCrudDao personService, ResetPasswordTokenDao resetPasswordTokenDao,
                                    HttpServletRequest request, MessageService messageService) {
        this.personService = personService;
        this.resetPasswordTokenDao = resetPasswordTokenDao;
        this.request = request;
        this.messageService = messageService;
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
        sendGeneratedTokenOnEmail(resetPasswordToken);

    }

    private void sendGeneratedTokenOnEmail(ResetPasswordToken resetPasswordToken){
        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        Message message = new Message();
        String subject = "Verify email address";
        message.setSubject(subject);
        String to = resetPasswordToken.getPerson().getContact().getEmail();
        message.setTo(to);
        String text = "To reset your password, click the link below:\n" + appUrl
                + "/password/reset?token=" + resetPasswordToken.getResetToken();
        message.setText(text);

        messageService.sendMessage(message);
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
