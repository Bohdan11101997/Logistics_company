package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Order;

import java.util.Date;
import java.util.List;

public interface OrderDao extends  CrudDao<Order, Long> {
    List<Order> findNotProcessed();
//
//    List<Order> HistoryCompleteOrderSender(Long aLong, String name, String last_name);
//
//    List<Order> HistoryCompleteOrderReceiver(Long aLong);
}

