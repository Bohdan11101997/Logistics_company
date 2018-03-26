package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RoleService extends CrudService<Role, Long> {


    List<Role> getByPersonId(Long personId);

    Optional<Role> getByName(String name);

    boolean exists(Long aLong);

    String findAll();

    List<Role> findRolesByPersonId(Long id);

    List<Role> findEmployeeRoles();

    List<Role> findClientRoles();


}
