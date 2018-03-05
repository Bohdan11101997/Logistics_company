package edu.netcracker.project.logistic.dao;

public interface QueryDao {

    String getInsertQuery();

    String getUpsertQuery();

    String getDeleteQuery();

    String getFindOneQuery();

}
