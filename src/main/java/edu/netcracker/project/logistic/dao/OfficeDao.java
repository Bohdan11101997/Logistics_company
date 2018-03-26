package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.OfficeSearchForm;
import edu.netcracker.project.logistic.model.SearchFormOrder;

import java.util.List;


public interface OfficeDao extends CrudDao<Office, Long> {

    List<Office> findByDepartmentOrAddress(OfficeSearchForm officeSearchForm);

    List<Office> allOffices();
}
