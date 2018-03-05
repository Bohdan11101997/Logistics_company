package edu.netcracker.project.logistic.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePasswordForm {

    private String oldPassword;
    private String newPassword;

    @NotNull
    @Size(min = 6, max = 30)
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @NotNull
    @Size(min = 6, max = 30)
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
