package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.ManagerStatisticsDao;
import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.OrderStatusDao;
import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Repository
public class ManagerStatisticsDaoImpl implements ManagerStatisticsDao {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private QueryService queryService;
    private RowMapper<Contact> contactMapper;
    private RowMapper<Role> roleMapper;
    private OrderDao orderDao;
    private OrderTypeDao orderTypeDao;
    private OrderStatusDao orderStatusDao;

    @Autowired
    public ManagerStatisticsDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, QueryService queryService, RowMapper<Contact> contactMapper, RowMapper<Role> roleMapper, OrderDao orderDao, OrderTypeDao orderTypeDao, OrderStatusDao orderStatusDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.queryService = queryService;
        this.contactMapper = contactMapper;
        this.roleMapper = roleMapper;
        this.orderDao = orderDao;
        this.orderTypeDao = orderTypeDao;
        this.orderStatusDao = orderStatusDao;
    }

    private List<Person> extractMany(ResultSet rs) throws SQLException {
        List<Person> result = new ArrayList<>();
        boolean rowsLeft = rs.next();
        for (int i = 0; rowsLeft; i++) {
            Person person = new Person();
            person.setId(rs.getLong("person_id"));
            person.setCount(rs.getLong("count_orders"));
            person.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());

            Contact contact = contactMapper.mapRow(rs, i);
            person.setContact(contact);

            Set<Role> roles = new HashSet<>();
            do {
                roles.add(roleMapper.mapRow(rs, i));
                rowsLeft = rs.next();
            } while (rowsLeft && rs.getLong("person_id") == person.getId());
            person.setRoles(roles);
            result.add(person);
        }
        return result;
    }

    private Contact mapContact(ResultSet rs, String prefix) throws SQLException {
        Contact c = new Contact();
        c.setFirstName(rs.getString(prefix + "first_name"));
        c.setLastName(rs.getString(prefix + "last_name"));
        return c;
    }


    private List<StatisticTask> extractOrderForStatistic(ResultSet rs) throws SQLException {
        List<StatisticTask> result = new ArrayList<>();
        boolean rowsLeft = rs.next();
        for (int i = 0; rowsLeft; i++) {
            Person person = new Person();
            person.setId(rs.getLong("employee_id"));
            person.setUserName(rs.getString("handler_employee"));

            OrderType orderType = new OrderType();
            orderType.setName(rs.getString("name"));

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setName(rs.getString("status_name"));

            Office office = new Office();
            office.setName(rs.getString("office_name"));

            Order order = new Order();
            order.setId(rs.getLong("order_id"));
            order.setCreationTime(rs.getTimestamp("creation_time").toLocalDateTime());
            order.setWeight(rs.getBigDecimal("weight"));
            order.setCapacity(rs.getLong("capacity"));
            order.setSenderContact(mapContact(rs, "sender_"));
            order.setReceiverContact(mapContact(rs, "receiver_"));
            order.setOffice(office);
            order.setOrderStatus(orderStatus);

            order.setOrderType(orderType);
            Set<Role> roles = new HashSet<>();
            do {
                roles.add(roleMapper.mapRow(rs, i));
                rowsLeft = rs.next();
            } while (rowsLeft && rs.getLong("person_id") == person.getId());
            person.setRoles(roles);

            StatisticTask statisticTask = new StatisticTask();
            statisticTask.setPerson(person);
            statisticTask.setOrder(order);
            result.add(statisticTask);
        }
        return result;

    }


    @Override
    public List<StatisticTask> searchStatisticOrders(SearchFormOrderStatistic searchFormOrderStatistic) {
        List<Long> ids = new ArrayList<>();


        ids.add(searchFormOrderStatistic.getIds());
        if (ids.contains(null)) {

            ids.addAll(orderDao.getAllOrderIds());
        }


        List<Long> status_OrdersList = new ArrayList<>(searchFormOrderStatistic.getOrder_statusIds());
        if (status_OrdersList.isEmpty()) {

            status_OrdersList.addAll(orderStatusDao.findAllIds());
        }

        List<Long> destination_typeIds = new ArrayList<>(searchFormOrderStatistic.getDestination_typeIds());
        if (destination_typeIds.isEmpty()) {

            destination_typeIds.addAll(orderTypeDao.findAllIds());
        }


        LocalDateTime from = searchFormOrderStatistic.getFrom();
        if (from == null) {
            from = LocalDateTime.MIN;
        } else {
            from = from.with(LocalTime.MIN);
        }

        LocalDateTime to = searchFormOrderStatistic.getTo();
        if (to == null) {
            to = LocalDateTime.now();
        } else {
            to = to.with(LocalTime.MAX);
        }

        Map<String, Object> paramMap = new HashMap<>(7);
        paramMap.put("id", ids);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        paramMap.put("destination_type", destination_typeIds);
        paramMap.put("order_status", status_OrdersList);

        if (searchFormOrderStatistic.getSortIds() == 1) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryByDateRangeOrderById(),
                        paramMap,
                        this::extractOrderForStatistic
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();
            }
        } else if (searchFormOrderStatistic.getSortIds() == 2) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryByDateRangeOrderByCreationDate(),
                        paramMap,
                        this::extractOrderForStatistic
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();
            }
        } else if (searchFormOrderStatistic.getSortIds() == 3) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryByDateRangeOrderByWeight(),
                        paramMap,
                        this::extractOrderForStatistic
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();
            }
        } else if (searchFormOrderStatistic.getSortIds() == 4) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryByDateRangeOrderByCapacity(),
                        paramMap,
                        this::extractOrderForStatistic
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();
            }
        } else {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryByDateRange(),
                        paramMap,
                        this::extractOrderForStatistic
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();
            }
        }

    }


    private String prepareSearchString(String input) {
        return "%" + input.replace("%", "\\%") + "%";
    }

    @Override
    public List<Person> searchStatisticForManager(SearchFormStatisticEmployee searchFormStatisticEmployee) {
        String firstName = searchFormStatisticEmployee.getFirstName();
        firstName = firstName == null ? "%%" : prepareSearchString(firstName.trim());

        String lastName = searchFormStatisticEmployee.getLastName();
        lastName = lastName == null ? "%%" : prepareSearchString(lastName.trim());

        Map<String, Object> paramMap = new HashMap<>(6);
        paramMap.put("first_name", firstName);
        paramMap.put("last_name", lastName);
        paramMap.put("role_ids", searchFormStatisticEmployee.getRoleIds());

        if (searchFormStatisticEmployee.getSortId() == 1) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryForManagerSortByRegistration(),
                        paramMap,
                        this::extractMany
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();

            }
        } else if (searchFormStatisticEmployee.getSortId() == 2) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryHandlerOrder(),
                        paramMap,
                        this::extractMany
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();

            }
        } else
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryForManager(),
                        paramMap,
                        this::extractMany
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();

            }
    }


    @Override
    public Integer countOrdersBetweenDate(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getOrderBetweenData(),
                paramMap, Integer.class);
    }

    @Override
    public List<Person> employeesByCourierOrCall_Center() {
        return jdbcTemplate.query(
                getQueryEmployeesByCourierOrCall_Center(),
                this::extractMany);
    }

    @Override
    public Integer countOrdersHandToHand(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountQueryOrdersHandToHand(),
                paramMap, Integer.class);


    }

    @Override
    public Integer countOrdersFromOffice(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountQueryOrdersFromOffice(),
                paramMap, Integer.class);

    }

    @Override
    public Integer countEmployees(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountEmployeesQuery(),
                paramMap, Integer.class);

    }

    @Override
    public Integer countEmployeesAdmins(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountEmployeesAdminsQuery(),
                paramMap, Integer.class);

    }

    @Override
    public Integer countEmployeesCouriers(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountEmployeesCouriersQuery(),
                paramMap, Integer.class);
    }

    @Override
    public Integer countEmployeesCouriersDriving(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountEmployeesCouriersDrivingQuery(),
                paramMap, Integer.class);

    }

    @Override
    public Integer countEmployeesCouriersWalking(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountEmployeesCouriersWalkingQuery(),
                paramMap, Integer.class);
    }

    @Override
    public Integer countEmployeesManagers(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountEmployeesManagerQuery(),
                paramMap, Integer.class);

    }

    @Override
    public Integer countEmployeesAgentCallCenter(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountEmployeesAgentCallCenterQuery(),
                paramMap, Integer.class);


    }

    @Override
    public Double avarageWeightDocument() {
        return jdbcTemplate.queryForObject(
                avarageWeightDocumentQuery(), new Object[]{}, Double.class);

    }

    @Override
    public Double avarageCapacityDocument() {
        return jdbcTemplate.queryForObject(
                avarageCapacityDocumentQuery(), new Object[]{}, Double.class);

    }


    @Override
    public Double avarageWeightPackage() {
        return jdbcTemplate.queryForObject(
                avarageWeightPackageQuery(), new Object[]{}, Double.class);

    }

    @Override
    public Double avarageCapacityPackage() {
        return jdbcTemplate.queryForObject(
                avarageCapacityPackageQuery(), new Object[]{}, Double.class);

    }

    @Override
    public Double avarageWeightCargo() {
        return jdbcTemplate.queryForObject(
                avarageWeightCargoQuery(), new Object[]{}, Double.class);

    }

    @Override
    public Double avarageCapacityCargo() {
        return jdbcTemplate.queryForObject(
                avarageCapacityCargoQuery(), new Object[]{}, Double.class);

    }


    @Override
    public Integer countOffices() {
        return jdbcTemplate.queryForObject(
                getCountOfficeQuery(), new Object[]{}, Integer.class);

    }


    @Override
    public Integer countUsers(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountUsersQuery(),
                paramMap, Integer.class);


    }

    @Override
    public Integer countUsersNormal(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountNormalUsersQuery(),
                paramMap, Integer.class);

    }

    @Override
    public Integer countUsersVip(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountVipUsersQuery(),
                paramMap, Integer.class);

    }

    @Override
    public Integer countUnregisteredContacts(LocalDateTime from, LocalDateTime to) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return namedParameterJdbcTemplate.queryForObject(getCountUnregisteredContactsQuery(),
                paramMap, Integer.class);
    }

    @Override
    public Integer countOrders() {
        return jdbcTemplate.queryForObject(
                getCountOrderQuery(), new Object[]{}, Integer.class);
    }


    private String getCountUnregisteredContactsQuery() {
        return queryService.getQuery("count.unregistered.contact");
    }


    private String getCountUsersQuery() {
        return queryService.getQuery("count.users");
    }


    private String getCountNormalUsersQuery() {
        return queryService.getQuery("count.users.normal");
    }

    private String getCountVipUsersQuery() {
        return queryService.getQuery("count.users.vip");
    }

    private String getCountEmployeesAgentCallCenterQuery() {
        return queryService.getQuery("count.employees.agent.call.center");
    }

    private String getCountEmployeesCouriersDrivingQuery() {
        return queryService.getQuery("count.employees.couriers.driving");
    }

    private String getCountEmployeesCouriersWalkingQuery() {
        return queryService.getQuery("count.employees.couriers.walking");
    }

    private String getCountEmployeesCouriersQuery() {
        return queryService.getQuery("count.employees.couriers");
    }

    private String getCountEmployeesQuery() {
        return queryService.getQuery("count.all.employee");
    }

    private String getCountEmployeesAdminsQuery() {
        return queryService.getQuery("count.employees.admins");
    }

    private String getSearchQueryForManager() {
        return queryService.getQuery("select.person.search.statistic");
    }


    private String getCountEmployeesManagerQuery() {
        return queryService.getQuery("count.employees.managers");
    }


    private String avarageWeightDocumentQuery() {
        return queryService.getQuery("count.avg.weight.document");
    }

    private String avarageCapacityDocumentQuery() {
        return queryService.getQuery("count.avg.capacity.document");
    }

    private String avarageWeightPackageQuery() {
        return queryService.getQuery("count.avg.weight.package");
    }

    private String avarageCapacityPackageQuery() {
        return queryService.getQuery("count.avg.capacity.package");
    }

    private String avarageWeightCargoQuery() {
        return queryService.getQuery("count.avg.weight.cargo");
    }

    private String avarageCapacityCargoQuery() {
        return queryService.getQuery("count.avg.capacity.cargo");
    }

    private String getCountOfficeQuery() {
        return queryService.getQuery("count.offices");
    }

    private String getCountOrderQuery() {
        return queryService.getQuery("count.order");
    }


    private String getSearchQueryForManagerSortByRegistration() {
        return queryService.getQuery("select.person.search.statistic.order.by.registration");
    }

    private String getSearchQueryHandlerOrder() {
        return queryService.getQuery("select.person.search.statistic.order.by.order.handled");
    }


    private String getSearchQueryByDateRangeOrderById() {
        return queryService.getQuery("select.manager.statistic.filter.order.order.by.id");
    }

    private String getSearchQueryByDateRangeOrderByCreationDate() {
        return queryService.getQuery("select.manager.statistic.filter.order.order.by.creation.date");
    }

    private String getSearchQueryByDateRangeOrderByWeight() {
        return queryService.getQuery("select.manager.statistic.filter.order.order.by.weight");
    }

    private String getSearchQueryByDateRangeOrderByCapacity() {
        return queryService.getQuery("select.manager.statistic.filter.order.order.by.capacity");
    }

    private String getSearchQueryByDateRange() {
        return queryService.getQuery("select.manager.statistic.filter.order");
    }


    private String getCountQueryOrdersHandToHand() {
        return queryService.getQuery("count.order.hand.to.hand");
    }

    private String getCountQueryOrdersFromOffice() {
        return queryService.getQuery("count.orders.from.office");
    }

    private String getQueryEmployeesByCourierOrCall_Center() {
        return queryService.getQuery("select.employee.call_center.couriers");
    }

    private String getOrderBetweenData() {
        return queryService.getQuery("count.orders.by.date");
    }

}