package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Office;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface OfficeService extends CrudService<Office, Long>{
    List<Office> allOffices();

}
