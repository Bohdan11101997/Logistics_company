package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping({"/", "index"})
public class IndexController {

    @Autowired
    private AdvertisementService advertisementService;

    public IndexController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model){

        List<Advertisement> advertisements = advertisementService.findAllForToday();
        model.addAttribute("advertisements", advertisements);

        return "index";
    }

}
