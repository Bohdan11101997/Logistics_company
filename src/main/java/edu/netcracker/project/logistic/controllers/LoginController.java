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

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private SmartValidator smartValidator;
    private ResetPasswordService resetPasswordService;
    private PersonService personService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(SmartValidator smartValidator, ResetPasswordService resetPasswordService, PersonService personService, PasswordEncoder passwordEncoder) {
        this.smartValidator = smartValidator;
        this.resetPasswordService = resetPasswordService;
        this.personService = personService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String login(){
        return "login";
    }

    @GetMapping("/forgot/password")
    public String forgotPassword(Model model){
        ForgotPasswordForm forgotPasswordForm = new ForgotPasswordForm();
        model.addAttribute("forgotPassword", forgotPasswordForm);
        return "forgot_password";
    }

    @PostMapping("/forgot/password")
    public String sendResetPasswordLinkOnEmail(@ModelAttribute("forgotPassword") ForgotPasswordForm forgotPasswordForm,
                                         BindingResult bindingResult){

        String email = forgotPasswordForm.getEmail();
        smartValidator.validate(email, bindingResult);

        if (bindingResult.hasErrors()){
            return "forgot_password";
        }

        try {
            resetPasswordService.generateAndSendOnEmailResetToken(email);
        } catch (MessagingException ex) {
            logger.error("Exception caught when sending confirmation mail", ex);
        } catch (IllegalArgumentException ex){
            logger.error("No person with such e-mail");
            return "redirect:/login";
        }

        return "redirect:/login";
    }

    @GetMapping("/reset/password")
    public String resetPassword(Model model, @RequestParam("token") String resetToken){
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        Optional<ResetPasswordToken> optionalResetPasswordToken = resetPasswordService.findResetPasswordTokenByResetToken(resetToken);

        if (!optionalResetPasswordToken.isPresent()){
            return "/error/404";
        }

        ResetPasswordToken resetPasswordToken = optionalResetPasswordToken.get();

        Person person = resetPasswordToken.getPerson();
        resetPasswordForm.setPersonId(person.getId());
        model.addAttribute("resetPassword", resetPasswordForm);
        return "reset_password";
    }

    @PostMapping("/reset/password/{id}")
    public String saveNewPassword(@ModelAttribute("resetPassword") ResetPasswordForm resetPasswordForm,
                                  @PathVariable("id") Long personId){

        String newPassword = resetPasswordForm.getNewPassword();
        Optional<Person> optionalPerson = personService.findOne(personId);
        Person person = optionalPerson.get();

        person.setPassword(newPassword);
        personService.savePerson(person);

        resetPasswordService.deleteResetPasswordTokenRowById(personId);

        return "redirect:/login";
    }

}
