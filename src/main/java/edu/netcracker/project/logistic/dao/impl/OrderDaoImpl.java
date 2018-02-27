package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;

public class OrderDaoImpl implements OrderDao, QueryDao {

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;


    @Autowired
    public OrderDaoImpl(JdbcTemplate jdbcTemplate, QueryService queryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
    }


    private RowMapper<Order> getMapper() {
        return (resultSet, i) -> {

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

            Address address = new Address();
            address.setId(resultSet.getLong("address_id"));
            address.setName(resultSet.getString("address_name"));


            Office office = new Office();
            office.setOfficeId(resultSet.getLong("office_id"));
            office.setName(resultSet.getString("name"));
            office.setAddress(address);

            AddressContact addressContact = new AddressContact();
            addressContact.setAddress_contact_id(resultSet.getLong("address_contact_id"));
            addressContact.setAddress(address);
            addressContact.setContact(contact);

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
            order.setReceiver(addressContact);
            order.setSender(addressContact);
            order.setOffice(office);
            order.setOrderStatus(orderStatus);

            return order;
        };
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
