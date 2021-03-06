package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskDao extends CrudDao<Task, Long> {
    List<Task> findUncompleted();
    List<Task> findUncompletedByEmployeeId(Long employeeId);
    Optional<Task> findByOrderId(Long orderId);
}
