package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.model.FeedbackForm;
import edu.netcracker.project.logistic.service.AdvertisementService;
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
@PropertySource("classpath:mail.properties")
@RequestMapping({"/", "index"})
public class IndexController {

    private AdvertisementService advertisementService;
    private HttpServletRequest request;
    private Environment env;
    private JavaMailSender sender;

    @Autowired
    public IndexController(AdvertisementService advertisementService, HttpServletRequest request, Environment env, JavaMailSender sender) {
        this.advertisementService = advertisementService;
        this.request = request;
        this.env = env;
        this.sender = sender;
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

        // send it in e-mail
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        msg.setSubject("Verify email address");
        String from = env.getProperty("spring.mail.username");
        msg.setFrom(from);
        msg.setTo("its_an_omen@ukr.net");
        msg.setText("Hello, my name is: " + feedback.getName() + "\n"
                + feedback.getMessage() + "\n"
                + "I'm waiting for answer on this e-mail: " + feedback.getEmail(), false) ;

        sender.send(mimeMessage);

        return "redirect:/index";
    }

}
