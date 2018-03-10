package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.AddressDao;
import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressDao addressDao;

    @Override
    public void save(Address address) {
        addressDao.save(address);
    }

    @Override
    public void delete(Long aLong) {
    addressDao.delete(aLong);
    }

    @Override
    public Optional<Address> findOne(Long aLong) {
        return addressDao.findOne(aLong);
    }

    @Override
    public Optional<Address> findOne(String address_name) {
        return addressDao.findOne(address_name);
    }
}
