package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.ResetPasswordService;
import edu.netcracker.project.logistic.service.SecurityService;
import edu.netcracker.project.logistic.service.UserService;
import edu.netcracker.project.logistic.validation.CurrentPasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Optional;

@Controller
@RequestMapping("/password")
public class PasswordController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private SmartValidator smartValidator;
    private CurrentPasswordValidator currentPasswordValidator;
    private ResetPasswordService resetPasswordService;
    private PersonService personService;
    private UserService userService;
    private SecurityService securityService;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public PasswordController(SmartValidator smartValidator, CurrentPasswordValidator currentPasswordValidator,
                              ResetPasswordService resetPasswordService, PersonService personService,
                              UserService userService, SecurityService securityService, PasswordEncoder passwordEncoder) {
        this.smartValidator = smartValidator;
        this.currentPasswordValidator = currentPasswordValidator;
        this.resetPasswordService = resetPasswordService;
        this.personService = personService;
        this.userService = userService;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/change")
    public String viewChangePassword(Model model){
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        model.addAttribute("changePassword", changePasswordForm);
        return "user/user_change_password";
    }

    @PostMapping(value = "/change")
    public String saveNewPassword(@ModelAttribute("changePassword") ChangePasswordForm changePasswordForm,
                                  BindingResult bindingResult){

        String currentPasswordFromForm = changePasswordForm.getOldPassword();
        currentPasswordValidator.validate(currentPasswordFromForm, bindingResult);

        if (bindingResult.hasErrors()){
            return "user/user_change_password";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // change for single password
        Optional<Person> optionalPerson = userService.findOne(username);

        if (!optionalPerson.isPresent()){
            return "/error/403";
        }

        Person person = optionalPerson.get();
        String newPassword = changePasswordForm.getNewPassword();
        String newPasswordEncoded = passwordEncoder.encode(newPassword);
        person.setPassword(newPasswordEncoded);
        userService.update(person);

        securityService.autoLogIn(username, newPassword);

        return "redirect:/password/change?save";
    }

    @GetMapping("/forgot")
    public String forgotPassword(Model model){
        ForgotPasswordForm forgotPasswordForm = new ForgotPasswordForm();
        model.addAttribute("forgotPassword", forgotPasswordForm);
        return "forgot_password";
    }

    @PostMapping("/forgot")
    public String sendResetPasswordLinkOnEmail(@ModelAttribute("forgotPassword") ForgotPasswordForm forgotPasswordForm,
                                               BindingResult bindingResult,
                                               Model model){

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
            return "redirect:/login?sendOnMail";
        }

        return "redirect:/login?sendOnMail";
    }

    @GetMapping("/reset")
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

    @PostMapping("/reset/{id}")
    public String saveNewPassword(@ModelAttribute("resetPassword") ResetPasswordForm resetPasswordForm,
                                  @PathVariable("id") Long personId){

        String newPassword = resetPasswordForm.getNewPassword();
        Optional<Person> optionalPerson = personService.findOne(personId);
        Person person = optionalPerson.get();

        person.setPassword(newPassword);
        personService.savePerson(person);

        resetPasswordService.deleteResetPasswordTokenRowById(personId);
        return "redirect:/login?newpassword";
    }

}
