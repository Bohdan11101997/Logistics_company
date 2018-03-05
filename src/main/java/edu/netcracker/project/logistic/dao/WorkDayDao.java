package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.WorkDay;

import java.time.LocalDate;
import java.util.Optional;

public interface WorkDayDao extends CrudDao<WorkDay, Long> {
    Optional<WorkDay> findScheduleForDate(LocalDate date, Long employeeId);
}
