package edu.netcracker.project.logistic.model;

public class Task {
    private Long id;
    private String description;
    private boolean is_complete;
    private  Person person;
    private Order order;

    public Task() {
    }

    public Task(Long id, String description, boolean is_complete, Person person, Order order) {
        this.id = id;
        this.description = description;
        this.is_complete = is_complete;
        this.person = person;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIs_complete() {
        return is_complete;
    }

    public void setIs_complete(boolean is_complete) {
        this.is_complete = is_complete;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
