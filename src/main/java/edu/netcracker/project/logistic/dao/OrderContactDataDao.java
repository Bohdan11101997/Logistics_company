package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.order.OrderContactData;

import java.util.List;

public interface OrderContactDataDao extends CrudDao<OrderContactData, Long> {
    List<OrderContactData> findNotProcessed();
}
