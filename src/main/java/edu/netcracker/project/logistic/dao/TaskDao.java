package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Task;

import java.util.List;

public interface TaskDao extends CrudDao<Task, Long>
{

    public List<Task> findAll();
}