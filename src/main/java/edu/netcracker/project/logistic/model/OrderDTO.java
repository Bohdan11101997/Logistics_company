package edu.netcracker.project.logistic.model;

public class OrderDTO {
    private Long id;
    private Contact receiverContact;
    private Address receiverAddress;
    private String orderType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getReceiverContact() {
        return receiverContact;
    }

    public void setReceiverContact(Contact receiverContact) {
        this.receiverContact = receiverContact;
    }

    public Address getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(Address receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public static OrderDTO valueOf(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderType(order.getOrderType().getName());
        orderDTO.setReceiverContact(order.getReceiverContact());
        orderDTO.setReceiverAddress(order.getReceiverAddress());
        return orderDTO;
    }
}
