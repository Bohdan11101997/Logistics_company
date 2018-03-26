package edu.netcracker.project.logistic.model;

public class OrderDraft {
    private Long id;
    private Long personId;
    private Order draft;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Order getDraft() {
        return draft;
    }

    public void setDraft(Order draft) {
        this.draft = draft;
    }
}
