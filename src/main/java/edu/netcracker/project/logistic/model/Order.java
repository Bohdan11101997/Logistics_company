package edu.netcracker.project.logistic.model;

import java.time.LocalDate;

public class Order {
    private Long id;
    private LocalDate creationDay;
    private LocalDate deliveryTime;
    private LocalDate orderStatusTime;
    private Contact receiverContact;
    private Contact senderContact;
    private Address receiverAddress;
    private Address senderAddress;
    private Person courier;
    private Office office;
    private OrderStatus orderStatus;

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDay() {
        return creationDay;
    }

    public void setCreationDay(LocalDate creationDay) {
        this.creationDay = creationDay;
    }

    public LocalDate getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDate deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public LocalDate getOrderStatusTime() {
        return orderStatusTime;
    }

    public void setOrderStatusTime(LocalDate orderStatusTime) {
        this.orderStatusTime = orderStatusTime;
    }

    public Contact getReceiverContact() {
        return receiverContact;
    }

    public void setReceiverContact(Contact receiverContact) {
        this.receiverContact = receiverContact;
    }

    public Contact getSenderContact() {
        return senderContact;
    }

    public void setSenderContact(Contact senderContact) {
        this.senderContact = senderContact;
    }

    public Address getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(Address receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public Address getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(Address senderAddress) {
        this.senderAddress = senderAddress;
    }

    public Person getCourier() {
        return courier;
    }

    public void setCourier(Person courier) {
        this.courier = courier;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
