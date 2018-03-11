package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.TaskDao;
import edu.netcracker.project.logistic.model.Task;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    public Task save(Task task) {
        boolean hasPrimaryKey = task.getId() != null;
        if (hasPrimaryKey) {
            jdbcTemplate.update(
                    getUpsertQuery(),
                    ps -> {
                        ps.setLong(1, task.getId());
                        ps.setLong(2, task.getEmployeeId());
                        ps.setLong(3, task.getOrderId());
                        ps.setBoolean(4, task.getCompleted());
                    }
            );
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, task.getEmployeeId());
                ps.setLong(2, task.getOrderId());
                ps.setBoolean(3, task.getCompleted());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("task_id");
            task.setId(key.longValue());
        }
        return task;
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

    private String getInsertQuery() {
        return queryService.getQuery("insert.task");
    }

    private String getUpsertQuery() {
        return queryService.getQuery("upsert.task");
    }

    private String getFindUncompletedByEmployeeIdQuery() {
        return queryService.getQuery("select.task.uncompleted.by.employee_id");
    }
}
