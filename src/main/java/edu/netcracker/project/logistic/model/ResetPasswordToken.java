package edu.netcracker.project.logistic.model;

public class ResetPasswordToken {

    private Person person;
    private String resetToken;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
