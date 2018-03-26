package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/image")
public class ImageController {

    @Autowired
    AdvertisementService advertisementService;

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public void viewImage(@RequestParam("id") Long advertisementId,
                          HttpServletResponse response)
            throws IOException {


        try {

            Optional<Advertisement> advertisementOptional = advertisementService.findOne(advertisementId);

            Advertisement advertisement = new Advertisement();
            if (advertisementOptional.isPresent()){
                advertisement = advertisementOptional.get();
            }

            response.setContentType("image/jpeg");
            response.getOutputStream().write(advertisement.getImage());
        } finally {
            response.getOutputStream().close();
        }

    }

}
