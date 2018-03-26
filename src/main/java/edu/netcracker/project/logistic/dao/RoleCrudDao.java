package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleCrudDao extends CrudDao<Role, Long> {

    List<Role> getAllRole();

    List<Role> getByPersonId(Long personId);

    Optional<Role> getByName(String name);

    List<Role> findEmployeeRoles();

    List<Role> findEmployeeRolesForManager();

    List<Role> findClientRoles();
}
