package edu.netcracker.project.logistic.flow;

import com.google.maps.DirectionsApi;
import com.google.maps.model.*;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.maps_wrapper.StaticMap;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.OrderType;
import edu.netcracker.project.logistic.model.Person;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface FlowBuilder {

    double getMaxWalkableDistance();

    void setMaxWalkableDistance(double maxWalkableDistance);

    double getMaxDrivableDistance();

    void setMaxDrivableDistance(double maxDrivableDistance);

    //TODO: assert is second parameter needed
    boolean add(Person courier, CourierType type);

    Person removeCourier(long courier_id);

    Person getCourier(long courier_id);

    boolean add(Order order);

    int add(Order[] orders);

    int add(Collection<Order> orders);

    Order removeOrder(long order_id);

    Order getOrder(long order_id);

    FlowBuilder optimize(boolean value);

    boolean isOptimized();

    boolean isUseMapRequests();

    FlowBuilder setUseMapRequests(boolean useMapRequests);

    List<Order> getUnused();

    List<Order> getOrders();

    List<Order> getOrders(OrderType type);

    List<Person> getCouriers();

    Queue<Person> getCouriers(CourierType type);

    Map<String,TravelMode> getTravelModeMap();

    void clear();

    //calculatings
    List<Order> calculatePath();

    List<Order> confirmCourier();

    List<Order> getOrdersSequence();

    List<LatLng> getPath();

    StaticMap getStaticMap();

    //in meters
    long getDistance();

    //in seconds
    long getDuration();

    boolean process();

    String getError();

    boolean isFinished();

    static double distance(LatLng a, LatLng b, boolean useMap, TravelMode travelMode) {
        if (useMap)
            return mapDistance(a, b, travelMode);
        else
            return distance(a, b);
    }

    static double distance(LatLng a, LatLng b) {
        double value = Math.sqrt(
                Math.pow(a.lat - b.lat, 2) +
                        Math.pow(a.lng - b.lng, 2));
        return value;
    }

    static double mapDistance(LatLng a, LatLng b, TravelMode travelMode) {
        DistanceMatrix req = null;
        try {
            req = GoogleApiRequest.DistanceMatrixApi()
                    .origins(a)
                    .destinations(b)
                    .mode(travelMode == null ? TravelMode.DRIVING : travelMode)
                    .avoid(DirectionsApi.RouteRestriction.FERRIES)
                    .avoid(DirectionsApi.RouteRestriction.TOLLS)
                    .units(Unit.METRIC)
                    .await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        long distance = 0;
        if (req != null) {
            for (DistanceMatrixElement d : req.rows[0].elements) {
                if (d.status != DistanceMatrixElementStatus.OK) {
                    System.err.println("DistanceMatrixRequest returns " + d.status.name());
                    return Double.MAX_VALUE;
                }
                distance += d.distance.inMeters;
            }
            return (double) distance;
        } else
            return Double.MAX_VALUE;
    }


    /*
    * //TODO: give a better name
    *enum FlowResultType {
    *   PathOnly,
    *    DestinationsOnly,
    *    PathAndDestinations
    *}
    */

    enum CourierType {
        Walker,
        Driver
    }
}
