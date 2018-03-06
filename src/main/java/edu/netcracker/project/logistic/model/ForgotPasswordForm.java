package edu.netcracker.project.logistic.model;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ForgotPasswordForm {

    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
