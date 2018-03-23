package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.PersonRoleDao;
import edu.netcracker.project.logistic.dao.WorkDayDao;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;



@Repository
public class ManagerStatisticsDaoImpl {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private QueryService queryService;
    private PersonRoleDao personRoleDao;
    private RowMapper<Contact> contactMapper;
    private RowMapper<Role> roleMapper;
    private RowMapper<Order> orderRowMapper;



    @Autowired
    WorkDayDao workDayDao;

    @Autowired
    ManagerStatisticsDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             QueryService queryService, PersonRoleDao personRoleDao,
                             RowMapper<Contact> contactMapper, RowMapper<Role> roleMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.queryService = queryService;
        this.personRoleDao = personRoleDao;
        this.contactMapper = contactMapper;
        this.roleMapper = roleMapper;
    }

    private List<Person> extractMany1(ResultSet rs) throws SQLException {
        List<Person> result = new ArrayList<>();
        boolean rowsLeft = rs.next();
        for (int i = 0; rowsLeft; i++) {
            Person person = new Person();
            person.setId(rs.getLong("person_id"));
            person.setId(rs.getLong("count_orders"));
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




    private List<Statistic_task> extractOrderForStatistic(ResultSet rs) throws SQLException {

        List<Statistic_task> result = new ArrayList<>();
        boolean rowsLeft = rs.next();
        for (int i = 0; rowsLeft; i++) {
            Person person = new Person();
            person.setId(rs.getLong("person_id"));
            person.setUserName(rs.getString("user_name"));

            Contact contact = new Contact();
            contact.setFirstName(rs.getString("first_name"));
            contact.setFirstName(rs.getString("last_name"));

            OrderType orderType = new OrderType();
            orderType.setName(rs.getString("name"));

            Order order = new Order();
            order.setId(rs.getLong("order_id"));
//            order.setDeliveryTime(rs.getTimestamp("estimated_delivery_time").toLocalDateTime());
            order.setReceiverContact(contact);
            order.setSenderContact(contact);
            order.setOrderType(orderType);
            Set<Role> roles = new HashSet<>();
            do {
                roles.add(roleMapper.mapRow(rs, i));
                rowsLeft = rs.next();
            } while (rowsLeft && rs.getLong("person_id") == person.getId());
            person.setRoles(roles);

            Statistic_task statistic_task = new Statistic_task();
            statistic_task.setPerson(person);
            statistic_task.setOrder(order);
            result.add(statistic_task);
        }
        return result;

    }

    public List<Statistic_task> searchStatisiticOrders(SearchFormOrderStatistic searchFormOrderStatistic) {
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
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        paramMap.put("destination_type", searchFormOrderStatistic.getDestination_typeIds());
        paramMap.put("order_status", searchFormOrderStatistic.getOrder_statusIds());
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



    private String prepareSearchString(String input) {
        return "%" + input.replace("%", "\\%") + "%";
    }

    public List<Person> searchStatisticForManager(SearchFormStatisticEmployee searchFormStatisticEmployee) {


        String firstName = searchFormStatisticEmployee.getFirstName();
        firstName = firstName == null ? "%%" : prepareSearchString(firstName.trim());

        String lastName = searchFormStatisticEmployee.getLastName();
        lastName = lastName == null ? "%%" : prepareSearchString(lastName.trim());

        Map<String, Object> paramMap = new HashMap<>(6);
        paramMap.put("first_name", firstName);
        paramMap.put("last_name", lastName);
        paramMap.put("role_ids", searchFormStatisticEmployee.getRoleIds());

        for (Map.Entry<String, Object> pair : paramMap.entrySet()) {
            Object value = pair.getValue();
            System.out.println(value);
        }
        System.out.println(searchFormStatisticEmployee.getSortId());
        if (searchFormStatisticEmployee.getSortId() == 1) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryForManagerSortByRegistration(),
                        paramMap,
                        this::extractMany1
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();

            }
        } else if (searchFormStatisticEmployee.getSortId() == 2) {
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryHendlerOrder(),
                        paramMap,
                        this::extractMany1
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();

            }
        } else
            try {
                return namedParameterJdbcTemplate.query(
                        getSearchQueryForManager(),
                        paramMap,
                        this::extractMany1
                );
            } catch (EmptyResultDataAccessException ex) {
                return Collections.emptyList();

            }
    }

    public Integer CountOrdersBetweenDate(LocalDateTime from, LocalDateTime to)
    {
        Map<String, Object> paramMap = new HashMap<>(7);
        paramMap.put("start_date", from);
        paramMap.put("end_date", to);
        return  namedParameterJdbcTemplate.queryForObject(getOrderBetweenData(),
                paramMap, Integer.class);
    }



    public List<Person> EmployeesByCourierOrCall_Center() {

        return jdbcTemplate.query(
                getQueryEmployeesByCourierOrCall_Center(),
             this::extractMany1);
    }

    public Integer countOrdersHandtoHand() {

        return jdbcTemplate.queryForObject(
                getCountQueryOrdersHandtoHand(), new Object[]{}, Integer.class);

    }


    public Integer countOrdersFromOffice() {

        return jdbcTemplate.queryForObject(
                getCountQueryOrdersFromOffice(), new Object[]{}, Integer.class);

    }

    public Integer countEmployees() {

        return jdbcTemplate.queryForObject(
                getCountEmployeesQuery(), new Object[]{}, Integer.class);

    }

    public Integer countEmployeesAdmins() {
        return jdbcTemplate.queryForObject(
                getCountEmployeesAdminsQuery(), new Object[]{}, Integer.class);
    }

    public Integer countEmployeesCouriers() {

        return jdbcTemplate.queryForObject(
                getCountEmployeesCouriersQuery(), new Object[]{}, Integer.class);

    }

    public Integer countEmployeesCouriersDriving() {
        return jdbcTemplate.queryForObject(
                getCountEmployeesCouriersDrivingQuery(), new Object[]{}, Integer.class);

    }

    public Integer countEmployeesCouriersWalking() {

        return jdbcTemplate.queryForObject(
                getCountEmployeesCouriersWalkingQuery(), new Object[]{}, Integer.class);

    }

    public Integer countEmployeesManagers() {

        return jdbcTemplate.queryForObject(
                getCountEmployeesManagerQuery(), new Object[]{}, Integer.class);

    }

    public Integer countOffices() {

        return jdbcTemplate.queryForObject(
                getCountOfficeQuery(), new Object[]{}, Integer.class);

    }

    public Integer countUsers() {

        return jdbcTemplate.queryForObject(
                getCountUsersQuery(), new Object[]{}, Integer.class);

    }

    public Integer countUsersNormal() {

        return jdbcTemplate.queryForObject(
                getCountNormalUsersQuery(), new Object[]{}, Integer.class);

    }

    public Integer countUsersVip() {
        return jdbcTemplate.queryForObject(
                getCountVipUsersQuery(), new Object[]{}, Integer.class);

    }


    public Integer countEmployeesAgentCallCenter() {
        return jdbcTemplate.queryForObject(
                getCountEmployeesAgentCallCenterQuery(), new Object[]{}, Integer.class);

    }


    public Integer countUnregisteredContacts() {

        return jdbcTemplate.queryForObject(
                getCountUnregisteredContactsQuery(), new Object[]{}, Integer.class);

    }

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

    public String getCountEmployeesQuery() {

        return queryService.getQuery("count.all.employee");
    }

    public String getCountEmployeesAdminsQuery() {

        return queryService.getQuery("count.employees.admins");
    }

    private String getSearchQueryForManager() {
        return queryService.getQuery("select.person.search.statistic");
    }


    private String getCountEmployeesManagerQuery() {

        return queryService.getQuery("count.employees.managers");
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

    private String getSearchQueryHendlerOrder()
    {

        return queryService.getQuery("select.person.search.statistic.order.by.order.handled");
    }


    private String  getSearchQueryByDateRange()

    {

        return queryService.getQuery("select.manager.statistic.filter.order");
    }


    private String getSearchQuery()

    {

        return queryService.getQuery("select.count.task.by.employee");
    }


    private String getCountQueryOrdersHandtoHand()
    {

        return queryService.getQuery("count.order.hand.to.hand");
    }

    private String getCountQueryOrdersFromOffice()
    {

        return queryService.getQuery("count.orders.from.office");
    }

    private String getQueryEmployeesByCourierOrCall_Center()
    {

        return queryService.getQuery("select.employee.call_center.couriers");
    }

    private String getOrderBetweenData()
    {
        return queryService.getQuery("count.orders.by.date");
    }

}
