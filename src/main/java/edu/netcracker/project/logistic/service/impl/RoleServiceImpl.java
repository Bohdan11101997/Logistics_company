package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Role;
import edu.netcracker.project.logistic.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleCrudDao roleCrudDao;

    @Autowired
    public RoleServiceImpl(RoleCrudDao roleCrudDao) {
        this.roleCrudDao = roleCrudDao;
    }

    @Override
    public void save(Role role) {
     roleCrudDao.save(role);
    }

    @Override
    public void delete(Long aLong) {
    roleCrudDao.delete(aLong);
    }

    @Override
    public Optional<Role> findOne(Long aLong) {
        return roleCrudDao.findOne(aLong);
    }

    @Override
    public List<Role> getByPersonId(Long personId) {
        return roleCrudDao.getByPersonId(personId);
    }

    @Override
    public Optional<Role> getByName(String name) {
        return roleCrudDao.getByName(name);
    }

    @Override
    public boolean exists(Long aLong) {
        return roleCrudDao.contains(aLong);
    }

    @Override
    public String findAll() {
        return String.valueOf(roleCrudDao.getAllRole());
    }

    @Override
    public List<Role> findRolesByPersonId(Long id) {
        return roleCrudDao.getByPersonId(id);
    }

    @Override
    public List<Role> findEmployeeRoles() {
        return roleCrudDao.findEmployeeRoles();
    }

    @Override
    public List<Role> findClientRoles() {
        return roleCrudDao.findClientRoles();
    }
}
