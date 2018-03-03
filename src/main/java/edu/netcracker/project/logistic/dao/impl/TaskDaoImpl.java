package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.TaskDao;
import edu.netcracker.project.logistic.model.Task;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskDaoImpl implements TaskDao {
    private QueryService queryService;

    public TaskDaoImpl(QueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public List<Task> findUncompleted() {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public Task save(Task object) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void delete(Long aLong) {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public Optional<Task> findOne(Long aLong) {
        throw new RuntimeException("Not implemented yet.");
    }

    private String getFindUncompletedQuery() {
        return queryService.getQuery("select.task.uncompleted");
    }
}
