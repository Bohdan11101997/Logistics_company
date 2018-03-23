package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.OfficeDao;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class OfficeServiceImpl implements OfficeService {

    OfficeDao officeDao;


    @Autowired
    public OfficeServiceImpl(OfficeDao officeDao) {
        this.officeDao = officeDao;
    }


    @Override
    public void save(Office office) {
        officeDao.save(office);
    }

    @Override
    public void delete(Long aLong) {
          officeDao.delete(aLong);
    }

    @Override
    public Optional<Office> findOne(Long aLong) {
        return  officeDao.findOne(aLong);
    }

    @Override
    public List<Office> allOffices() {
        return officeDao.allOffices();
    }

    @Override
    public List<Office> findByDepartment(String department, String address) {
        return officeDao.findByDepartmentOrAddress(department, address);
    }
}
