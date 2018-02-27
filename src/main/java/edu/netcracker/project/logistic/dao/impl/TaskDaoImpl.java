package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.TaskDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Task;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TaskDaoImpl implements TaskDao, RowMapper<Task> {


    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;
    private RowMapper<Person> personRowMapper;

    @Autowired
    public TaskDaoImpl(JdbcTemplate jdbcTemplate, QueryService queryService, RowMapper<Person> personRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
        this.personRowMapper = personRowMapper;
    }




    @Override
    public Task mapRow(ResultSet resultSet, int i) throws SQLException {

        Task task = new Task();
        task.setId(resultSet.getLong("task_id"));
        task.setDescription(resultSet.getString("description"));
        task.setIs_complete(resultSet.getBoolean("is_complete"));
        Person person = personRowMapper.mapRow(resultSet, i);
        task.setPerson(person);
        return null;
    }

    @Override
    public Task save(Task object) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public Optional<Task> findOne(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Task> findAll() {

        return null;


    }


}
