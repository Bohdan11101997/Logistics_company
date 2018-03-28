package edu.netcracker.project.logistic.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserForm {

    private Long id;
    private String userName;
    private Contact contact;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 6, max = 30)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @NotNull
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
