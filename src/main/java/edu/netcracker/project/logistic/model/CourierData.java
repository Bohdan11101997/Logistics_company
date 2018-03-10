package edu.netcracker.project.logistic.model;

import java.util.Objects;

public class CourierData {
    private Person id;
    private CourierStatus courierStatus;
    private String lastLocation;

    public CourierData() {
    }

    public CourierData(Person id, CourierStatus courier_status, String lastLocation) {
        this.id = id;
        this.courierStatus = courier_status;
        this.lastLocation = lastLocation;
    }

    public Person getId() {
        return id;
    }

    public void setId(Person id) {
        this.id = id;
    }

    public CourierStatus getCourierStatus() {
        return courierStatus;
    }

    public void setCourierStatus(CourierStatus courierStatus) {
        this.courierStatus = courierStatus;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourierData that = (CourierData) o;
        return Objects.equals(id, that.id) &&
                courierStatus == that.courierStatus &&
                Objects.equals(lastLocation, that.lastLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courierStatus, lastLocation);
    }
}