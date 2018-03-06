package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.OrderType;

import java.util.List;

public interface OrderTypeDao extends CrudDao<OrderType, Long> {
    List<OrderType> findAll();
}
