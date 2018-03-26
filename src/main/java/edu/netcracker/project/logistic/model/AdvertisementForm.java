package edu.netcracker.project.logistic.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class AdvertisementForm {

    private Long id;
    private String caption;
    private String description;
    private String type;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate showFirstDate;
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private LocalDate showEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 3, max = 200)
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @NotNull
    @Size(min = 10, max = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
