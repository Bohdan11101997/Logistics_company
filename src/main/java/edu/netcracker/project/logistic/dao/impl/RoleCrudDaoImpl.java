package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Role;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleCrudDaoImpl implements RoleCrudDao, QueryDao, RowMapper<Role> {

    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;

    public RoleCrudDaoImpl(JdbcTemplate jdbcTemplate, QueryService queryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
    }

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setRoleId(rs.getLong("role_id"));
        role.setRoleName(rs.getString("role_name"));
        role.setEmployeeRole(rs.getBoolean("is_employee_role"));
        role.setPriority(rs.getString("priority"));
        return role;
    }

    private RowMapper<Role> getMapperForAll() {
        return ((resultSet, i) ->
        {
            Role role = new Role();
            role.setRoleName(resultSet.getString("role_name"));
            role.setEmployeeRole(resultSet.getBoolean("is_employee_role"));
            role.setPriority(resultSet.getString("priority"));
            return role;
        });
    }

    @Override
    public Role save(Role role) {
        boolean hasPrimaryKey = role.getRoleId() != null;

        if (hasPrimaryKey) {
            jdbcTemplate.update(getUpsertQuery(), ps -> {
                ps.setObject(1, role.getRoleId());
                ps.setObject(2, role.getRoleName());
                ps.setObject(3, role.getPriority());
            });
        } else {
            String query = getInsertQuery();
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(psc -> {
                PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, role.getRoleName());
                ps.setObject(2, role.getPriority());

                return ps;
            }, keyHolder);
            Number key = (Number) keyHolder.getKeys().get("role_id");
            role.setRoleId(key.longValue());
        }
        return role;
    }

    @Override
    public void delete(Long aLong) {
        jdbcTemplate.update(getDeleteQuery(), ps ->
        {
            ps.setObject(1, aLong);
        });
    }


    @Override
    public Optional<Role> findOne(Long aLong) {
        Role role;
        try {
            role = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{aLong},
                    this);
            return Optional.of(role);

        } catch (EmptyResultDataAccessException e) {
            System.err.println("Empty data");
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> getByName(String name) {
        try {
            Role role = jdbcTemplate.queryForObject(
                    getFindByNameQuery(),
                    new Object[]{name},
                    this
            );
            return Optional.of(role);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Role> getAllRole() {
        return jdbcTemplate.query(getAllRolesQuery(), getMapperForAll());
    }

    @Override
    public List<Role> getByPersonId(Long personId) {
        try {
            return jdbcTemplate.query(getByPersonIdQuery(), new Object[]{personId}, this);
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Role> findEmployeeRoles() {
        try {
            return jdbcTemplate.query(
                    getEmployeeRolesQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }


    public List<Role> findEmployeeRolesForManager() {
        try {
            return jdbcTemplate.query(
                    getEmployeeRolesQueryForManager(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }
    @Override
    public List<Role> findClientRoles() {
        try {
            return jdbcTemplate.query(
                    getClientRolesQuery(),
                    this
            );
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    private String getAllRolesQuery() {
        return queryService.getQuery("role.name.user");
    }

    private String getByPersonIdQuery() {
        return queryService.getQuery("select.role.by.person_id");
    }

    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.role");
    }

    @Override
    public String getUpsertQuery() {
        return queryService.getQuery("upsert.role");
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.role");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.role");
    }

    private String getFindByNameQuery() {
        return queryService.getQuery("select.role.by.name");
    }

    private String getEmployeeRolesQuery() {
        return queryService.getQuery("select.role.employee");
    }

    private String getClientRolesQuery() { return queryService.getQuery("select.role.client"); }

    private String getEmployeeRolesQueryForManager()
    {

        return queryService.getQuery("select.role.employee.statistic");
    }
}
