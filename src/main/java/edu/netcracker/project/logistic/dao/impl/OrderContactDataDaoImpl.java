package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OrderContactDataDao;
import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.OrderStatus;
import edu.netcracker.project.logistic.model.order.OrderContactData;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OrderContactDataDaoImpl implements OrderContactDataDao, RowMapper<OrderContactData> {
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    public OrderContactDataDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private Contact mapContact(ResultSet rs, String prefix) throws SQLException {
        Contact c = new Contact();
        c.setContactId(rs.getLong(prefix + "contact_id"));
        c.setFirstName(rs.getString(prefix + "first_name"));
        c.setLastName(rs.getString(prefix + "last_name"));
        c.setEmail(rs.getString(prefix + "email"));
        c.setPhoneNumber(rs.getString(prefix + "phone_number"));
        return c;
    }

    private Address mapAddress(ResultSet rs, String prefix) throws SQLException {
        Address a = new Address();
        Address address = new Address();
        address.setId(rs.getLong(prefix + "address_id"));
        address.setName(rs.getString(prefix + "address_name"));
        return address;
    }

    @Override
    public OrderContactData mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderContactData orderContactData = new OrderContactData();
        orderContactData.setId(rs.getLong("order_id"));
        orderContactData.setCreationDate(rs.getObject("creation_date", LocalDateTime.class));
        orderContactData.setDeliveryTime(rs.getObject("delivery_time", LocalTime.class));
        orderContactData.setOrderStatusTime(rs.getObject("order_status_time", LocalDateTime.class));
        orderContactData.setWeight(rs.getBigDecimal("weight"));
        orderContactData.setWidth(rs.getLong("width"));
        orderContactData.setHeight(rs.getLong("height"));
        orderContactData.setLength(rs.getLong("length"));
        orderContactData.setOrderTypeId(rs.getLong("order_type_id"));

        orderContactData.setSenderContact(mapContact(rs, "sender_"));
        orderContactData.setReceiverContact(mapContact(rs, "receiver_"));
        orderContactData.setSenderAddress(mapAddress(rs, "sender_"));
        orderContactData.setReceiverAddress(mapAddress(rs, "receiver_"));

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setId(rs.getLong("order_status_id"));
        orderStatus.setName(rs.getString("status_name"));
        orderContactData.setOrderStatus(orderStatus);

        return orderContactData;
    }

    @Override
    public OrderContactData save(OrderContactData object) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public Optional<OrderContactData> findOne(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<OrderContactData> findNotProcessed() {
        try {
            return jdbcTemplate.query(
                    getFindNotProcessedQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private String getFindNotProcessedQuery() { return queryService.getQuery("select.order_contact_data.unprocessed"); }
}
