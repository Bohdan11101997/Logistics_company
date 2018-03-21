package edu.netcracker.project.logistic.model;

import com.google.maps.model.TravelMode;

import java.util.Objects;

public class CourierData {
    private Person courier;
    private CourierStatus courierStatus;
    private String lastLocation;
    private TravelMode travelMode;
    private Route route;

    public CourierData() {
    }

    public CourierData(Person courier, CourierStatus courier_status, String lastLocation, TravelMode travelMode, Route route) {
        this.courier = courier;
        this.courierStatus = courier_status;
        this.lastLocation = lastLocation;
        this.travelMode = travelMode;
        this.route = route;
    }

    public Person getCourier() {
        return courier;
    }

    public void setCourier(Person courier) {
        this.courier = courier;
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
        return Objects.equals(courier, that.courier) &&
                courierStatus == that.courierStatus &&
                Objects.equals(lastLocation, that.lastLocation) &&
                Objects.equals(travelMode, that.travelMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courier, courierStatus, lastLocation, travelMode);
    }
}
