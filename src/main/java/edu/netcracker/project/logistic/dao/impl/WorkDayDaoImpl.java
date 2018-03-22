package edu.netcracker.project.logistic.dao.impl;

import edu.netcracker.project.logistic.dao.WorkDayDao;
import edu.netcracker.project.logistic.model.WeekDay;
import edu.netcracker.project.logistic.model.WorkDay;
import edu.netcracker.project.logistic.service.QueryService;
import org.apache.tomcat.jni.Local;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public WorkDay save(WorkDay object) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public Optional<WorkDay> findOne(Long aLong) {
        return Optional.empty();
    }

    private String getFindScheduleForDateQuery() {
        return queryService.getQuery("select.work_day.schedule_for_day");
    }
//    private  String  getFindScheduleForDateQueryForAllEmployee()
//    {
//
//        return queryService.getQuery("select.work_day.schedule_for_day.all.employees");
//    }
}
