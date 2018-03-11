package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Order;

public interface OrderService {
    void draft(Order order);
    void createOrder(Order order);
}
