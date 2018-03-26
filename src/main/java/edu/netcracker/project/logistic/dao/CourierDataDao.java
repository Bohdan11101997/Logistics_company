package edu.netcracker.project.logistic.dao;


import edu.netcracker.project.logistic.model.CourierData;

import java.util.Optional;

public interface CourierDataDao extends QueryDao {
    Optional<CourierData> findOne(Long id);
    Optional<CourierData> findOne(String username);
    CourierData save(CourierData courierData);
}
