package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.model.OrderType;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OrderTypeDaoImpl implements OrderTypeDao, RowMapper<OrderType> {
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    public OrderTypeDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public OrderType mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderType orderType = new OrderType();
        orderType.setId(rs.getLong("order_type_id"));
        orderType.setName(rs.getString("name"));
        orderType.setMaxWeight(rs.getBigDecimal("max_weight"));
        orderType.setMaxWidth(rs.getLong("max_width"));
        orderType.setMaxHeight(rs.getLong("max_height"));
        orderType.setMaxLength(rs.getLong("max_length"));
        return orderType;
    }

    private Long mapRowIds(ResultSet rs, int rowNum) throws SQLException {
        OrderType orderType = new OrderType();
        orderType.setId(rs.getLong("order_type_id"));
        return orderType.getId();
    }

    @Override
    public OrderType save(OrderType orderType) {
        boolean hasPrimaryKey = orderType.getId() != null;
        if (hasPrimaryKey) {
            jdbcTemplate.update(
                    getUpsertQuery(),
                    ps -> {
                        ps.setLong(1, orderType.getId());
                        ps.setString(2, orderType.getName());
                        ps.setBigDecimal(3, orderType.getMaxWeight());
                        ps.setLong(4, orderType.getMaxWidth());
                        ps.setLong(5, orderType.getMaxHeight());
                        ps.setLong(6, orderType.getMaxLength());
                    }
            );
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, orderType.getName());
                ps.setBigDecimal(2, orderType.getMaxWeight());
                ps.setLong(3, orderType.getMaxWidth());
                ps.setLong(4, orderType.getMaxHeight());
                ps.setLong(5, orderType.getMaxLength());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("order_type_id");
            orderType.setId(key.longValue());
        }
        return orderType;
    }

    @Override
    public void delete(Long orderTypeId) {
        jdbcTemplate.update(
                getDeleteQuery(),
                ps -> ps.setObject(1, orderTypeId)
        );
    }

    @Override
    public Optional<OrderType> findOne(Long id) {
        try {
            OrderType orderType = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{id},
                    this
            );
            return Optional.of(orderType);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderType> findAll() {
        try {
            return jdbcTemplate.query(
                    getFindAllQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }


    @Override
    public List<Long> findAllIds() {
        try {
            return jdbcTemplate.query(
                    getFindAllIdsQuery(),
                    this::mapRowIds
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private String getUpsertQuery() {
        return queryService.getQuery("upsert.order_type");
    }

    private String getInsertQuery() {
        return queryService.getQuery("insert.order_type");
    }

    private String getDeleteQuery() {
        return queryService.getQuery("delete.order_type");
    }

    private String getFindOneQuery() {
        return queryService.getQuery("select.order_type");
    }

    private String getFindAllQuery() {
        return queryService.getQuery("all.order_type");
    }

    private String   getFindAllIdsQuery() {
        return queryService.getQuery("all.order_type.ids");
    }

}
