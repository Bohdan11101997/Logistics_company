package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Order;

import java.util.List;

public interface OrderService {
    void draft(Order order);
    void createOrder(Order order);
     List<Order> HistoryCompleteOrderReceiver(Long aLong);
    List<Order> HistoryCompleteOrderSender(Long aLong);
}
