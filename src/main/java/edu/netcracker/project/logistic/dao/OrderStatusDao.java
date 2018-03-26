package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderStatusDao extends CrudDao<OrderStatus, Long> {
    Optional<OrderStatus> findByName(String name);

    List<OrderStatus> findAll();
}
