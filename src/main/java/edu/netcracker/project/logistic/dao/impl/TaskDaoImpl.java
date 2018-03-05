package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.TaskDao;
import edu.netcracker.project.logistic.model.Task;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskDaoImpl implements TaskDao, RowMapper<Task> {
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    public TaskDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("task_id"));
        task.setOrderId(rs.getLong("order_id"));
        task.setEmployeeId(rs.getLong("employee_id"));
        task.setCompleted(rs.getBoolean("is_completed"));
        return task;
    }

    @Override
    public List<Task> findUncompleted() {
        try {
            return jdbcTemplate.query(
                    getFindUncompletedQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Task> findUncompletedByEmployeeId(Long employeeId) {
        try {
            return jdbcTemplate.query(
                    getFindUncompletedByEmployeeIdQuery(),
                    new Object[]{employeeId},
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
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

    private String getFindUncompletedByEmployeeIdQuery() {
        return queryService.getQuery("select.task.uncompleted.by.employee_id");
    }
}
