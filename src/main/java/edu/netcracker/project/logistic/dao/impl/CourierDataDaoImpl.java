package edu.netcracker.project.logistic.dao.impl;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.model.CourierData;
import edu.netcracker.project.logistic.model.CourierStatus;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.QueryService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class CourierDataDaoImpl implements CourierDataDao, QueryDao {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CourierDataDaoImpl.class);

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;

    @Autowired
    public CourierDataDaoImpl(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<CourierData> findOne(Person id){
        try {
            CourierData courierData = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    //TODO: review this
                    (resultSet, i) -> {
                        CourierData cd = new CourierData();
                        cd.setId(id);
                        resultSet.getLong("person_id");
                        cd.setCourierStatus(CourierStatus.valueOf(resultSet.getString("courier_status")));
                        cd.setLastLocation(resultSet.getString("courier_last_location"));
                        cd.setTravelMode(TravelMode.valueOf(resultSet.getString("courier_travel_mode")));
                        return cd;
                    });
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
                ps.setObject(3, courierData.getTravelMode().name());
            });
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc -> {
                String query = getInsertQuery();
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, courierData.getId().getId());
                ps.setObject(2, courierData.getCourierStatus().name());
                ps.setObject(3, courierData.getLastLocation());
                ps.setObject(3, courierData.getTravelMode().name());
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
        return queryService.getQuery("select.courier_data");
    }

}
