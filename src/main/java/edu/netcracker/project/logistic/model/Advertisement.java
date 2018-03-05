package edu.netcracker.project.logistic.model;

import java.time.LocalDate;

public class Advertisement {

    private Long id;
    private String caption;
    private String description;
    private LocalDate showFirstDate;
    private LocalDate showEndDate;
    private AdvertisementType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AdvertisementType getType() {
        return type;
    }

    public void setType(AdvertisementType type) {
        this.type = type;
    }

    public LocalDate getShowFirstDate() {
        return showFirstDate;
    }

    public void setShowFirstDate(LocalDate showFirstDate) {
        this.showFirstDate = showFirstDate;
    }

    public LocalDate getShowEndDate() {
        return showEndDate;
    }

    public void setShowEndDate(LocalDate showEndDate) {
        this.showEndDate = showEndDate;
    }
}
