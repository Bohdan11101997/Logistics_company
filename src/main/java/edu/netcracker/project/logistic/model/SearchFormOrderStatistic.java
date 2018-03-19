package edu.netcracker.project.logistic.model;


import java.time.LocalDateTime;
import java.util.List;

public class SearchFormOrderStatistic {
    private LocalDateTime from;
    private LocalDateTime to;
    private Integer year;
    private Integer month;
    private List<Long> destination_typeIds;
    private List<Long> order_statusIds;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<Long> getDestination_typeIds() {
        return destination_typeIds;
    }

    public void setDestination_typeIds(List<Long> destination_typeIds) {
        this.destination_typeIds = destination_typeIds;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public List<Long> getOrder_statusIds() {
        return order_statusIds;
    }

    public void setOrder_statusIds(List<Long> order_statusIds) {
        this.order_statusIds = order_statusIds;
    }
}
