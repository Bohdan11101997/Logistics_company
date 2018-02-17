package edu.netcracker.project.logistic.model;

import java.time.LocalDate;

public class Person   {

    private Long id;
    private String userName;
    private String password;
    private LocalDate registrationDate;
    private String email;

    public Person(Long id, String userName, String password, LocalDate registrationDate, String email) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.registrationDate = registrationDate;
        this.email = email;
    }

public  Person()
{

}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }




    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", registrationDate=" + registrationDate +
                ", email='" + email + '\'' +
                '}';
    }
}
