package edu.netcracker.project.logistic.model;

import java.io.Serializable;
import java.time.LocalTime;

public class WorkDay implements Serializable {
    private Long employeeId;
    private WeekDay weekDay;
    private LocalTime startTime;
    private LocalTime endTime;

    public WorkDay() {
    }

    public WorkDay(Long employeeId, WeekDay weekDay, LocalTime startTime, LocalTime endTime) {
        this.employeeId = employeeId;
        this.weekDay = weekDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public WeekDay getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
