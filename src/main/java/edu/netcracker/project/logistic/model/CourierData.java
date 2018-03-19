package edu.netcracker.project.logistic.model;

import com.google.maps.model.TravelMode;

import java.util.List;
import java.util.Objects;

public class CourierData {
    private Person id;
    private CourierStatus courierStatus;
    private String lastLocation;
    private TravelMode travelMode;
    private Route route;

    public CourierData() {
    }

    public CourierData(Person id, CourierStatus courier_status, String lastLocation, TravelMode travelMode, Route route) {
        this.id = id;
        this.courierStatus = courier_status;
        this.lastLocation = lastLocation;
        this.travelMode = travelMode;
        this.route = route;
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

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourierData that = (CourierData) o;
        return Objects.equals(id, that.id) &&
                courierStatus == that.courierStatus &&
                Objects.equals(lastLocation, that.lastLocation) &&
                Objects.equals(travelMode, that.travelMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courierStatus, lastLocation, travelMode);
    }
}
