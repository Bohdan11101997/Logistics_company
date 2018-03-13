package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.QueryService;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ContactDaoImpl implements ContactDao, QueryDao, RowMapper<Contact> {

    private static final Logger logger = LoggerFactory.getLogger(ContactDaoImpl.class);

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;

    @Autowired
    public ContactDaoImpl(JdbcTemplate jdbcTemplate, QueryService queryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
    }

    public Contact mapRow(ResultSet rs, int i) throws SQLException {
        Contact c = new Contact();
        c.setContactId(rs.getLong("contact_id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setEmail(rs.getString("email"));
        c.setPhoneNumber(rs.getString("phone_number"));
        return c;
    }



    @Override
    public Contact save(Contact contact) {
        boolean hasPrimaryKey = contact.getContactId() != null;

        if (hasPrimaryKey) {
            jdbcTemplate.update(getUpsertQuery(), ps -> {
                ps.setObject(1, contact.getContactId());
                ps.setObject(2, contact.getFirstName());
                ps.setObject(3, contact.getLastName());
                ps.setObject(4, contact.getPhoneNumber());
                ps.setObject(5, contact.getEmail());
            });
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, contact.getFirstName());
                ps.setObject(2, contact.getLastName());
                ps.setObject(3, contact.getPhoneNumber());
                ps.setObject(4, contact.getEmail());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("contact_id");
            contact.setContactId(key.longValue());
        }
        logger.info("Save Contact");
        return contact;
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(getDeleteQuery(), ps -> ps.setObject(1, id));
    }

    @Override
    public Optional<Contact> findOne(Long id) {
        try {
            Contact contact = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{id},
                    this);
            return Optional.of(contact);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Contact> findByPhoneNumberOrEmail(String phoneNumber, String email) {
        try {
            return jdbcTemplate.query(
                    getFindByPhoneNumberOrEmailQuery(),
                    new Object[]{phoneNumber, email},
                    this
            );
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }


    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.contact");
    }

    @Override
    public String getUpsertQuery() {
        return queryService.getQuery("upsert.contact");
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.contact");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.contact");
    }

    private String getFindByPhoneNumberOrEmailQuery() {
        return queryService.getQuery("select.contact.by.phone_number.or.email");
    }
}
