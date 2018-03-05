package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.AdvertisementDao;
import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private AdvertisementDao advertisementDao;

    @Autowired
    public AdvertisementServiceImpl(AdvertisementDao advertisementDao){
        this.advertisementDao = advertisementDao;
    }

    @Override
    public void save(Advertisement advertisement) {
        advertisementDao.save(advertisement);
    }

    @Override
    public void update(Advertisement advertisement) {
        advertisementDao.update(advertisement);
    }

    @Override
    public void delete(Long id) {
        advertisementDao.delete(id);
    }

    @Override
    public Optional<Advertisement> findOne(Long id) {
        return advertisementDao.findOne(id);
    }

    @Override
    public List<Advertisement> findAll() {
        return advertisementDao.allOffices();
    }
}
