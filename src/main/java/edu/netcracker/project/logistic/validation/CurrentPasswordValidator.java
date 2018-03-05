package edu.netcracker.project.logistic.validation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CurrentPasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return String.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        String oldPassword = (String) o;

        String currentPassword = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        if (!currentPassword.equals(oldPassword)){
            errors.rejectValue("oldPassword", "Password.Not.Match");
        }
    }
}
