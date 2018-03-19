package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.CourierData;
import edu.netcracker.project.logistic.model.Order;

import java.util.List;

public interface OrderService {
    void draft(Order order);

    void createOrder(Order order);

    void confirmOrder(Long employeeId, Long orderId);

    void rejectOrder(Long employeeId, Long orderId);

    void confirmDelivered(CourierData data, Long orderId);

    void confirmFailed(CourierData data, Long orderId);
}
