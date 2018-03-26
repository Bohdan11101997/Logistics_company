package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.SearchFormOrder;

import java.util.List;

public interface OrderDao extends  CrudDao<Order, Long> {
    List<Order> findConfirmed();
    List<Order> findConfirmedByEmployeeId(Long employeeId);
    List<Order> findNotProcessed();
    List<Order> findNotProcessedByEmployeeId(Long employeeId);
    List<Order> orderBySenderOrReceiver(Long contactId);
    List<Order> search(SearchFormOrder searchFormOrder, Long id);
}

