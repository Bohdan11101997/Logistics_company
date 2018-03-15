package edu.netcracker.project.logistic.model;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderType {
    private Long id;
    private String name;
    private BigDecimal maxWeight;
    private Long maxHeight;
    private Long maxWidth;
    private Long maxLength;

    public OrderType() {

    }

    public OrderType(Long id, String name, BigDecimal maxWeight, Long maxHeight, Long maxWidth, Long maxLength) {
        this.id = id;
        this.name = name;
        this.maxWeight = maxWeight;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.maxLength = maxLength;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(BigDecimal maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Long getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Long maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Long getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Long maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderType orderType = (OrderType) o;
        return Objects.equals(id, orderType.id) &&
                Objects.equals(name, orderType.name) &&
                Objects.equals(maxWeight, orderType.maxWeight) &&
                Objects.equals(maxHeight, orderType.maxHeight) &&
                Objects.equals(maxWidth, orderType.maxWidth) &&
                Objects.equals(maxLength, orderType.maxLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxWeight, maxHeight, maxWidth, maxLength);
    }

    @Override
    public String toString() {
        return " " + name;
    }
}
