package edu.netcracker.project.logistic.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.netcracker.project.logistic.dao.OrderDraftDao;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.OrderDraft;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OrderDraftDaoImpl implements OrderDraftDao, RowMapper<OrderDraft> {
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;

    public OrderDraftDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public OrderDraft mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderDraft orderDraft = new OrderDraft();
        orderDraft.setId(rs.getLong("order_draft_id"));
        orderDraft.setPersonId(rs.getLong("person_id"));
        String draftJson = rs.getString("draft");
        try {
            Order order = objectMapper.readValue(draftJson, Order.class);
            orderDraft.setDraft(order);
        } catch (IOException ex) {
            throw new IllegalStateException("Invalid order draft data format in database");
        }
        return orderDraft;
    }

    @Override
    public OrderDraft save(OrderDraft orderDraft) {
        boolean hasPrimaryKey = orderDraft.getId() != null;
        if (hasPrimaryKey) {
            jdbcTemplate.update(
                    getUpdateQuery(),
                    ps -> {
                        ps.setLong(1, orderDraft.getId());
                        ps.setLong(2, orderDraft.getPersonId());
                        try {
                            ps.setString(3, objectMapper.writeValueAsString(orderDraft.getDraft()));
                        } catch (JsonProcessingException ex) {
                            throw new IllegalStateException("Can't convert order draft data to JSON");
                        }
                    }
            );
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    psc -> {
                        PreparedStatement ps = psc.prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS);
                        ps.setLong(1, orderDraft.getPersonId());
                        try {
                            ps.setString(2, objectMapper.writeValueAsString(orderDraft.getDraft()));
                        } catch (JsonProcessingException ex) {
                            throw new IllegalStateException("Can't convert order draft data to JSON");
                        }
                        return ps;
                    },
                    keyHolder
            );
            Number key = (Number) keyHolder.getKeys().get("order_draft_id");
            orderDraft.setId(key.longValue());
        }
        return orderDraft;
    }

    @Override
    public void delete(Long orderDraftId) {
        jdbcTemplate.update(
                getDeleteQuery(),
                orderDraftId
        );
    }

    @Override
    public Optional<OrderDraft> findOne(Long orderDraftId) {
        try {
            OrderDraft orderDraft = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{orderDraftId},
                    this
            );
            return Optional.of(orderDraft);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderDraft> findByPersonId(Long personId) {
        try {
            return jdbcTemplate.query(
                    getFindByPersonIdQuery(),
                    new Object[]{personId},
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private String getFindOneQuery() {
        return queryService.getQuery("select.order_draft");
    }

    private String getInsertQuery() {
        return queryService.getQuery("insert.order_draft");
    }

    private String getUpdateQuery() {
        return queryService.getQuery("update.order_draft");
    }

    private String getDeleteQuery() {
        return queryService.getQuery("delete.order_draft");
    }

    private String getFindByPersonIdQuery() {
        return queryService.getQuery("select.order_draft.by.person_id");
    }
}
