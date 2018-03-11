package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Address;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressDao extends CrudDao<Address, Long> {
    Optional<Address> findOne(String address_name);

}
