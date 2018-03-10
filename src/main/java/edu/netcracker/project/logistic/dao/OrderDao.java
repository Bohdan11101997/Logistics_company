package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Order;

import java.util.List;

public interface OrderDao extends  CrudDao<Order, Long> {
    List<Order> findNotProcessed();
}

