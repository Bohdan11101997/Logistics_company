package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.ChangePasswordForm;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.UserForm;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.SecurityService;
import edu.netcracker.project.logistic.service.UserService;
import edu.netcracker.project.logistic.validation.UpdateUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;
    private UpdateUserValidator validator;
    private SmartValidator userFormValidator;
    private PasswordEncoder passwordEncoder;
    private SecurityService securityService;

    @Autowired
    public UserController(UserService userService, UpdateUserValidator validator,
                          SmartValidator userFormValidator, PasswordEncoder passwordEncoder,
                          SecurityService securityService){
        this.userService = userService;
        this.validator = validator;
        this.userFormValidator = userFormValidator;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @GetMapping("/personal")
    public String viewPersonalArea(Model model, Principal principal){

        String username = principal.getName();
        Optional<Person> optionalPerson = userService.findOne(username);

        if (!optionalPerson.isPresent()){
            return "/error/403";
        }

        Person person = optionalPerson.get();

        UserForm userForm = new UserForm();
        userForm.setId(person.getId());
        userForm.setUserName(person.getUserName());
        Contact contact = person.getContact();
        userForm.setFirstName(contact.getFirstName());
        userForm.setLastName(contact.getLastName());
        userForm.setEmail(contact.getEmail());
        userForm.setPhoneNumber(contact.getPhoneNumber());

        model.addAttribute("user", userForm);

        return "/user/user_personal_area";
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

        userFormValidator.validate(userForm, bindingResult);
        validator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/user/user_personal_area";
        }

        userService.update(person);

        if (!oldUsername.equals(person.getUserName())){
            SecurityContextHolder.clearContext();
            return "redirect:/login?changeUsername";
        }

        return "redirect:/user/personal?save";
    }

    @PostMapping(value = "/personal/{id}", params = "action=delete")
    public String deletePersonalArea(@PathVariable Long id){

        userService.delete(id);
        return "redirect:/login?delete";

    }

    @GetMapping(value = "/change/password")
    public String viewChangePassword(Model model){
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        model.addAttribute("changePassword", changePasswordForm);
        return "user/user_change_password";
    }

    @PostMapping(value = "/change/password")
    public String saveNewPassword(@ModelAttribute("changePassword") ChangePasswordForm changePasswordForm,
                                  Authentication authentication,
                                  BindingResult bindingResult){

        String oldPassword = (String) authentication.getCredentials();
        String oldPasswordFromForm = changePasswordForm.getOldPassword();

        // validation
        if (!oldPassword.equals(oldPasswordFromForm)){
            bindingResult.rejectValue("oldPassword", "Password.Not.Match");
        }

        if (bindingResult.hasErrors()){
            return "user/user_change_password";
        }

        String username = authentication.getName();
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

        return "redirect:/user/change/password?save";
    }
}
