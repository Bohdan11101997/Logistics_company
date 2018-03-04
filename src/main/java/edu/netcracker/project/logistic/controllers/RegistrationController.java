package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.RegistrationService;
import edu.netcracker.project.logistic.service.RoleService;
import edu.netcracker.project.logistic.validation.RegistrationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.UUID;

@Controller
public class RegistrationController {
    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);


    private RegistrationService registrationService;
    private RegistrationValidator registrationValidator;
    private RoleService roleService;

    @Autowired
    public RegistrationController(RegistrationService registrationService,
                                  RegistrationValidator registrationValidator, RoleService roleService) {
        this.registrationService = registrationService;
        this.registrationValidator = registrationValidator;
        this.roleService = roleService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("registrationForm", new Person());
        model.addAttribute("availableRoles", roleService.findClientRoles());
        return "registration";
    }

    @PostMapping("/registration")
    public String doRegistration(Model model,@ModelAttribute("registrationForm") Person registrationForm,
                                 BindingResult bindingResult) {
        registrationValidator.validate(registrationForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableRoles", roleService.findClientRoles());
            return "registration";
        }
        try {
            registrationService.register(registrationForm);
        } catch (MessagingException ex) {
            logger.error("Exception caught when sending confirmation mail", ex);
        }
        return "redirect:/registration/complete";
    }

    @GetMapping("/registration/complete")
    public String completeRegistration() {
        return "complete_registration";
    }

    @GetMapping("/registration/confirm")
    public String confirmEmail(@RequestParam("id") String idParam) {
        try {
            UUID id = UUID.fromString(idParam);
            registrationService.confirmAccount(id);
        } catch (IllegalArgumentException ex) {
            return "redirect:/error/404";
        }
        return "confirm_email";
    }
}
