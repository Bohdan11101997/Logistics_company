package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.OrderStatusDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
public class OrderDaoImpl implements OrderDao, RowMapper<Order> {
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public OrderDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    private Contact mapContact(ResultSet rs, String prefix) throws SQLException {
        Contact c = new Contact();
        c.setContactId(rs.getLong(prefix + "contact_id"));
        if (rs.wasNull()) {
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
        if (rs.wasNull()) {
            return null;
        }
        address.setName(rs.getString(prefix + "address_name"));
        return address;
    }

    private Office mapOffice(ResultSet rs) throws SQLException {
        Office office = new Office();
        office.setOfficeId(rs.getLong("office_id"));
        if (rs.wasNull()) {
            return null;
        }
        office.setName(rs.getString("office_name"));
        return office;
    }


    private Person mapPerson(ResultSet rs) throws SQLException {
        Person courier = new Person();
        courier.setId(rs.getLong("courier_id"));
        if (rs.wasNull()) {
            return null;
        }
        courier.setUserName(rs.getString("courier_user_name"));
        courier.setPassword(rs.getString("courier_password"));
        Contact contact = new Contact();
        contact.setContactId(rs.getLong("courier_contact_id"));
        courier.setContact(contact);
        return courier;
    }

    private OrderType mapOrderType(ResultSet rs) throws SQLException {
        OrderType orderType = new OrderType();
        orderType.setId(rs.getLong("order_type_id"));
        if (rs.wasNull()) {
            return null;
        }
        orderType.setName(rs.getString("order_type_name"));
        orderType.setMaxWeight(rs.getBigDecimal("max_weight"));
        orderType.setMaxWidth(rs.getLong("max_width"));
        orderType.setMaxHeight(rs.getLong("max_height"));
        orderType.setMaxLength(rs.getLong("max_length"));
        return orderType;
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
        order.setOrderType(mapOrderType(rs));

        return order;
    }

    private void prepareUpdate(PreparedStatement ps, Order order, boolean hasPrimaryKey) throws SQLException {
        ps.setObject(1, order.getCreationTime().toLocalDate());
        ps.setObject(2, order.getDeliveryTime());
        ps.setObject(3, order.getOrderStatusTime());
        ps.setObject(4, order.getCourier() == null ? null : order.getCourier().getId());
        ps.setObject(5, order.getReceiverContact() == null ? null : order.getReceiverContact().getContactId());
        ps.setObject(6, order.getReceiverAddress() == null ? null : order.getReceiverAddress().getId());
        ps.setObject(7, order.getSenderContact() == null ? null : order.getSenderContact().getContactId());
        ps.setObject(8, order.getSenderAddress() == null ? null : order.getSenderAddress().getId());
        ps.setObject(9, order.getOffice() == null ? null : order.getOffice().getOfficeId());
        ps.setLong(10, order.getOrderStatus().getId());
        ps.setObject(11, order.getOrderType() == null ? null : order.getOrderType().getId());
        ps.setBigDecimal(12, order.getWeight());
        ps.setObject(13, order.getWidth());
        ps.setObject(14, order.getHeight());
        ps.setObject(15, order.getLength());
        if (hasPrimaryKey) {
            ps.setLong(16, order.getId());
        }
    }

    @Override
    public Order save(Order order) {
        boolean hasPrimaryKey = order.getId() != null;
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
    public List<Order> findConfirmed() {
        try {
            return jdbcTemplate.query(
                    getFindConfirmedQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
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


    private String prepareSearchString(String input) {
        return "%" + input.replace("%", "\\%") + "%";
    }

    @Override
    public List<Order> search(SearchFormOrder searchFormOrder, Long id) {

        String firstName = searchFormOrder.getFirstName();
        firstName = firstName == null ? "%%" : prepareSearchString(firstName.trim());

        String lastName = searchFormOrder.getLastName();
        lastName = lastName == null ? "%%" : prepareSearchString(lastName.trim());

        LocalDateTime from = searchFormOrder.getFrom();
        if (from == null) {
            from = LocalDateTime.MIN;
        } else {
            from = from.with(LocalTime.MIN);
        }

        LocalDateTime to = searchFormOrder.getTo();
        if (to == null) {
            to = LocalDateTime.now();
        } else {
            to = to.with(LocalTime.MAX);
        }

        List<Long> destination_type = searchFormOrder.getDestination_typeIds();

        Map<String, Object> paramMap = new HashMap<>(9);
        paramMap.put("first_name_contact", firstName);
        paramMap.put("last_name_contact", lastName);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        paramMap.put("destination_type", destination_type);
        paramMap.put("order_status", searchFormOrder.getOrder_statusIds());
        paramMap.put("sender_contact_id", id);
        paramMap.put("receiver_contact_id", id);

        if (searchFormOrder.getContact_side() == 1) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchSenderQuery(),
                        paramMap,
                        this

                );

            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();
            }
        } else if (searchFormOrder.getContact_side() == 2) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchReceiverQuery(),
                        paramMap,
                        this

                );

            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();
            }
        }
            else if (searchFormOrder.getContact_side() == 3) {
                try {
                    return namedParameterJdbcTemplate.query(
                            getSearchFromOfficeQuery(),
                            paramMap,
                            this

                    );

                } catch (EmptyResultDataAccessException ex) {
                    return Collections.emptyList();
                }
        } else {

            return orderBySenderOrReceiver(id);
        }
    }


    @Override
    public List<Order> findNotProcessedByEmployeeId(Long employeeId) {
        return jdbcTemplate.query(
                getFindByEmployeeIdNotProcessedQuery(),
                new Object[]{employeeId},
                this
        );
    }

    @Override
    public List<Order> findConfirmedByEmployeeId(Long employeeId) {
        return jdbcTemplate.query(
                getFindByEmployeeIdConfirmedQuery(),
                new Object[]{employeeId},
                this
        );
    }

    @Override
    public List<Order> orderBySenderOrReceiver(Long aLong) {
        try {
            return jdbcTemplate.query(
                    getOrderByUser(),
                    new Object[]{aLong, aLong},
                    this
            );
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }


    private String getOrderByUser() {
        return queryService.getQuery("select.order.by.user");
    }

    private String getFindByEmployeeIdNotProcessedQuery() {
        return queryService.getQuery("select.order.not_processed.by.employee_id");
    }

    private String getFindByEmployeeIdConfirmedQuery() {
        return queryService.getQuery("select.order.confirmed.by.employee_id");
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

    private String getSearchSenderQuery() {
        return queryService.getQuery("select.order.search.sender");
    }

    private String getSearchReceiverQuery() {
        return queryService.getQuery("select.order.search.receiver");
    }

    private String getSearchFromOfficeQuery() {
        return queryService.getQuery("select.order.search.from.office");
    }

    private String getFindNotProcessedQuery() {
        return queryService.getQuery("select.order.not_processed");
    }

    private String getFindConfirmedQuery() {
        return queryService.getQuery("select.order.confirmed");
    }
}
