package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.QueryDao;
import edu.netcracker.project.logistic.dao.ResetPasswordTokenDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.ResetPasswordToken;
import edu.netcracker.project.logistic.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class ResetPasswordTokenDaoImpl implements ResetPasswordTokenDao, QueryDao, RowMapper<ResetPasswordToken> {
    private final Logger logger = LoggerFactory.getLogger(ResetPasswordTokenDaoImpl.class);

    private RowMapper<Person> personMapper;
    private JdbcTemplate jdbcTemplate;
    private QueryService queryService;
    private PersonCrudDao personCrudDao;

    @Autowired
    public ResetPasswordTokenDaoImpl(RowMapper<Person> personMapper, JdbcTemplate jdbcTemplate, QueryService queryService, PersonCrudDao personCrudDao) {
        this.personMapper = personMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
        this.personCrudDao = personCrudDao;
    }

    @Override
    public ResetPasswordToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setResetToken(rs.getString("reset_token"));
        resetPasswordToken.setPerson(new Person());
        resetPasswordToken.getPerson().setId(rs.getLong("person_id"));
        return resetPasswordToken;
    }

    @Override
    public ResetPasswordToken save(ResetPasswordToken resetPasswordToken) {
        jdbcTemplate.update(psc -> {
            String query = getInsertQuery();
            PreparedStatement ps = psc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, resetPasswordToken.getPerson().getId());
            ps.setObject(2, resetPasswordToken.getResetToken());
            return ps;
        });

        return resetPasswordToken;
    }

    @Override
    public void delete(Long personId) {
        jdbcTemplate.update(getDeleteQuery(), ps ->
        {
            ps.setObject(1, personId);
        });
    }

    @Override
    public Optional<ResetPasswordToken> findOne(Long personId) {
        ResetPasswordToken resetPasswordToken;
        try {
            resetPasswordToken = jdbcTemplate.queryForObject(
                    getFindOneQuery(),
                    new Object[]{personId},
                    this);
            return Optional.ofNullable(resetPasswordToken);

        } catch (EmptyResultDataAccessException e) {
            logger.info("Empty data");
        }
        return Optional.empty();
    }


    @Override
    public Optional<ResetPasswordToken> findOneByToken(String token) {
        ResetPasswordToken resetPasswordToken;
        try {
            resetPasswordToken = jdbcTemplate.queryForObject(
                    getFindOneByResetToken(),
                    new Object[]{token},
                    this);

            // assign person by id
            resetPasswordToken.setPerson(personCrudDao.findOne(resetPasswordToken.getPerson().getId()).get());

            return Optional.ofNullable(resetPasswordToken);

        } catch (EmptyResultDataAccessException e) {
            logger.info("Empty data");
        }
        return Optional.empty();
    }

    @Override
    public String getInsertQuery() {
        return queryService.getQuery("insert.forgot_password");
    }

    @Override
    public String getUpsertQuery() {
        return null;
    }

    @Override
    public String getDeleteQuery() {
        return queryService.getQuery("delete.forgot_password");
    }

    @Override
    public String getFindOneQuery() {
        return queryService.getQuery("select.forgot_password.by.person_id");
    }

    public String getFindOneByResetToken() {
        return queryService.getQuery("select.forgot_password.by.reset_token");
    }


}
