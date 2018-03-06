package edu.netcracker.project.logistic.controllers;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/facebook")
public class HelloController {

    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public HelloController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }


    @GetMapping
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        }
        String [] fields = {"name","email","location","gender","first_name","last_name"};
        User user = facebook.fetchObject("me", User.class, fields);
        String location = String.valueOf(user.getLocation());
        String name=user.getName();
        String email=user.getEmail();
        String first_name = user.getFirstName();
        String last_name = user.getLastName();
        String gender=user.getGender();
        model.addAttribute("name",name );
        model.addAttribute("email",email );
        model.addAttribute("gender",gender);
        model.addAttribute("location",location);
        model.addAttribute("first_name",first_name);
        model.addAttribute("last_name",last_name);
        model.addAttribute("facebookProfile", facebook.fetchObject("me", User.class, fields));
        PagedList<Post> feed = facebook.feedOperations().getFeed();
        model.addAttribute("feed", feed);
        return "hello";
    }

}
