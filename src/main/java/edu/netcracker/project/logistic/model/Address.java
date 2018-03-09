package edu.netcracker.project.logistic.model;

import com.google.maps.model.LatLng;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;

public class Address {
    private Long id;
    private String name;
    private LatLng location;

    public Address(String name) {
        this.name = name;
        this.location = null;
    }

    public Address(Long id, String name) {
        this.id = id;
        this.name = name;
        this.location = null;
    }

    public Address(Long id, LatLng location) {
        this.id = id;
        this.name = null;
        this.location = location;
    }

    //TODO: add default name or create rerequest
    private static String LocationToAddress(LatLng location) {
        String address;
        try {
            address = GoogleApiRequest.GeocodingApi().latlng(location).await()[0].formattedAddress;
        } catch (Exception e) {
            address = null;
        }
        return address;
    }

    private static LatLng AdressToLocation(String name) {
        LatLng location;
        try {
            location = GoogleApiRequest.GeocodingApi().address(name).await()[0].geometry.location;
        } catch (Exception e) {
            location = null;
        }
        return location;
    }

    public Address() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        if (name == null && location != null)
            return LocationToAddress(location);
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        if (name != null && location == null)
            return AdressToLocation(name);
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +  //NEW
                '}';
    }
}
