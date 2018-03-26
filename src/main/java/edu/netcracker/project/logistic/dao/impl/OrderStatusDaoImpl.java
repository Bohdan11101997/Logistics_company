package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OrderStatusDao;
import edu.netcracker.project.logistic.model.OrderStatus;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OrderStatusDaoImpl implements OrderStatusDao, RowMapper<OrderStatus> {
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    public OrderStatusDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public OrderStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setId(rs.getLong("order_status_id"));
        orderStatus.setName(rs.getString("status_name"));
        return orderStatus;
    }

    @Override
    public OrderStatus save(OrderStatus orderStatus) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<OrderStatus> findOne(Long id) {
        try {
            OrderStatus orderStatus = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{id},
                    this
            );
            return Optional.of(orderStatus);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<OrderStatus> findByName(String name) {
        try {
            OrderStatus orderStatus = jdbcTemplate.queryForObject(
                    getFindByNameQuery(),
                    new Object[]{name},
                    this
            );
            return Optional.of(orderStatus);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }


    public List<OrderStatus> findAll() {

        try {
            return jdbcTemplate.query(getAllStatusQuery(), this);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private String getAllStatusQuery() {
        return queryService.getQuery("all.status");
    }

    private String getFindOneQuery() {
        return queryService.getQuery("select.order_status");
    }

    private String getFindByNameQuery() {
        return queryService.getQuery("select.order_status.by.name");
    }
}
