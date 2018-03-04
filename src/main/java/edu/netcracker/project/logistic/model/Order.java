package edu.netcracker.project.logistic.model;

import java.time.LocalDate;

public class Order {
    private Long id;
    private LocalDate creationDay;
    private LocalDate deliveryTime;
    private LocalDate orderStatusTime;
    private AddressContact receiver;
    private AddressContact sender;
    private Person courier;
    private Office office;
    private OrderStatus orderStatus;


    public Order() {


    }

    public Order(Long id, LocalDate creationDay, LocalDate deliveryTime, LocalDate orderStatusTime, AddressContact receiver, AddressContact sender, Person courier, Office office, OrderStatus orderStatus) {
        this.id = id;
        this.creationDay = creationDay;
        this.deliveryTime = deliveryTime;
        this.orderStatusTime = orderStatusTime;
        this.receiver = receiver;
        this.sender = sender;
        this.courier = courier;
        this.office = office;
        this.orderStatus = orderStatus;
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

    public AddressContact getReceiver() {
        return receiver;
    }

    public void setReceiver(AddressContact receiver) {
        this.receiver = receiver;
    }

    public AddressContact getSender() {
        return sender;
    }

    public void setSender(AddressContact sender) {
        this.sender = sender;
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
