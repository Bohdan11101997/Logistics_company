package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.WorkDayDao;
import edu.netcracker.project.logistic.model.WeekDay;
import edu.netcracker.project.logistic.model.WorkDay;
import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
public class WorkDayDaoImpl implements WorkDayDao, RowMapper<WorkDay> {
    private QueryService queryService;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public WorkDayDaoImpl(QueryService queryService, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.queryService = queryService;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public WorkDay mapRow(ResultSet rs, int rowNum) throws SQLException {
        WorkDay workDay = new WorkDay();
        workDay.setEmployeeId(rs.getLong("employee_id"));
        workDay.setWeekDay(WeekDay.valueOf(rs.getString("week_day")));
        workDay.setStartTime(rs.getObject("start_time", LocalTime.class));
        workDay.setEndTime(rs.getObject("end_time", LocalTime.class));
        return workDay;
    }

    @Override
    public Optional<WorkDay> findScheduleForDate(LocalDate date, Long employeeId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("employee_id", employeeId);
        paramMap.put("date", date);
        try {
            WorkDay workDay = namedJdbcTemplate.queryForObject(
                    getFindScheduleForDateQuery(),
                    paramMap,
                    this
            );
            return Optional.of(workDay);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }


    @Override
    public WorkDay save(WorkDay workDay) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("employee_id", workDay.getEmployeeId());
        paramMap.put("week_day", workDay.getWeekDay());
        paramMap.put("start_time", workDay.getStartTime());
        paramMap.put("end_time", workDay.getEndTime());
        namedJdbcTemplate.update(getUpsertQuery(), paramMap);
        return workDay;
    }

    @Override
    public void delete(WorkDay workDay) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("employee_id", workDay.getEmployeeId());
        paramMap.put("week_day", workDay.getWeekDay());
        namedJdbcTemplate.update(getDeleteQuery(), paramMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveMany(List<WorkDay> workDays) {
        List<Map<String, Object>> batchParamMap = new ArrayList<>();
        for (WorkDay workDay: workDays) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("employee_id", workDay.getEmployeeId());
            paramMap.put("week_day", workDay.getWeekDay().toString());
            paramMap.put("start_time", workDay.getStartTime());
            paramMap.put("end_time", workDay.getEndTime());
            batchParamMap.add(paramMap);
        }
        namedJdbcTemplate.batchUpdate(getUpsertQuery(), batchParamMap.toArray(new Map[0]));
    }


    @Override
    public Optional<WorkDay> findOne(WorkDay workDay) {
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("employee_id", workDay.getEmployeeId());
            paramMap.put("week_day", workDay.getWeekDay());

            WorkDay res = namedJdbcTemplate.queryForObject(getFindOneQuery(), paramMap,this);
            return Optional.of(res);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    private String getFindScheduleForDateQuery() {
        return queryService.getQuery("select.work_day.schedule_for_day");
    }

    private String getUpsertQuery() { return queryService.getQuery("upsert.work_day"); }

    private String getDeleteQuery() { return queryService.getQuery("delete.work_day"); }

    private String getFindOneQuery() { return queryService.getQuery("select.work_day"); }
}
