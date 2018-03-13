package edu.netcracker.project.logistic.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public class Person {
    private Long id;
    private String userName;
    private String password;
    private LocalDateTime registrationDate;
    private Contact contact;
    private Set<Role> roles;

    public Person(String userName, String password, LocalDateTime registrationDate, Contact contact, Set<Role> roles) {
        this.userName = userName;
        this.password = password;
        this.registrationDate = registrationDate;
        this.contact = contact;
        this.roles = roles;
    }

    public Person() {
    }

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
    @Size(min = 6, max = 30)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @NotNull
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", registrationDate=" + registrationDate +
                ", contact=" + contact +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(userName, person.userName) &&
                Objects.equals(password, person.password) &&
                Objects.equals(registrationDate, person.registrationDate) &&
                Objects.equals(contact, person.contact) &&
                Objects.equals(roles, person.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password, registrationDate, contact, roles);
    }
}
