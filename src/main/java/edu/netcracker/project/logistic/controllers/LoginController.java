package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.ForgotPasswordForm;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.ResetPasswordForm;
import edu.netcracker.project.logistic.model.ResetPasswordToken;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.ResetPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Optional;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    @GetMapping
    public String login(Model model,
                        @ModelAttribute("infoMessage") String infoMessage){

        if (infoMessage == null || infoMessage.isEmpty()) {
            infoMessage = null;
        }

        model.addAttribute("infoMessage", infoMessage);
        return "login";
    }

}
