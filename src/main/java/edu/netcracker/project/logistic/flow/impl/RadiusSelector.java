package edu.netcracker.project.logistic.flow.impl;

import edu.com.google.maps.DirectionsApi;
import edu.com.google.maps.StaticMap;
import edu.com.google.maps.errors.ApiException;
import edu.com.google.maps.model.DirectionsResult;
import edu.com.google.maps.model.LatLng;
import edu.com.google.maps.model.TravelMode;
import edu.com.google.maps.model.Unit;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.RoleService;

import java.io.IOException;
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

    private double searchRadius;
    private static final double radiusModifier = 1.5;
    private static final int maxOrderCount = 8;
    private int MAX_RADIUS_STEPS = 5;

    PriorityQueue<Order> candidates;

    List<Order> resultSequence;
    DirectionsResult directionsResult;
    StaticMap staticMap;

    List<LatLng> path;

    public RadiusSelector(RoleService roleService, Office office) {
        super(roleService, office);
        resultSequence = new LinkedList<>();
        center = office.getAddress().getLocation();
        courierToPick = CourierType.Walker;
        newPath();
    }

    public void newPath(){
        Order pivot = pickNextOrder();
        if(pivot != null) {
            resultSequence.add(pivot);
            center = pivot.getReceiver().getAddress().getLocation();
            candidates = new PriorityQueue<>(11, (o1, o2) -> {
                LatLng l1 = o1.getReceiver().getAddress().getLocation();
                LatLng l2 = o2.getReceiver().getAddress().getLocation();
                Double dist1 = distance(l1, center);
                Double dist2 = distance(l2, center);
                dist1 = updateOrderDistanceIfVip(this, o1, dist1);
                dist2 = updateOrderDistanceIfVip(this, o2, dist2);
                return Double.compare(dist1, dist2);
            });
            searchRadius = distance(office.getAddress().getLocation(), center);

            switch(courierToPick){
                case Driver:
                    candidates.addAll(driveOrders);
                case Walker:
                    candidates.addAll(walkOrders);
            }
        }
    }

    private Person nextCourier() {
        return nextCourier(0);
    }
    private Person nextCourier(int level){
        if(level >= 2)
            return null;
        switch(courierToPick){
            case Walker:
                if(walkCouriers.isEmpty()) {
                    courierToPick = CourierType.Driver;
                    return nextCourier(level+1);
                }
                return walkCouriers.poll();
            case Driver:
                if(driveCouriers.isEmpty()) {
                    courierToPick = CourierType.Walker;
                    return nextCourier(level+1);
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
        if(o == null)
            return false;
        if(resultSequence.size() >= maxOrderCount)
            return false;
        if(searchRadius <= distance(o.getReceiver().getAddress().getLocation(), office.getAddress().getLocation()))
            return false;
        //TODO: make bigger after updating other classes
        return true;
    }

    public void setOrderComparator(Comparator<Order> c){
        List<Order> tmp = new ArrayList<>(candidates.size()+1);
        tmp.addAll(candidates);
        candidates = new  PriorityQueue<>((tmp.size()+1), c);
    }

    public RadiusSelector setMaxRadiusStep(int step){
        this.MAX_RADIUS_STEPS = step;
        return this;
    }

    @Override
    public List<Order> calculatePath() {
        LatLng[] waypoints = new LatLng[resultSequence.size()];
        for(int i = 0; i < waypoints.length; i++)
            waypoints[i] = resultSequence.get(i).getReceiver().getAddress().getLocation();

        TravelMode mode = TravelMode.WALKING;
        switch(courierToPick){
            case Walker:
                mode = TravelMode.WALKING;
                break;
            case Driver:
                mode = TravelMode.DRIVING;
                break;
        }

        try {
            directionsResult = GoogleApiRequest.DirectionsApi()
                    .origin(office.getAddress().getLocation())
                    .destination(waypoints[waypoints.length-1])//TODO: organize better
                    .units(Unit.METRIC)
                    .avoid(DirectionsApi.RouteRestriction.FERRIES)
                    .avoid(DirectionsApi.RouteRestriction.TOLLS)
                    .waypoints(waypoints)
                    .optimizeWaypoints(optimize)
                    .mode(mode)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        path = directionsResult.routes[0].overviewPolyline.decodePath();
        staticMap = GoogleApiRequest.StaticMap().center(new StaticMap.GeoPoint(center.lat,center.lng))
                .path(new StaticMap.Path(StaticMap.Path.Style.DEFAULT,path.toArray(new LatLng[]{})));
        //TODO: set via interface
        staticMap.size(600,600)
                 .scale(4);

        staticMap.marker(new StaticMap.GeoPoint(office.getAddress().getLocation().lat,office.getAddress().getLocation().lng));
        for(Order o : resultSequence)
            //staticMap.marker(o.getReceiver().getAddress().getName());
            staticMap.marker(
                    new StaticMap.GeoPoint(
                            o.getReceiver().getAddress().getLocation().lat,o.getReceiver().getAddress().getLocation().lng
                    )
            );

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
        for(int i = 0; i < 5 && candidates == null; i++){
            searchRadius*=radiusModifier;
            newPath();
        }

        if(candidates == null) {
            errorMessage = "Candidats queue wasn`t created.";
            return false;
        }

        for(int i = 0; i < 5 && candidates.size()==0; i++){
            searchRadius*=radiusModifier;
            newPath();
        }

        if(candidates.size() == 0) {
            errorMessage = "There are no candidates.";
            return false;
        }

        courier = nextCourier();
        if(courier == null) {
            errorMessage = "There are no couriers.";
            return false;
        }

        for(int i = 0; i < 5; i++)
            while(null != pickNextOrder());

        resultSequence = calculatePath();
        confirmCourier();

        try {
            System.out.println(getStaticMap().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
