package edu.netcracker.project.logistic.flow.impl;

import edu.com.google.maps.DirectionsApi;
import edu.com.google.maps.StaticMap;
import edu.com.google.maps.model.DirectionsResult;
import edu.com.google.maps.model.LatLng;
import edu.com.google.maps.model.TravelMode;
import edu.com.google.maps.model.Unit;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.Person;

import java.net.MalformedURLException;
import java.util.*;

public class RadiusSelector extends FlowBuilderImpl {
    /*
    * //from super:
    * protected RoleCrudDao roleCrudDao;
    * protected Office office;
    * protected LatLng center;
    * */
    CourierType courierToPick;
    Person courier;

    private double searchRadius = 0.0;
    private double radiusModifier = 1.5;
    private static final int maxOrderCount = 8;

    PriorityQueue<Order> candidates;

    List<Order> resultSequence;
    DirectionsResult directionsResult;
    StaticMap staticMap;

    List<LatLng> path;

    public RadiusSelector(RoleCrudDao roleCrudDao, Office office) {
        super(roleCrudDao, office);
        resultSequence = new LinkedList<>();
        center = office.getAddress().getLocation();
        newPath();
    }

    public void newPath(){
        Order pivot = pickNextOrder();
        if(pivot != null) {
            resultSequence.add(pivot);
            center = pivot.getReceiverAddress().getLocation();
            candidates = new PriorityQueue<>(11, (o1, o2) -> {
                LatLng l1 = o1.getReceiverAddress().getLocation();
                LatLng l2 = o2.getReceiverAddress().getLocation();
                Double dist1 = distance(l1, center);
                Double dist2 = distance(l2, center);
                dist1 = updateOrderDistanceIfVip(this, o1, dist1);
                dist2 = updateOrderDistanceIfVip(this, o2, dist2);
                return Double.compare(dist1, dist2);
            });
            searchRadius = distance(office.getAddress().getLocation(), center);

            //TODO: optimize
            switch (courierToPick){
                case Driver:
                    candidates.addAll(driveOrders);
                case Walker:
                    //walker will not get any driver orders
                    candidates.addAll(walkOrders);
            }
        }
    }

    private Person nextCourier() {
        return nextCourier(0);
    }
    private Person nextCourier(int level){
        if(level == 2)
            return null;
        switch(courierToPick){
            case Walker:
                if(walkCouriers.isEmpty()) {
                    courierToPick = CourierType.Driver;
                    return nextCourier();
                }
                return walkCouriers.poll();
            case Driver:
                if(driveCouriers.isEmpty()) {
                    courierToPick = CourierType.Walker;
                    return nextCourier();
                }
                return driveCouriers.poll();
        }
        return null;
    }

    private Order pickNextOrder(){
        Order next = null;
        switch(courierToPick){
            case Driver:
                next = driveOrders.poll();
                if(canBePicked(next)) {
                    resultSequence.add(next);
                    return next;
                }
            case Walker:
                next = walkOrders.poll();
                if(canBePicked(next)) {
                    resultSequence.add(next);
                    return next;
                }
        }
        return next;
    }

    private boolean canBePicked(Order o){
        if(resultSequence.size() >= maxOrderCount)
            return false;
        if(searchRadius <= distance(o.getReceiverAddress().getLocation(), office.getAddress().getLocation()))
            return false;
        //TODO: make bigger after updating other classes
        return true;
    }

    public void setOrderComparator(Comparator<Order> c){
        List<Order> tmp = new ArrayList<>(candidates.size()+1);
        tmp.addAll(candidates);
        candidates = new  PriorityQueue<>((tmp.size()+1), c);
    }

    @Override
    public List<Order> calculatePath() {
        LatLng[] waypoints = new LatLng[resultSequence.size()];
        for(int i = 0; i < waypoints.length; i++)
            waypoints[i] = resultSequence.get(i).getReceiverAddress().getLocation();

        TravelMode mode = TravelMode.WALKING;
        switch(courierToPick){
            case Walker:
                mode = TravelMode.TRANSIT;
                break;
            case Driver:
                mode = TravelMode.DRIVING;
                break;
        }

        directionsResult = GoogleApiRequest.DirectionsApi().origin(office.getAddress().getLocation())
                .units(Unit.METRIC)
                .avoid(DirectionsApi.RouteRestriction.FERRIES)
                .avoid(DirectionsApi.RouteRestriction.TOLLS)
                .waypoints(waypoints)
                .optimizeWaypoints(optimize)
                .mode(mode).awaitIgnoreError()
        ;
        path = directionsResult.routes[0].overviewPolyline.decodePath();
        staticMap = GoogleApiRequest.StaticMap().center(new StaticMap.GeoPoint(center.lat,center.lng))
                .path(new StaticMap.Path(StaticMap.Path.Style.DEFAULT,path.toArray(new LatLng[]{})));

        for(Order o : resultSequence)
            staticMap.marker(o.getReceiverAddress().getName());

        List<Order> return_value = new ArrayList<>(waypoints.length);
        for(int i : directionsResult.routes[0].waypointOrder)
            return_value.add(resultSequence.get(i));
        resultSequence = return_value;
        return resultSequence;
    }

    @Override
    public List<Order> confirmCourier() {
        for(Order o : resultSequence){
            o.setCourier(courier);
            //TODO: update database
        }
        return resultSequence;
    }

    @Override
    public List<Order> getOrdersSequence() {
        return resultSequence;
    }

    @Override
    public List<LatLng> getPath() {
        if(path == null){
            calculatePath();
        }
        return path;
    }

    @Override
    public StaticMap getStaticMap() {
        if(staticMap == null){
            calculatePath();
        }
        return staticMap;
    }

    @Override
    public boolean process() {

        if(candidates.size()==0){
            return false;
        }
        courier = nextCourier();
        if(courier == null)
            return false;

        while(null!=pickNextOrder());

        resultSequence = calculatePath();

        try {
            System.out.println(getStaticMap().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
