package edu.netcracker.project.logistic.flow.impl;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.maps_wrapper.StaticMap;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.RoleService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class RadiusSelector extends FlowBuilderImpl {
    /*
    * //from super:
    * protected RoleService roleService;
    * protected Office office;
    * protected LatLng center;
    * */
    private CourierType courierToPick;
    private TravelMode travelMode;
    private Person courier;

    private double searchRadius;
    private static final double radiusModifier = 1.5;
    private static final int maxOrderCount = 8;
    private int MAX_RADIUS_STEPS = 5;

    private double currentAproxDistance = 0/*meters*/;
    private BigDecimal currentWeight = new BigDecimal(0.0)/*kg*/;

    private PriorityQueue<Order> candidates;

    private List<Order> resultSequence;
    private DirectionsResult directionsResult;
    private StaticMap staticMap;

    private List<LatLng> path;

    private URL[] icons;

    public RadiusSelector(RoleService roleService, OrderTypeDao orderTypeDao, Office office) {
        super(roleService, orderTypeDao, office);
        resultSequence = new LinkedList<>();
        center = office.getAddress().getLocation();
        courierToPick = CourierType.Driver;
        newPath();

        try {
            icons = new URL[]{//TODO: max different icons - 5: rework
                    new URL("https://image.flaticon.com/icons/png/128/46/46021.png"),         //0 - warehouse
                    new URL("https://image.flaticon.com/icons/png/128/45/45880.png"),         //1 - car
                    new URL("https://image.flaticon.com/icons/png/128/709/709790.png"),       //2 - carVIP
                    new URL("https://image.flaticon.com/icons/png/128/93/93375.png"),         //3 - courier
                    new URL("https://image.flaticon.com/icons/png/128/93/93381.png"),         //4 - courierCar
                    new URL("https://image.flaticon.com/icons/png/128/45/45968.png"),         //5 - personal
                    new URL("https://image.flaticon.com/icons/png/128/702/702441.png")        //6 - personalVIP
            };
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void newPath(){
        Order pivot = pickNextOrder();
        duration = 0;
        distance = 0;
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

            switch(courierToPick){
                case Driver:
                    travelMode = TravelMode.DRIVING;
                    candidates.addAll(driveOrders);
                    candidates.addAll(walkOrders);
                    break;
                case Walker:
                    candidates.addAll(walkOrders);
                    travelMode = TravelMode.WALKING;
                    break;
            }

            searchRadius = mapDistance(office.getAddress().getLocation(), center, travelMode);
            currentAproxDistance = searchRadius;
            currentWeight = pivot.getWeight();
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
                    travelMode = TravelMode.DRIVING;
                    return nextCourier(level+1);
                }
                return walkCouriers.poll();
            case Driver:
                if(driveCouriers.isEmpty()) {
                    courierToPick = CourierType.Walker;
                    travelMode = TravelMode.WALKING;
                    return nextCourier(level+1);
                }
                return driveCouriers.poll();
        }
        return null;
    }

    private void updateTimeAndDistance(Order next){
        currentAproxDistance += mapDistance(center,next.getReceiverAddress().getLocation(),travelMode);
        if(!next.getSenderAddress().equals(office.getAddress()))
            currentAproxDistance += mapDistance(next.getSenderAddress().getLocation(),next.getReceiverAddress().getLocation(),travelMode);
    }

    private Order pickNextOrder(){
        Order next = null;
        switch(courierToPick){
            case Driver:
                next = driveOrders.poll();
                break;
            case Walker:
                next = walkOrders.poll();
                break;
        }
        if(canBePicked(next)) {
            resultSequence.add(next);
            updateTimeAndDistance(next);
        }
        return next;
    }

    private boolean canBePicked(Order o){
        if(o == null)
            return false;
        if(resultSequence.size() >= maxOrderCount)
            return false;

        double distance = mapDistance(center,o.getReceiverAddress().getLocation(), travelMode);
        if(searchRadius <= distance)
            return false;
        /*
         * if(searchRadius <= distance(o.getReceiverAddress().getLocation(), center))
         *    return false;
         */
        {
            double maxDistance = 0;
            double approxSpeed = 0;
            switch (travelMode) {
                case WALKING:
                    maxDistance = getMaxWalkableDistance();
                    approxSpeed = 5/*km/hour*/;

                    //time check
                    if ((resultSequence.size()+1)*clientWaitingTime+(currentAproxDistance+distance)/(approxSpeed*1000.0/60.0/60.0) > 5*60*60/*working day*/)
                        return false;
                    break;
                default:
                    maxDistance = getMaxDrivableDistance();
                    approxSpeed = 40/*km/hour*/;

                    //time check
                    if ((resultSequence.size()+1)*clientWaitingTime+(currentAproxDistance+distance)/(approxSpeed*1000.0/60.0/60.0) > 7*60*60/*working day*/)
                        return false;
                    break;
            }
            //distance check
            if (currentAproxDistance+distance > maxDistance)
                return false;
        }

        //weight check
        if(currentWeight.compareTo(o.getOrderType().getMaxWeight()) > 0)
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
            waypoints[i] = resultSequence.get(i).getReceiverAddress().getLocation();


        switch(courierToPick){
            case Walker:
                travelMode = TravelMode.WALKING;
                break;
            case Driver:
                travelMode = TravelMode.DRIVING;
                break;
        }

        try {
            directionsResult = GoogleApiRequest.DirectionsApi()
                    .origin(office.getAddress().getLocation())
                    .destination(office.getAddress().getLocation())
                    .units(Unit.METRIC)
                    .avoid(DirectionsApi.RouteRestriction.FERRIES)
                    .avoid(DirectionsApi.RouteRestriction.TOLLS)
                    .waypoints(waypoints)
                    .optimizeWaypoints(optimize)
                    .mode(travelMode)
                    .await();
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        path = directionsResult.routes[0].overviewPolyline.decodePath();
        staticMap = GoogleApiRequest.StaticMap()
                .center(new StaticMap.GeoPoint(center.lat,center.lng))
                .path(new StaticMap.Path(StaticMap.Path.Style.DEFAULT,path.toArray(new LatLng[]{})));
        //TODO: set via interface
        staticMap.size(640,640)
                 .scale(2);

        //duration & distance
        for(DirectionsLeg l : directionsResult.routes[0].legs){
            duration+=l.duration.inSeconds;
            distance+=l.distance.inMeters;
        }


        //warehouse marker
        staticMap.marker(new StaticMap.Marker.Style.Builder().icon(icons[0]).color(0x0f0f0f).build(),
                new StaticMap.GeoPoint(office.getAddress().getLocation().lat,office.getAddress().getLocation().lng));

        List<Order> return_value = new ArrayList<>(waypoints.length);
        for(int i : directionsResult.routes[0].waypointOrder)
            return_value.add(resultSequence.get(i));

        resultSequence = return_value;
        //client markers
        List<StaticMap.GeoPoint> markers = new ArrayList<>();
        {
            int index = 0;
            for (Order o : resultSequence) {
                markers.add(new StaticMap.GeoPoint(
                        o.getReceiverAddress().getLocation().lat, o.getReceiverAddress().getLocation().lng)
                );
                staticMap.marker(
                        new StaticMap.Marker.Style.Builder().label((char) ((index++) % 10 + '0')).color(0xff0000).build(),
                        new StaticMap.GeoPoint(
                                o.getReceiverAddress().getLocation().lat, o.getReceiverAddress().getLocation().lng)
                );
                //staticMap.marker(o.getReceiverAddress().getName());
            }
        /*
        staticMap.marker(
                new StaticMap.Marker.Style.Builder().label('P').color(0xff0000).build(),
                markers.toArray(new StaticMap.GeoPoint[] {})
                );
        */
        }

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
    public long getDistance(){
        if(distance == 0){
            calculatePath();
        }
        return distance;
    }

    @Override
    public long getDuration(){
        if(duration == 0){
            calculatePath();
        }
        return duration*resultSequence.size()*clientWaitingTime;
    }

    @Override
    public boolean process() {
        for(int i = 0; i < MAX_RADIUS_STEPS && candidates == null; i++){
            searchRadius*=radiusModifier;
            newPath();
        }

        if(candidates == null) {
            errorMessage = "Candidats queue wasn`t created.";
            return false;
        }

        for(int i = 0; i < MAX_RADIUS_STEPS && candidates.size()==0; i++){
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

        for(int i = 0; i < MAX_RADIUS_STEPS; i++) {
            searchRadius *= radiusModifier;
            while (null != pickNextOrder()) ;
        }

        resultSequence = calculatePath();
        confirmCourier();

        return true;
    }
}
