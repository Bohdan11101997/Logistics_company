package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.WorkDay;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkDayDao extends CrudDao<WorkDay, WorkDay> {
    Optional<WorkDay> findScheduleForDate(LocalDate date, Long employeeId);
    void saveMany(List<WorkDay> workDays);
}
