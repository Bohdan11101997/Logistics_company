package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.OfficeDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.service.QueryService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


@Repository
public class OfficeDaoImpl implements OfficeDao, QueryDao, RowMapper<Office> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OfficeDaoImpl.class);

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private QueryService queryService;
    private RowMapper<Address> addressRowMapper;


    @Autowired
    public OfficeDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, QueryService queryService, RowMapper<Address> addressRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.queryService = queryService;
        this.addressRowMapper = addressRowMapper;
    }

    @Override
    public Office mapRow(ResultSet resultSet, int i) throws SQLException {

            Office office = new Office();
            office.setOfficeId(resultSet.getLong("office_id"));
            office.setName(resultSet.getString("name"));

            Address address = addressRowMapper.mapRow(resultSet, i);
            office.setAddress(address);

            return office;
        }





    @Override
    public Office save(Office office) {
        boolean hasPrimaryKey = office.getOfficeId() != null;
        if (hasPrimaryKey) {
            jdbcTemplate.update(getUpsertQuery(), ps -> {
                ps.setObject(1, office.getOfficeId());
                ps.setObject(2, office.getName());
                ps.setObject(3, office.getAddress().getId());

            });
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, office.getName());
                ps.setObject(2, office.getAddress().getId());
                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("office_id");
            office.setOfficeId(key.longValue());
        }
        logger.info("Office saved");
        return office;
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update(getDeleteQuery(), ps ->
        {
            ps.setObject(1, aLong);
            logger.info("Office delete");
        });

    }

    @Override
    public Optional<Office> findOne(Long aLong) {
        Office office;
        try {
            office = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{aLong},
                    this::mapRow);
            logger.info("Find one office");
            return Optional.of(office);

        } catch (EmptyResultDataAccessException e) {
            logger.error("Empty data");
        }

        return Optional.empty();
    }

    private String prepareSearchString(String input) {
        return "%" + input.replace("%", "\\%") + "%";
    }

    public List<Office> findByDepartmentOrAddress( String department, String address) {

        String departmentSearch= department;
     departmentSearch = departmentSearch == null ? "%%" : prepareSearchString(departmentSearch.trim());

        String addressSearch= address;
        addressSearch = addressSearch == null ? "%%" : prepareSearchString(addressSearch.trim());


        Map<String, Object> paramMap = new HashMap<>(5);
        paramMap.put("department",  departmentSearch);
        paramMap.put("address", addressSearch);

        System.out.println(departmentSearch);
        System.out.println(addressSearch);

        try {
            return namedParameterJdbcTemplate.query(
                    getAllOfficesByDepartment(),
                    paramMap,
                    this);

        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Office> allOffices() {
        return jdbcTemplate.query(getAllOffices(), this);
    }

    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.office");
    }

    @Override
    public String getUpsertQuery() {
        return queryService.getQuery("upsert.office");
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.office");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.office");
    }

    private String getAllOffices() {
        return queryService.getQuery("all.office");
    }

    private String getAllOfficesByDepartment() {
        return queryService.getQuery("all.office.by.department");
    }


}
