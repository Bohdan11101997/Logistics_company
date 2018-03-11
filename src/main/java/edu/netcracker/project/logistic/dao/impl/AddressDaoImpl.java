package edu.netcracker.project.logistic.dao.impl;

import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.AddressDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.service.QueryService;

import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;


@Repository
public class AddressDaoImpl implements AddressDao, QueryDao, RowMapper<Address> {

    private static final Logger logger = LoggerFactory.getLogger(ContactDaoImpl.class);

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;


    @Override
    public Address mapRow(ResultSet resultSet, int i) throws SQLException {
        Address address = new Address();
        address.setId(resultSet.getLong("address_id"));
        address.setName(resultSet.getString("address_name"));
        return address;
    }

    @Autowired
    public AddressDaoImpl(JdbcTemplate jdbcTemplate, QueryService queryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
    }

    @Override
    public Address save(Address address) {
        boolean hasPrimaryKey = address.getId() != null;
        if (address.check(address.getName())) {
            if (hasPrimaryKey) {
                jdbcTemplate.update(getUpsertQuery(), ps -> {
                    ps.setObject(1, address.getId());
                    ps.setObject(2, address.getName());
                });
            } else {
                GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(psc -> {
                    String query = getInsertQuery();
                    PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    ps.setObject(1, address.getName());
                    return ps;
                }, keyHolder);
                Number key = (Number) keyHolder.getKeys().get("address_id");
                address.setId(key.longValue());
            }
        } else {
            throw new IllegalArgumentException("Address not exists");
        }
        logger.info("Save address");
        return address;
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update(getDeleteQuery(), ps -> ps.setObject(1, aLong));
        logger.info("Delete address");
    }


    public Optional<Address> findOne(String address_name) {
        try {
            Address address = jdbcTemplate.queryForObject(
                    getFindOneQueryByAddress_name(),
                    new Object[]{address_name},
                    this);
            logger.info("Find address");
            return Optional.of(address);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Address> findOne(Long aLong) {
        try {
            Address address = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{aLong},
                    this);
            logger.info("Find address");
            return Optional.of(address);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.address");
    }

    @Override
    public String getUpsertQuery() {
        return queryService.getQuery("upsert.address");
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.address");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.address");
    }

    public String getFindOneQueryByAddress_name() {
        return queryService.getQuery("select.address.by.name");
    }

    public boolean check(Address target, Address base) {
        return base.check(target);
    }

    public boolean check(String target, Address base) {
        return base.check(target);
    }

    public boolean check(String target, Address base, TravelMode travelMode) {
        return base.check(target, travelMode);
    }

    public boolean check(String target, String base) {
        return new Address(base).check(target);
    }

    public boolean check(String target, String base, TravelMode travelMode) {
        return new Address(base).check(target, travelMode);
    }
}
