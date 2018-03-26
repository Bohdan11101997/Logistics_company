package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.PersonRole;

import java.util.List;

public interface PersonRoleDao extends CrudDao<PersonRole, PersonRole> {
    void deleteByPersonId(Long personId);
    void deleteMany(List<PersonRole> personRoles);
    void saveMany(List<PersonRole> personRoles);
}
