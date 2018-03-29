package edu.netcracker.project.logistic.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

    private Long id;
    private LocalDateTime creationTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime orderStatusTime;
    private Person courier;
    private Contact receiverContact;
    private Contact senderContact;
    private Address receiverAddress;
    private Address senderAddress;
    private Office office;
    private OrderStatus orderStatus;
    private OrderType orderType;
    private BigDecimal weight;
    private Long width;
    private Long height;
    private Long length;
    private Long capacity;

    public Order() {
    }

    public Order(Order other) {
        this.id = other.id;
        this.creationTime = other.creationTime;
        this.deliveryTime = other.deliveryTime;
        this.orderStatusTime = other.orderStatusTime;
        this.courier = other.courier;
        this.receiverContact = other.receiverContact;
        this.senderContact = other.senderContact;
        this.receiverAddress = other.receiverAddress;
        this.senderAddress = other.senderAddress;
        this.office = other.office;
        this.orderStatus = other.orderStatus;
        this.orderType = other.orderType;
        this.weight = other.weight;
        this.width = other.width;
        this.height = other.height;
        this.length = other.length;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public LocalDateTime getOrderStatusTime() {
        return orderStatusTime;
    }

    public void setOrderStatusTime(LocalDateTime orderStatusTime) {
        this.orderStatusTime = orderStatusTime;
    }

    public Person getCourier() {
        return courier;
    }

    public void setCourier(Person courier) {
        this.courier = courier;
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

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(creationTime, order.creationTime) &&
                Objects.equals(deliveryTime, order.deliveryTime) &&
                Objects.equals(orderStatusTime, order.orderStatusTime) &&
                Objects.equals(courier, order.courier) &&
                Objects.equals(receiverContact, order.receiverContact) &&
                Objects.equals(senderContact, order.senderContact) &&
                Objects.equals(receiverAddress, order.receiverAddress) &&
                Objects.equals(senderAddress, order.senderAddress) &&
                Objects.equals(office, order.office) &&
                Objects.equals(orderStatus, order.orderStatus) &&
                Objects.equals(orderType, order.orderType) &&
                Objects.equals(weight, order.weight) &&
                Objects.equals(width, order.width) &&
                Objects.equals(height, order.height) &&
                Objects.equals(length, order.length);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, creationTime, deliveryTime, orderStatusTime, courier, receiverContact, senderContact, receiverAddress, senderAddress, office, orderStatus, orderType, weight, width, height, length);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", creationTime=" + creationTime +
                ", deliveryTime=" + deliveryTime +
                ", orderStatusTime=" + orderStatusTime +
                ", courier=" + courier +
                ", receiverContact=" + receiverContact +
                ", senderContact=" + senderContact +
                ", receiverAddress=" + receiverAddress +
                ", senderAddress=" + senderAddress +
                ", office=" + office +
                ", orderStatus=" + orderStatus +
                ", orderType=" + orderType +
                ", weight=" + weight +
                ", width=" + width +
                ", height=" + height +
                ", length=" + length +
                '}';
    }
}
