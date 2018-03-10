package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Address;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AddressService  extends  CrudService<Address,Long>{

    Optional<Address> findOne(String address_name);

}
