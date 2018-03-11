package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.service.SecurityService;
import edu.netcracker.project.logistic.service.UserService;
import edu.netcracker.project.logistic.validation.CurrentPasswordValidator;
import edu.netcracker.project.logistic.validation.UpdateUserValidator;
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
    private SmartValidator fieldValidator;
    private UpdateUserValidator updateUserValidator;
    private CurrentPasswordValidator currentPasswordValidator;
    private UserService userService;
    private SecurityService securityService;
    private OrderTypeDao orderTypeDao;
    private PasswordEncoder passwordEncoder;
    private TaskProcessor taskProcessor;

    @Autowired
    public UserController(SmartValidator fieldValidator, UpdateUserValidator updateUserValidator,
                          CurrentPasswordValidator currentPasswordValidator, UserService userService,
                          SecurityService securityService, OrderTypeDao orderTypeDao, PasswordEncoder passwordEncoder,
                          TaskProcessor taskProcessor) {
        this.fieldValidator = fieldValidator;
        this.updateUserValidator = updateUserValidator;
        this.currentPasswordValidator = currentPasswordValidator;
        this.userService = userService;
        this.securityService = securityService;
        this.orderTypeDao = orderTypeDao;
        this.passwordEncoder = passwordEncoder;
        this.taskProcessor = taskProcessor;
    }

    @GetMapping("/personal")
    public String viewPersonalArea(Model model, Principal principal) {

        String username = principal.getName();
        Optional<Person> optionalPerson = userService.findOne(username);

        if (!optionalPerson.isPresent()) {
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

    private UserForm getUserFormFilledWithContactData(Contact contact) {

        UserForm userForm = new UserForm();
        userForm.setFirstName(contact.getFirstName());
        userForm.setLastName(contact.getLastName());
        userForm.setEmail(contact.getEmail());
        userForm.setPhoneNumber(contact.getPhoneNumber());
        return userForm;
    }

    @GetMapping(value = "/personal/{id}")
    public String changePersonalArea(@PathVariable Long id) {
        return "redirect:/user/personal";
    }

    @PostMapping(value = "/personal/{id}", params = "action=save")
    public String updatePersonalArea(@PathVariable Long id,
                                     @ModelAttribute("user") UserForm userForm,
                                     BindingResult bindingResult) {

        Optional<Person> optionalPerson = userService.findOne(id);

        if (!optionalPerson.isPresent()) {
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

        if (!oldUsername.equals(person.getUserName())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            securityService.autoLogIn(person.getUserName(), (String) authentication.getCredentials());
        }

        return "redirect:/user/personal?save";
    }

    @PostMapping(value = "/personal/{id}", params = "action=delete")
    public String deletePersonalArea(@PathVariable Long id) {

        userService.delete(id);
        return "redirect:/login?delete";

    }

    @GetMapping(value = "/change/password")
    public String viewChangePassword(Model model) {
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        model.addAttribute("changePassword", changePasswordForm);
        return "user/user_change_password";
    }

    @PostMapping(value = "/change/password")
    public String saveNewPassword(@ModelAttribute("changePassword") ChangePasswordForm changePasswordForm,
                                  BindingResult bindingResult) {

        String currentPasswordFromForm = changePasswordForm.getOldPassword();
        currentPasswordValidator.validate(currentPasswordFromForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "user/user_change_password";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // change for single password
        Optional<Person> optionalPerson = userService.findOne(username);

        if (!optionalPerson.isPresent()) {
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

    @GetMapping(value = "/order")
    public String createOrder(Model model) {
        Order order = new Order();
        model.addAttribute("order", order);
        model.addAttribute("orderTypes", orderTypeDao.findAll());
        return "user/order";
    }

    @PostMapping(value = "/order")
    public String doCreateOrder(@ModelAttribute("order") Order order, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person user = opt.get();
        order.setSenderContact(user.getContact());

        userService.createOrder(order);
        return "person_main";
    }
}
