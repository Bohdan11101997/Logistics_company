package edu.netcracker.project.logistic.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageValidator {

    public boolean isImage(MultipartFile multipartFile) {

        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e){
            return false;
        }
        return true;
    }
}
