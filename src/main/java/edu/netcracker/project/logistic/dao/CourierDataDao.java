package edu.netcracker.project.logistic.dao;


import edu.netcracker.project.logistic.model.CourierData;
import edu.netcracker.project.logistic.model.Person;

import java.util.Optional;

public interface CourierDataDao extends QueryDao {
    Optional<CourierData> findOne(Long id);
    CourierData save(CourierData courierData);
}
