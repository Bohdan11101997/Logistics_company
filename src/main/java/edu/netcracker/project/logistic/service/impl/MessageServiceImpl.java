package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.model.Message;
import edu.netcracker.project.logistic.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@PropertySource("classpath:mail.properties")
public class MessageServiceImpl implements MessageService {

    private final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private Environment env;
    private JavaMailSender sender;

    @Autowired
    public MessageServiceImpl(Environment env, JavaMailSender sender) {
        this.env = env;
        this.sender = sender;
    }

    @Override
    public boolean sendMessage(Message message) {

        boolean sendSuccess;

        try {
            sendSuccess = trySendMessage(message);
        } catch (Exception e) {
            logger.error("Message wasn't send. Something went wrong.");
            e.printStackTrace();
            return false;
        }

        return sendSuccess;

    }

    private boolean trySendMessage(Message message){

        try {

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper messageToSend = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            trySetParametersForSendMessage(messageToSend, message);
            sender.send(mimeMessage);

        } catch (MessagingException e) {
            logger.error("Message creation was failed!");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void trySetParametersForSendMessage(MimeMessageHelper messageToSend, Message message) throws MessagingException {

        setSubject(messageToSend, message);
        setFrom(messageToSend);
        setTo(messageToSend, message);
        setText(messageToSend, message);

    }

    private void setSubject(MimeMessageHelper messageToSend, Message message) throws MessagingException {
        String subject = message.getSubject();
        messageToSend.setSubject(subject);
    }

    private void setFrom(MimeMessageHelper messageToSend) throws MessagingException {
        String from = env.getProperty("spring.mail.username");
        messageToSend.setFrom(from);
    }

    private void setTo(MimeMessageHelper messageToSend, Message message) throws MessagingException {
        String to = message.getTo();
        messageToSend.setTo(to);
    }

    private void setText(MimeMessageHelper messageToSend, Message message) throws MessagingException {
        String text = message.getText();
        messageToSend.setText(text, false);
    }

}
