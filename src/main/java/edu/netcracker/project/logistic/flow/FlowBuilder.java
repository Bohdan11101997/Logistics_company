package edu.netcracker.project.logistic.flow;

import com.google.maps.DirectionsApi;
import com.google.maps.model.*;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.maps_wrapper.StaticMap;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.OrderType;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.processing.RouteProcessor;

import java.util.*;

public interface FlowBuilder {

    void setOffice(Office office);

    Office getOffice();

    double getMaxWalkableDistance();

    void setMaxWalkableDistance(double maxWalkableDistance);

    double getMaxDrivableDistance();

    void setMaxDrivableDistance(double maxDrivableDistance);

    boolean add(RouteProcessor.OrderEntry order);

    int add(RouteProcessor.OrderEntry[] orders);

    int add(Collection<RouteProcessor.OrderEntry> orders);

    FlowBuilder optimize(boolean value);

    boolean isOptimized();

    boolean isUseMapRequests();

    FlowBuilder setUseMapRequests(boolean useMapRequests);

    List<RouteProcessor.OrderEntry> getUnused();

    List<RouteProcessor.OrderEntry> getOrders();

    List<RouteProcessor.OrderEntry> getOrders(OrderType type);

    List<RouteProcessor.CourierEntry> getCouriers();

    Queue<RouteProcessor.CourierEntry> getWalkingCouriers();

    Queue<RouteProcessor.CourierEntry> getDrivingCouriers();

    //calculatings
    List<RouteProcessor.OrderEntry> calculatePath();

    List<RouteProcessor.OrderEntry> getOrdersSequence();

    List<LatLng> getPath();

    StaticMap getStaticMap();

    DirectionsResult getDirectionsResult();

    //in meters
    long getDistance();

    //in seconds
    long getDuration();

    boolean process(RouteProcessor.OrderEntry pivot, RouteProcessor.CourierEntry courierEntry);

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

    static double updateOrderDistanceIfVip(RouteProcessor.OrderEntry o, double distance) {
        if (o.getPriority().equalsIgnoreCase("VIP")) {
            distance = (Double.MIN_VALUE + distance);
        }
        return distance;
    }

    static Comparator<RouteProcessor.OrderEntry> makeOrderComparator(LatLng center, boolean useMap, TravelMode travelMode) {
        return (RouteProcessor.OrderEntry o1, RouteProcessor.OrderEntry o2) -> {
            LatLng l1 = o1.getOrder().getReceiverAddress().getLocation();
            LatLng l2 = o2.getOrder().getReceiverAddress().getLocation();
            Double dist1 = FlowBuilder.distance(l1, center, useMap, travelMode);
            Double dist2 = FlowBuilder.distance(l2, center, useMap, travelMode);
            dist1 = updateOrderDistanceIfVip(o1, dist1);
            dist2 = updateOrderDistanceIfVip(o2, dist2);
            return Double.compare(dist1, dist2);
        };
    }

    static Comparator<RouteProcessor.CourierEntry> makeCourierComparator(LatLng center, boolean useMap, TravelMode travelMode) {
        return (RouteProcessor.CourierEntry ce1, RouteProcessor.CourierEntry ce2) -> {
            LatLng l1 = ce1.getLastLocation();
            LatLng l2 = ce2.getLastLocation();
            Double dist1 = FlowBuilder.distance(l1, center, useMap, travelMode);
            Double dist2 = FlowBuilder.distance(l2, center, useMap, travelMode);
            return Double.compare(dist1, dist2);
        };
    }

    /*
    *enum FlowResultType {
    *   PathOnly,
    *    DestinationsOnly,
    *    PathAndDestinations
    *}
    */
}
