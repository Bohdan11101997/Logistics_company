package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.ChangePasswordForm;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.UserForm;
import edu.netcracker.project.logistic.service.SecurityService;
import edu.netcracker.project.logistic.service.UserService;
import edu.netcracker.project.logistic.validation.CurrentPasswordValidator;
import edu.netcracker.project.logistic.validation.UpdateUserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private SmartValidator fieldValidator;
    private UpdateUserValidator updateUserValidator;
    private CurrentPasswordValidator currentPasswordValidator;
    private UserService userService;
    private SecurityService securityService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(SmartValidator fieldValidator, UpdateUserValidator updateUserValidator,
                          CurrentPasswordValidator currentPasswordValidator, UserService userService,
                          SecurityService securityService, PasswordEncoder passwordEncoder) {
        this.fieldValidator = fieldValidator;
        this.updateUserValidator = updateUserValidator;
        this.currentPasswordValidator = currentPasswordValidator;
        this.userService = userService;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/personal")
    public String viewPersonalArea(Model model, Principal principal){

        String username = principal.getName();
        Optional<Person> optionalPerson = userService.findOne(username);

        if (!optionalPerson.isPresent()){
            return "/error/403";
        }

        Person person = optionalPerson.get();
        UserForm userForm = getUserFormFilledWithPersonData(person);
        model.addAttribute("user", userForm);

        return "/user/user_personal_area";
    }

    private UserForm getUserFormFilledWithPersonData(Person person) {

        Contact contact = person.getContact();
        UserForm userForm = getUserFormFilledWithContactData(contact);
        userForm.setId(person.getId());
        userForm.setUserName(person.getUserName());
        return userForm;
    }

    private UserForm getUserFormFilledWithContactData(Contact contact){

        UserForm userForm = new UserForm();
        userForm.setFirstName(contact.getFirstName());
        userForm.setLastName(contact.getLastName());
        userForm.setEmail(contact.getEmail());
        userForm.setPhoneNumber(contact.getPhoneNumber());
        return userForm;
    }

    @GetMapping(value = "/personal/{id}")
    public String changePersonalArea(@PathVariable Long id){
        return "redirect:/user/personal";
    }

    @PostMapping(value = "/personal/{id}", params = "action=save")
    public String updatePersonalArea(@PathVariable Long id,
                                     @ModelAttribute("user") UserForm userForm,
                                     BindingResult bindingResult){

        Optional<Person> optionalPerson = userService.findOne(id);

        if (!optionalPerson.isPresent()){
            return "/error/403";
        }

        Person person = optionalPerson.get();
        String oldUsername = person.getUserName();
        person.setUserName(userForm.getUserName());
        Contact contact = person.getContact();
        contact.setFirstName(userForm.getFirstName());
        contact.setLastName(userForm.getLastName());
        contact.setEmail(userForm.getEmail());
        contact.setPhoneNumber(userForm.getPhoneNumber());

        fieldValidator.validate(userForm, bindingResult);
        updateUserValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/user/user_personal_area";
        }

        userService.update(person);

        if (!oldUsername.equals(person.getUserName())){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            securityService.autoLogIn(person.getUserName(), (String) authentication.getCredentials());
        }

        return "redirect:/user/personal?save";
    }

    @PostMapping(value = "/personal/{id}", params = "action=delete")
    public String deletePersonalArea(@PathVariable Long id){

        userService.delete(id);
        return "redirect:/login?delete";

    }


    @GetMapping(value = "/password/change")
    public String viewChangePassword(Model model){
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        model.addAttribute("changePassword", changePasswordForm);
        return "user/user_change_password";
    }

    @PostMapping(value = "/password/change")
    public String saveNewPassword(@ModelAttribute("changePassword") ChangePasswordForm changePasswordForm,
                                  BindingResult bindingResult,
                                  Model model){

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
        String oldPassword = person.getPassword();

        if (!passwordEncoder.matches(newPassword, oldPassword)){
            String newPasswordEncoded = passwordEncoder.encode(newPassword);
            person.setPassword(newPasswordEncoded);
            userService.update(person);
        } else {
            logger.error("Same old and new passwords");
            bindingResult.rejectValue("newPassword", "Password.Old.Equals.New");
            return "user/user_change_password";
        }

        securityService.autoLogIn(username, newPassword);

        return "redirect:/user/password/change?save";
    }


}
