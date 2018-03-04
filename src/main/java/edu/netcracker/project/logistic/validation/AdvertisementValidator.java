package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.model.AdvertisementForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AdvertisementValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return AdvertisementForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        AdvertisementForm advertisementForm = (AdvertisementForm) o;

        if (advertisementForm.getShowFirstDate().isAfter(advertisementForm.getShowEndDate())){
            errors.rejectValue("showFirstDate", "Date.First.Less.End");
        }
    }
}
