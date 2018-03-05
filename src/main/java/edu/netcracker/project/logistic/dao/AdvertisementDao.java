package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Advertisement;

import java.util.List;

public interface AdvertisementDao extends CrudDao<Advertisement, Long> {

    List<Advertisement> allOffices();
    void update(Advertisement advertisement);

}
