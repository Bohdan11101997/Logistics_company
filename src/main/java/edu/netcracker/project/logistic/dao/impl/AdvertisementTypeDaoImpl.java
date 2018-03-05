package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.AdvertisementTypeDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.AdvertisementType;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class AdvertisementTypeDaoImpl implements AdvertisementTypeDao, QueryDao, RowMapper<AdvertisementType> {

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;

    @Autowired
    public AdvertisementTypeDaoImpl(JdbcTemplate jdbcTemplate, QueryService queryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
    }

    @Override
    public AdvertisementType mapRow(ResultSet resultSet, int i) throws SQLException {
        AdvertisementType type = new AdvertisementType();
        type.setId(resultSet.getLong("type_advertisement_id"));
        type.setName(resultSet.getString("advertisement_name"));
        return type;
    }

    @Override
    public AdvertisementType save(AdvertisementType object) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public Optional<AdvertisementType> findOne(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<AdvertisementType> getByName(String advertisementName) {

        try {
            AdvertisementType type = jdbcTemplate.queryForObject(
                    getFindByNameQuery(),
                    new Object[]{advertisementName},
                    this::mapRow);
            return Optional.of(type);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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

    private String getFindByNameQuery() { return queryService.getQuery("select.advertisement.type.by.name"); }



}
