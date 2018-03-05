package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Advertisement;

import java.util.List;
import java.util.Optional;

public interface AdvertisementService {

    void save(Advertisement advertisement);

    void update(Advertisement advertisement);

    void delete(Long id);

    Optional<Advertisement> findOne(Long id);

    List<Advertisement> findAll();

}
