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
    public void delete(Long id) {
        advertisementDao.delete(id);
    }

    @Override
    public Optional<Advertisement> findOne(Long id) {
        return advertisementDao.findOne(id);
    }

    @Override
    public List<Advertisement> findAll() {
        return advertisementDao.allAdvertisements();
    }

    @Override
    public List<Advertisement> findAmountOfAdvertisementsForCurrentPage(int itemsOnPage, int currentPage) {
        return advertisementDao.findAmountOfAdvertisementsForCurrentPage(itemsOnPage, currentPage);
    }

    @Override
    public int getCountOfAllAdvertisements() {
        return advertisementDao.getCountOfAllAdvertisements();
    }

    @Override
    public List<Advertisement> findAllForToday() {
        return advertisementDao.allAdvertisementsForToday();
    }
}
