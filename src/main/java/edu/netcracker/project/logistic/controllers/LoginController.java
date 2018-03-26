package edu.netcracker.project.logistic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
