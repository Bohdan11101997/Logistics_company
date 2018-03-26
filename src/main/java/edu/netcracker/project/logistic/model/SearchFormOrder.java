package edu.netcracker.project.logistic.model;

import java.time.LocalDateTime;
import java.util.List;

public class SearchFormOrder {

    private String firstName;
    private String lastName;
    private LocalDateTime from;
    private LocalDateTime to;
    private List<Long> destination_typeIds;
    private List<Long> order_statusIds;
    private Long contact_side;

    public Long getContact_side() {
        return contact_side;
    }

    public void setContact_side(Long contact_side) {
        this.contact_side = contact_side;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public List<Long> getDestination_typeIds() {
        return destination_typeIds;
    }

    public void setDestination_typeIds(List<Long> destination_typeIds) {
        this.destination_typeIds = destination_typeIds;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public List<Long> getOrder_statusIds() {
        return order_statusIds;
    }

    public void setOrder_statusIds(List<Long> order_statusIds) {
        this.order_statusIds = order_statusIds;
    }
}
