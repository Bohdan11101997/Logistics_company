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
import java.util.*;



@Repository
public class ManagerStatisticsDaoImpl  {



    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private QueryService queryService;
    private PersonRoleDao personRoleDao;
    private RowMapper<Contact> contactMapper;
    private RowMapper<Role> roleMapper;

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

//            WorkDay workDay = new WorkDay();
//            workDay.setEmployeeId(person.getId());
//            workDay.setWeekDay(WeekDay.valueOf(rs.getString("week_day")));

        return result;
    }


    private String prepareSearchString(String input) {
        return "%" + input.replace("%", "\\%") + "%";
    }

    public List<Person> searchStatisticForManager(SearchFormStatisticEmployee searchFormStatisticEmployee) {


        String firstName = searchFormStatisticEmployee.getFirstName();
        firstName = firstName == null ? "%%" : prepareSearchString(firstName);

        String lastName = searchFormStatisticEmployee.getLastName();
        lastName = lastName == null ? "%%" : prepareSearchString(lastName);


        Map<String, Object> paramMap = new HashMap<>(6);
        paramMap.put("first_name", firstName);
        paramMap.put("last_name", lastName);
        paramMap.put("role_ids" ,searchFormStatisticEmployee.getRoleIds());
        paramMap.put("order", searchFormStatisticEmployee.getSortId());

        for(Map.Entry<String, Object> pair : paramMap.entrySet())
        {
            Object value = pair.getValue();
            System.out.println(value);
        }
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


    public Integer countEmployees() {

            return jdbcTemplate.queryForObject(
                    getCountEmployeesQuery(), new  Object[] {}, Integer.class);

    }

    public Integer countEmployeesAdmins() {
        return jdbcTemplate.queryForObject(
                getCountEmployeesAdminsQuery(), new  Object[] {}, Integer.class);
    }

    public List<Person> countEmployeesCouriers() {
        try {
            return jdbcTemplate.query(
                    getCountEmployeesCouriersQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Person> countEmployeesCouriersDriving() {
        try {
            return jdbcTemplate.query(
                    getCountEmployeesCouriersDrivingQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }
    public List<Person> countEmployeesCouriersWalking() {
        try {
            return jdbcTemplate.query(
                    getCountEmployeesCouriersWalkingQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }


    public List<Person> countUsers() {
        try {
            return jdbcTemplate.query(
                    getCountUsersQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Person> countUsersNormal() {
        try {
            return jdbcTemplate.query(
                    getCountNormalUsersQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Person> countUsersVip() {
        try {
            return jdbcTemplate.query(
                    getCountVipUsersQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }


    public List<Person> countEmployeesAgentCallCenter() {
        try {
            return jdbcTemplate.query(
                    getCountEmployeesAgentCallCenterQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    public List<Person> countUnregisteredContacts() {
        try {
            return jdbcTemplate.query(
                    getCountUnregisteredContactsQuery(),
                    this::extractMany1
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }


    private String  getCountUnregisteredContactsQuery()
    {

        return queryService.getQuery("count.unregistered.contact");

    }


    private String getCountUsersQuery()
    {

        return  queryService.getQuery("count.users");
    }

    private String  getCountNormalUsersQuery()
    {

        return  queryService.getQuery("count.users.normal");
    }

    private String getCountVipUsersQuery()
    {

        return  queryService.getQuery("count.users.vip");
    }

    private String getCountEmployeesAgentCallCenterQuery()
    {

        return queryService.getQuery("count.employees.agent.call.center");
    }

    private String   getCountEmployeesCouriersDrivingQuery()
    {

        return queryService.getQuery("count.employees.couriers.driving");
    }

    private String   getCountEmployeesCouriersWalkingQuery()
    {

        return queryService.getQuery("count.employees.couriers.walking");
    }

    private String   getCountEmployeesCouriersQuery()
    {

        return queryService.getQuery("count.employees.couriers");
    }

    public String getCountEmployeesQuery()
    {

        return queryService.getQuery("count.all.employee");
    }

    public String getCountEmployeesAdminsQuery()
    {

        return queryService.getQuery("count.employees.admins");
    }

    private String  getSearchQueryForManager()
    {
        return queryService.getQuery("select.person.search.statistic");
    }

    private String getPersonOrderByRegistration()
    {

        return queryService.getQuery("order.person.by.registration");
    }
}
