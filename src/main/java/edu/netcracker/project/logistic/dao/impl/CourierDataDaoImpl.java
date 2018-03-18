package edu.netcracker.project.logistic.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.QueryService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CourierDataDaoImpl implements CourierDataDao, RowMapper<CourierData> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourierDataDaoImpl.class);

    private ObjectMapper objectMapper;
    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;
    private PersonCrudDao personCrudDao;

    @Autowired
    public CourierDataDaoImpl(ObjectMapper objectMapper, QueryService queryService,
                              JdbcTemplate jdbcTemplate, PersonCrudDao personCrudDao) {
        this.objectMapper = objectMapper;
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
        this.personCrudDao = personCrudDao;
    }

    @Override
    public CourierData mapRow(ResultSet rs, int rowNum) throws SQLException {

        CourierData courierData = new CourierData();

        Optional<Person> person = personCrudDao.findOne(rs.getLong("person_id"));
        if (!person.isPresent()) {
            return null;
        }
        courierData.setId(person.get());
        courierData.setCourierStatus(CourierStatus.valueOf(rs.getString("courier_status").toUpperCase()));
        courierData.setLastLocation(rs.getString("courier_last_location"));
        courierData.setTravelMode(TravelMode.valueOf(rs.getString("courier_travel_mode").toUpperCase()));

        List<RoutePoint> route;
        try {
            route = objectMapper.readValue(
                    rs.getString("route"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RoutePoint.class)
            );
        } catch (IOException ex) {
            route = null;
        }
        courierData.setRoute(route);
        return courierData;
    }

    @Override
    public Optional<CourierData> findOne(Person id){
        try {
            CourierData courierData = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{id.getId()},
                    this
            );
            return Optional.of(courierData);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CourierData save(CourierData courierData) {
        boolean hasPrimaryKey = courierData.getId() != null;
        if (hasPrimaryKey) {
            jdbcTemplate.update(getUpsertQuery(), ps -> {
                ps.setObject(1, courierData.getId().getId());
                ps.setObject(2, courierData.getCourierStatus().name());
                ps.setObject(3, courierData.getLastLocation());
                ps.setObject(4, courierData.getTravelMode().name());
                String routeJson;
                try {
                    routeJson = objectMapper.writeValueAsString(courierData.getRoute());
                } catch (JsonProcessingException ex) {
                    routeJson = null;
                }
                ps.setObject(5, routeJson);
            });
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, courierData.getId().getId());
                ps.setObject(2, courierData.getCourierStatus().name());
                ps.setObject(3, courierData.getLastLocation());
                ps.setObject(4, courierData.getTravelMode().name());
                String routeJson;
                try {
                    routeJson = objectMapper.writeValueAsString(courierData.getRoute());
                } catch (JsonProcessingException ex) {
                    routeJson = null;
                }
                ps.setObject(5, routeJson);

                return ps;
            }, keyHolder);
        }
        logger.info("CourierData saved");
        return courierData;
    }

    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.courier_data");
    }

    @Override
    public String getUpsertQuery() {
        return queryService.getQuery("upsert.courier_data");
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.courier_data");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.courier_data.by_id");
    }

}
