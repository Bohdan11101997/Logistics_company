package edu.netcracker.project.logistic.model;

import com.google.maps.DirectionsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

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
        /*GeocodingResult result = getListOfAddresses(location)[0];
        for(AddressComponent ac : result.addressComponents){
            for(AddressComponentType act : ac.types)
                if(act.toCanonicalLiteral().equalsIgnoreCase("locality"))
                    return result.formattedAddress;
        }
        return "";
        */
        return getListOfAddresses(location)[0].formattedAddress;
    }

    private static LatLng AddressToLocation(String name) {
        GeocodingResult[] addressList = getListOfAddresses(name);
        if (addressList.length == 0) {
            return null;
        }
        /*GeocodingResult result = addressList[0];
        for(AddressComponent ac : result.addressComponents){
            for(AddressComponentType act : ac.types)
                if(act.toCanonicalLiteral().equalsIgnoreCase("locality"))
                    return result.geometry.location;
        }
        return null;*/
        return addressList[0].geometry.location;
    }

    public static GeocodingResult[] getListOfAddresses(LatLng location){
        GeocodingResult[] result = null;
        try {
            result = GoogleApiRequest.GeocodingApi().latlng(location).
                    components(ComponentFilter.country("ua"))
                    .bounds(new LatLng(50.243848, 30.204895),new LatLng(50.674379, 30.735831))
                    .region("ua").await();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public static GeocodingResult[] getListOfAddresses(String address){
        GeocodingResult[] result = null;
        try {
            result = GoogleApiRequest.GeocodingApi().address(address)
            .components(ComponentFilter.country("ua"))
                    .bounds(new LatLng(50.243848, 30.204895),new LatLng(50.674379, 30.735831))
                    .region("ua").await();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
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
//        if (name == null && location != null)
//            return LocationToAddress(location);
        return name;
    }

    public void setName(String name) {
        if(this.name != name) {
            this.location = null;
            this.name = name;
        }
    }

    public LatLng getLocation() {
        if (name != null && location == null)
            return AddressToLocation(name);
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean check(String address) {
        return check(new Address(address), TravelMode.WALKING);
    }

    public boolean check(String address, TravelMode travelMode ) {
        return check(new Address(address), travelMode);
    }

    public boolean check(Address with) {
        return check(with, TravelMode.WALKING);
    }

    public boolean check(Address with, TravelMode travelMode) {
        //TODO: change this later
        DistanceMatrix result;
        try {
            result = GoogleApiRequest.DistanceMatrixApi()
                    .origins(this.getLocation())
                    .destinations(with.getLocation())
                    .mode(travelMode)
                    .avoid(DirectionsApi.RouteRestriction.FERRIES)
                    .avoid(DirectionsApi.RouteRestriction.TOLLS)
                    .await();

            return (result.rows[0].elements[0].status == DistanceMatrixElementStatus.OK
                    && result.rows[0].elements[0].fare.value.equals(BigDecimal.valueOf(0.0))//Check on money wastes
            );
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException e )
        {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address that = (Address) o;
        return Objects.equals(id, that.id) &&
                name == that.name &&
                Objects.equals(location, that.location);
    }

    @Override
    public String toString() {
        return " " + name;
    }
}
