package edu.netcracker.project.logistic.dao.impl;


import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.PersonRoleDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.dao.WorkDayDao;
import edu.netcracker.project.logistic.model.*;

import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Repository
public class PersonCrudDaoImpl implements PersonCrudDao, QueryDao, RowMapper<Person> {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private QueryService queryService;
    private PersonRoleDao personRoleDao;
    private RowMapper<Contact> contactMapper;
    private RowMapper<Role> roleMapper;

    @Autowired
    WorkDayDao workDayDao;

    @Autowired
    PersonCrudDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                      QueryService queryService, PersonRoleDao personRoleDao,
                      RowMapper<Contact> contactMapper, RowMapper<Role> roleMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.queryService = queryService;
        this.personRoleDao = personRoleDao;
        this.contactMapper = contactMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("person_id"));
        person.setUserName(rs.getString("user_name"));
        person.setPassword(rs.getString("password"));
        person.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());

        Contact contact = contactMapper.mapRow(rs, rowNum);
        person.setContact(contact);

        Set<Role> roles = new HashSet<>();
        do {
            roles.add(roleMapper.mapRow(rs, rowNum));
        } while (rs.next());
        person.setRoles(roles);
        return person;
    }

    @Transactional
    @Override
    public Person save(Person person) {
        boolean hasPrimaryKey = person.getId() != null;

        if (hasPrimaryKey) {
            jdbcTemplate.update(getUpsertQuery(), ps -> {
                ps.setObject(1, person.getId());
                ps.setObject(2, person.getUserName());
                ps.setObject(3, person.getPassword());
                ps.setObject(4, person.getRegistrationDate());
                ps.setObject(5, person.getContact().getContactId());
            });
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, person.getUserName());
                ps.setObject(2, person.getPassword());
                ps.setObject(3, person.getRegistrationDate());
                ps.setObject(4, person.getContact().getContactId());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("person_id");
            person.setId(key.longValue());
        }
        // Save roles
        personRoleDao.deleteByPersonId(person.getId());
        List<PersonRole> personRoles = person.getRoles()
                .stream()
                .map(r -> new PersonRole(person.getId(), r.getRoleId()))
                .collect(Collectors.toList());
        personRoleDao.saveMany(personRoles);
        return person;
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update(getDeleteQuery(), ps ->
        {
            ps.setObject(1, aLong);
        });

    }

    @Override
    public Optional<Person> findOne(Long aLong) {
        Person person;
        try {
            person = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{aLong},
                    this);
            return Optional.of(person);

        } catch (EmptyResultDataAccessException e) {
            System.err.println("Empty data");
        }
        return Optional.empty();
    }

    @Override
    public Optional<Person> findByContactId(Long contactId) {
        Person person;
        try {
            person = jdbcTemplate.queryForObject(
                    getFindByContactIdQuery(),
                    new Object[]{contactId},
                    this);
            return Optional.of(person);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Person> findOne(String username) {
        Person person;
        try {
            person = jdbcTemplate.queryForObject(
                    getFindOneByUsernameQuery(),
                    new Object[]{username},
                    this);
            return Optional.ofNullable(person);

        } catch (EmptyResultDataAccessException e) {
            System.err.println("Empty data");
        }
        return Optional.empty();
    }

    @Override
    public List<Person> findAll() {
        try {
            return jdbcTemplate.query(
                    getFindAllQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private List<Person> extractMany(ResultSet rs) throws SQLException {
        List<Person> result = new ArrayList<>();

        boolean rowsLeft = rs.next();
        for (int i = 0; rowsLeft; i++) {
            Person person = new Person();
            person.setId(rs.getLong("person_id"));
            person.setUserName(rs.getString("user_name"));
            person.setPassword(rs.getString("password"));
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

    @Override
    public List<Person> findAllEmployees() {
        try {
            return jdbcTemplate.query(
                    getFindAllEmployeesQuery(),
                    this::extractMany
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Person> findByRoleId(Long roleId) {
        try {
            return jdbcTemplate.query(
                    getFindByRoleIdQuery(),
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
    public List<Person> search(SearchForm searchForm) {
        String firstName = searchForm.getFirstName();
        firstName = firstName == null ? "%%" : prepareSearchString(firstName);

        String lastName = searchForm.getLastName();
        lastName = lastName == null ? "%%" : prepareSearchString(lastName);

        LocalDateTime from = searchForm.getFrom();
        if (from == null) {
            from = LocalDateTime.MIN;
        } else {
            from = from.with(LocalTime.MIN);
        }

        LocalDateTime to = searchForm.getTo();
        if (to == null) {
            to = LocalDateTime.now();
        } else {
            to = to.with(LocalTime.MAX);
        }

        Map<String, Object> paramMap = new HashMap<>(5);
        paramMap.put("first_name", firstName);
        paramMap.put("last_name", lastName);
        paramMap.put("from", from);
        paramMap.put("to", to);
        paramMap.put("role_ids", searchForm.getRoleIds());

        try {
            return namedParameterJdbcTemplate.query(
                    getSearchQuery(),
                    paramMap,
                    this::extractMany
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Person> findOneByEmail(String email) {
        Person person;
        try {
            person = jdbcTemplate.queryForObject(
                    getFindOneByEmailQuery(),
                    new Object[]{email},
                    this);
            return Optional.of(person);

        } catch (EmptyResultDataAccessException e) {
            System.err.println("Empty data");
        }
        return Optional.empty();
    }


    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.person");
    }

    @Override
    public String getUpsertQuery() {
        return queryService.getQuery("upsert.person");
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.person");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.person");
    }




    private String getFindAllQuery() {
        return queryService.getQuery("select.all.person");
    }

    private String getFindAllEmployeesQuery() {
        return queryService.getQuery("select.person.employee");
    }

    private String getFindOneByUsernameQuery() {
        return queryService.getQuery("select.person.by.username");
    }

    private String getSearchQuery() {
        return queryService.getQuery("select.person.search");
    }

    private String getFindOneByEmailQuery() {
        return queryService.getQuery("select.person.by.email");
    }

    private String getFindByRoleIdQuery() {
        return queryService.getQuery("select.person.by.role_id");
    }

    private String getFindByContactIdQuery() { return queryService.getQuery("select.person.by.contact_id"); }



}
