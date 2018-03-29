package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.OfficeSearchForm;

import java.util.List;


public interface OfficeDao extends CrudDao<Office, Long> {

    List<Office> findByDepartmentOrAddress(OfficeSearchForm officeSearchForm);

    List<Office> allOfficesForManager();

    List<Office>  getOfficesForAdmin();

    List<Office> allOffices();
}
