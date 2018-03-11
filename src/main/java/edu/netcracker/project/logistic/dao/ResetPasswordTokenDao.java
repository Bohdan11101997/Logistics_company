package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.ResetPasswordToken;

import java.util.Optional;

public interface ResetPasswordTokenDao extends CrudDao<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findOneByToken(String token);
}
