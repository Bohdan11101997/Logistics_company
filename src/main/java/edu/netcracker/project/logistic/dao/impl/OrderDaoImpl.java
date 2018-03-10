package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.model.*;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OrderDaoImpl implements OrderDao, RowMapper<Order> {
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    public OrderDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private Contact mapContact(ResultSet rs, String prefix) throws SQLException {
        Contact c = new Contact();
        c.setContactId(rs.getLong(prefix + "contact_id"));
        if (c.getContactId() == null) {
            return null;
        }
        c.setFirstName(rs.getString(prefix + "first_name"));
        c.setLastName(rs.getString(prefix + "last_name"));
        c.setEmail(rs.getString(prefix + "email"));
        c.setPhoneNumber(rs.getString(prefix + "phone_number"));
        return c;
    }

    private Address mapAddress(ResultSet rs, String prefix) throws SQLException {
        Address address = new Address();
        address.setId(rs.getLong(prefix + "address_id"));
        if (address.getId() == null) {
            return null;
        }
        address.setName(rs.getString(prefix + "address_name"));
        return address;
    }

    private Office mapOffice(ResultSet rs) throws SQLException {
        Office office = new Office();
        office.setOfficeId(rs.getLong("office_id"));
        if (office.getOfficeId() == null) {
            return null;
        }
        office.setName(rs.getString("office_name"));
        return office;
    }

    private Person mapPerson(ResultSet rs) throws SQLException {
        Person courier = new Person();
        courier.setId(rs.getLong("courier_id"));
        if (courier.getId() == null) {
            return null;
        }
        courier.setUserName(rs.getString("courier_user_name"));
        courier.setPassword(rs.getString("courier_password"));
        Contact contact = new Contact();
        contact.setContactId(rs.getLong("courier_contact_id"));
        courier.setContact(contact);
        return courier;
    }

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("order_id"));
        order.setCreationTime(rs.getObject("creation_time", LocalDateTime.class));
        order.setDeliveryTime(rs.getObject("delivery_time", LocalDateTime.class));
        order.setOrderStatusTime(rs.getObject("order_status_time", LocalDateTime.class));
        order.setWeight(rs.getBigDecimal("weight"));
        order.setWidth(rs.getLong("width"));
        order.setHeight(rs.getLong("height"));
        order.setLength(rs.getLong("length"));
        order.setOrderTypeId(rs.getLong("order_type_id"));

        order.setSenderContact(mapContact(rs, "sender_"));
        order.setReceiverContact(mapContact(rs, "receiver_"));
        order.setSenderAddress(mapAddress(rs, "sender_"));
        order.setReceiverAddress(mapAddress(rs, "receiver_"));
        order.setOffice(mapOffice(rs));
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setId(rs.getLong("order_status_id"));
        orderStatus.setName(rs.getString("status_name"));
        order.setOrderStatus(orderStatus);
        order.setCourier(mapPerson(rs));

        return order;
    }

    private void prepareUpdate(PreparedStatement ps, Order order, boolean hasPrimaryKey) throws SQLException {
        ps.setLong(1, order.getId());
        ps.setObject(2, order.getCreationTime());
        ps.setObject(3, order.getDeliveryTime());
        ps.setObject(4, order.getOrderStatus().getId());
        ps.setObject(5, order.getCourier() == null ? null : order.getCourier().getId());
        ps.setObject(6, order.getReceiverContact() == null ? null : order.getReceiverContact().getContactId());
        ps.setObject(7, order.getSenderContact() == null ? null : order.getSenderContact().getContactId());
        ps.setObject(8, order.getReceiverAddress() == null ? null : order.getReceiverAddress().getId());
        ps.setObject(9, order.getSenderAddress() == null ? null : order.getSenderAddress().getId());
        ps.setObject(10, order.getOffice() == null ? null : order.getOffice().getOfficeId());
        ps.setLong(11, order.getOrderStatus().getId());
        ps.setLong(12, order.getOrderTypeId());
        ps.setBigDecimal(13, order.getWeight());
        ps.setLong(14, order.getWidth());
        ps.setLong(15, order.getHeight());
        ps.setLong(16, order.getLength());
        if (hasPrimaryKey) {
            ps.setLong(17, order.getId());
        }
    }

    @Override
    public Order save(Order order) {
        boolean hasPrimaryKey = order.getId() == null;
        if (hasPrimaryKey) {
            jdbcTemplate.update(
                    getUpdateQuery(),
                    ps -> prepareUpdate(ps, order, true)
            );
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    psc -> {
                        PreparedStatement ps = psc.prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS);
                        prepareUpdate(ps, order, false);
                        return ps;
                    },
                    keyHolder
            );
            Number key = (Number) keyHolder.getKeys().get("order_id");
            order.setId(key.longValue());
        }
        return order;
    }

    @Override
    public void delete(Long orderId) {
        jdbcTemplate.update(
                getDeleteQuery(),
                ps -> ps.setLong(1, orderId)
        );
    }

    @Override
    public Optional<Order> findOne(Long orderId) {
        try {
            Order order = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{orderId},
                    this
            );
            return Optional.of(order);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findNotProcessed() {
        try {
            return jdbcTemplate.query(
                    getFindNotProcessedQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private String getFindOneQuery() {
        return queryService.getQuery("select.order");
    }

    private String getInsertQuery() {
        return queryService.getQuery("insert.order");
    }

    private String getUpdateQuery() {
        return queryService.getQuery("update.order");
    }

    private String getDeleteQuery() {
        return queryService.getQuery("delete.order");
    }

    private String getFindNotProcessedQuery() {
        return queryService.getQuery("select.order.not_processed");
    }
}
