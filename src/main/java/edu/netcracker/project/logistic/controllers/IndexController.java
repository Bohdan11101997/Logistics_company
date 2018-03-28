package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.model.FeedbackForm;
import edu.netcracker.project.logistic.model.Message;
import edu.netcracker.project.logistic.service.AdvertisementService;
import edu.netcracker.project.logistic.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping({"/", "index"})
public class IndexController {

    private AdvertisementService advertisementService;
    private MessageService messageService;

    @Autowired
    public IndexController(AdvertisementService advertisementService, MessageService messageService) {
        this.advertisementService = advertisementService;
        this.messageService = messageService;
    }


    @GetMapping
    public String index(Model model){

        List<Advertisement> advertisements = advertisementService.findAllForToday();
        model.addAttribute("advertisements", advertisements);

        FeedbackForm feedbackForm = new FeedbackForm();
        model.addAttribute("feedbackForm", feedbackForm);

        return "index";
    }

    @PostMapping(value = "/feedback/send")
    public String sendFeedback(@ModelAttribute("feedbackForm") FeedbackForm feedback) throws MessagingException {

        Message message = new Message();
        message.setSubject("Feedback about service");
        message.setTo("its_an_omen@ukr.net");

        String messageText = formMessageTextFromFeedbackFrom(feedback);
        message.setText(messageText);

        messageService.sendMessage(message);

        return "redirect:/index";
    }

    private String formMessageTextFromFeedbackFrom(FeedbackForm feedback) {

        String greeting = formMessageGreetingFromFeedbackForm(feedback);
        String mainText = feedback.getMessage();
        String waitingForAnswer = formMessageWaitingForAnswerFromFeedbackForm(feedback);

        return greeting + "\n" + mainText + "\n" + waitingForAnswer;
    }

    private String formMessageGreetingFromFeedbackForm(FeedbackForm feedback){
        String name = feedback.getName();
        return "Hello, my name is: " + name;
    }

    private String formMessageWaitingForAnswerFromFeedbackForm(FeedbackForm feedback) {
        String email = feedback.getEmail();
        return "I'm waiting for answer on this e-mail: " + email;
    }

}
