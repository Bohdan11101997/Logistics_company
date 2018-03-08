package edu.netcracker.project.logistic.model;

import java.util.Objects;

public class Courier_data {

    private Person id;
    private COURIER_STATUS courier_status;
    private String last_location;


    public Courier_data() {
    }

    public Courier_data(Person id, COURIER_STATUS courier_status, String last_location) {
        this.id = id;
        this.courier_status = courier_status;
        this.last_location = last_location;
    }

    public Person getId() {
        return id;
    }

    public void setId(Person id) {
        this.id = id;
    }

    public COURIER_STATUS getCourier_status() {
        return courier_status;
    }

    public void setCourier_status(COURIER_STATUS courier_status) {
        this.courier_status = courier_status;
    }

    public String getLast_location() {
        return last_location;
    }

    public void setLast_location(String last_location) {
        this.last_location = last_location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Courier_data that = (Courier_data) o;
        return Objects.equals(id, that.id) &&
                courier_status == that.courier_status &&
                Objects.equals(last_location, that.last_location);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, courier_status, last_location);
    }
}
