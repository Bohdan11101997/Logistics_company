package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Advertisement;

import java.util.List;

public interface AdvertisementDao extends CrudDao<Advertisement, Long> {

    List<Advertisement> allAdvertisements();
    int getCountOfAllAdvertisements();
    List<Advertisement> findAmountOfAdvertisementsForCurrentPage(int itemsOnPage, int currentPage);
}
