package edu.netcracker.project.logistic.dao;


import edu.netcracker.project.logistic.model.CourierData;
import edu.netcracker.project.logistic.model.Person;

import java.util.Optional;

public interface CourierDataDao {
    Optional<CourierData> findOne(Person id);
    CourierData save(CourierData courierData);
}
