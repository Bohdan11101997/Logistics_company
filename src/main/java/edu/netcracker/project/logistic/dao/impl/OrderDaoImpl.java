package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class OrderDaoImpl implements OrderDao, QueryDao, RowMapper<Order> {

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;

    public OrderDaoImpl(JdbcTemplate jdbcTemplate, QueryService queryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
    }

    public Order mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(resultSet.getLong("person_id"));
        person.setUserName(resultSet.getString("user_name"));
        person.setPassword(resultSet.getString("password"));
        person.setRegistrationDate(resultSet.getTimestamp("registration_date").toLocalDateTime());

        Contact contact = new Contact();
        contact.setContactId(resultSet.getLong("contact_id"));
        contact.setFirstName(resultSet.getString("first_name"));
        contact.setLastName(resultSet.getString("last_name"));
        contact.setPhoneNumber(resultSet.getString("phone_number"));

        Address senderAddress = new Address();
        senderAddress.setId(resultSet.getLong("sender_address_id"));
        senderAddress.setName(resultSet.getString("sender_address_name"));

        Address receiverAddress = new Address();
        receiverAddress.setId(resultSet.getLong("receiver_address_id"));
        receiverAddress.setName(resultSet.getString("receiver_address_name"));

        Contact senderContact = new Contact();
        senderContact.setContactId(resultSet.getLong("sender_contact_id"));
        senderContact.setFirstName(resultSet.getString("sender_first_name"));
        senderContact.setLastName(resultSet.getString("sender_last_name"));
        senderContact.setEmail(resultSet.getString("sender_email"));
        senderContact.setPhoneNumber(resultSet.getString("sender_phone_number"));

        Contact receiverContact = new Contact();
        receiverContact.setContactId(resultSet.getLong("receiver_contact_id"));
        receiverContact.setFirstName(resultSet.getString("receiver_first_name"));
        receiverContact.setLastName(resultSet.getString("receiver_last_name"));
        receiverContact.setEmail(resultSet.getString("receiver_email"));
        receiverContact.setPhoneNumber(resultSet.getString("receiver_phone_number"));

        Office office = new Office();
        office.setOfficeId(resultSet.getLong("office_id"));
        office.setName(resultSet.getString("name"));

        person.setContact(contact);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setId(resultSet.getLong("order_status_id"));
        orderStatus.setName(resultSet.getString("status_name"));

        Order order = new Order();
        order.setId(resultSet.getLong("order_id"));
        order.setCreationDay(resultSet.getDate("creation_date").toLocalDate());
        order.setDeliveryTime(resultSet.getDate("delivery_time").toLocalDate());
        order.setOrderStatusTime(resultSet.getDate("order_status_time").toLocalDate());
        order.setCourier(person);
        order.setSenderAddress(senderAddress);
        order.setReceiverAddress(receiverAddress);
        order.setSenderContact(senderContact);
        order.setReceiverContact(receiverContact);
        order.setOffice(office);
        order.setOrderStatus(orderStatus);

        return order;
    }

    @Override
    public Order save(Order object) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }




    @Override
    public Optional<Order> findOne(Long aLong) {
        return Optional.empty();
    }


    @Override
    public String getInsertQuery() {
        return null;
    }

    @Override
    public String getUpsertQuery() {
        return null;
    }

    @Override
    public String getDeleteQuery() {
        return null;
    }

    @Override
    public String getFindOneQuery() {
        return null;
    }




}
