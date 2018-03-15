package edu.netcracker.project.logistic.flow.impl;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.maps_wrapper.StaticMap;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.model.*;
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

    private List<Order> returnOrders;
    private List<Order> resultSequence;
    private DirectionsResult directionsResult;
    private StaticMap staticMap;

    private List<LatLng> path;

    public RadiusSelector(RoleService roleService, OrderTypeDao orderTypeDao, CourierDataDao courierDataDao, Office office) {
        super(roleService, orderTypeDao, courierDataDao, office);
        resultSequence = new LinkedList<>();
        center = office.getAddress().getLocation();
        courierToPick = CourierType.Driver;
        returnOrders = new LinkedList<>();
    }

    public void newPath() {
        Order pivot = null;
        returnOrders = new ArrayList<>();
        resultSequence = new ArrayList<>();
        //returnOrders.addAll(resultSequence);
        searchRadius = 99999.999999;
        do {
            if (pivot != null)
                returnOrders.add(pivot);
            pivot = pickNextOrder();
        } while (!canBePicked(pivot));

        duration = 0;
        distance = 0;
        if (pivot != null) {
            resultSequence.add(pivot);
            center = pivot.getReceiverAddress().getLocation();
            searchRadius = FlowBuilder.distance(office.getAddress().getLocation(), center, isUseMapRequests(), travelMode);
            candidates = new PriorityQueue<>(11, makeOrderComparator(this, center, false, null));

            switch (courierToPick) {
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

            staticMap = null;
            searchRadius = FlowBuilder.distance(office.getAddress().getLocation(), center, isUseMapRequests(), travelMode);
            currentAproxDistance = searchRadius;
            currentWeight = pivot.getWeight();
        }
    }

    private Person nextCourier() {
        return nextCourier(0);
    }

    private Person nextCourier(int level) {
        if (level >= 2)
            return null;
        switch (courierToPick) {
            case Walker:
                if (walkCouriers.isEmpty()) {
                    courierToPick = CourierType.Driver;
                    travelMode = TravelMode.DRIVING;
                    return nextCourier(level + 1);
                }
                return walkCouriers.poll();
            case Driver:
                if (driveCouriers.isEmpty()) {
                    courierToPick = CourierType.Walker;
                    travelMode = TravelMode.WALKING;
                    return nextCourier(level + 1);
                }
                return driveCouriers.poll();
        }
        return null;
    }

    private void updateTimeAndDistance(Order next) {
        currentAproxDistance += FlowBuilder.distance(center, next.getReceiverAddress().getLocation(), isUseMapRequests(), travelMode);
        //if(!next.getSenderAddress().equals(office.getAddress()))
        //    currentAproxDistance += FlowBuilder.distance(center, next.getReceiverAddress().getLocation(), isUseMapRequests(),travelMode);
    }

    private static boolean nearlyEqual(double a, double b, double tollerance) {
        return Math.abs(a - b) < tollerance;
    }

    private Order pickNextOrder() {
        Order next = null;
        switch (courierToPick) {
            case Driver:
                next = driveOrders.poll();
                if (next != null)
                    break;
            case Walker:
                next = walkOrders.poll();
                break;
        }

        try {
            if (nearlyEqual(next.getSenderAddress().getLocation().lat, office.getAddress().getLocation().lat, 0.001) &&
                    nearlyEqual(next.getSenderAddress().getLocation().lng, office.getAddress().getLocation().lng, 0.001)) {
                if (resultSequence.size() > 0 && resultSequence.size() < 7) {
                    Order sender = new Order(next);
                    sender.setReceiverAddress(next.getSenderAddress());
                    sender.setSenderAddress(office.getAddress());
                    if (canBePicked(sender) && canBePicked(next)) {
                        resultSequence.add(next);
                        resultSequence.add(sender);
                    } else
                        returnOrders.add(next);
                } else
                    returnOrders.add(next);

            } else {
                if (canBePicked(next)) {
                    resultSequence.add(next);
                    updateTimeAndDistance(next);
                } else
                    returnOrders.add(next);
            }
            return next;
        } catch (NullPointerException e) {
            returnOrders.add(next);
            return next;
        }
    }

    private boolean canBePicked(Order o) {
        if (o == null)
            return false;
        if (resultSequence.size() >= maxOrderCount)
            return false;

        double distance = FlowBuilder.distance(office.getAddress().getLocation(), center, isUseMapRequests(), travelMode);
        if (distance >= searchRadius)
            return false;
        if (travelMode != null)//not the first check
        {
            double maxDistance;
            double approxSpeed;
            switch (travelMode) {
                case WALKING:
                    maxDistance = getMaxWalkableDistance();
                    approxSpeed = 5/*km/hour*/;

                    //time check
                    if ((resultSequence.size() + 1) * clientWaitingTime + (currentAproxDistance + distance) / (approxSpeed * 1000.0 / 60.0 / 60.0) > 8 * 60 * 60/*working day*/)
                        return false;
                    break;

                case DRIVING:
                default:
                    maxDistance = getMaxDrivableDistance();
                    approxSpeed = 40/*km/hour*/;

                    //time check
                    if ((resultSequence.size() + 1) * clientWaitingTime + (currentAproxDistance + distance) / (approxSpeed * 1000.0 / 60.0 / 60.0) > 8 * 60 * 60/*working day*/)
                        return false;
                    break;
            }
            //distance check
            if (currentAproxDistance + distance > maxDistance)
                return false;
        }

        //weight check
        if (currentWeight.compareTo(o.getOrderType().getMaxWeight()) > 0)
            return false;


        //TODO: make bigger after updating other classes
        return true;
    }

    public void setOrderComparator(Comparator<Order> c) {
        List<Order> tmp = new ArrayList<>(candidates.size() + 1);
        tmp.addAll(candidates);
        candidates = new PriorityQueue<>((tmp.size() + 1), c);
    }

    public RadiusSelector setMaxRadiusStep(int step) {
        this.MAX_RADIUS_STEPS = step;
        return this;
    }

    @Override
    public List<Order> getUnused() {
        return returnOrders;
    }

    @Override
    public List<Order> getOrders() {
        List<Order> return_value = super.getOrders();
        return_value.addAll(returnOrders);
        return return_value;
    }

    @Override
    public List<Order> calculatePath() {
        LatLng[] waypoints = new LatLng[resultSequence.size()];
        for (int i = 0; i < waypoints.length; i++)
            waypoints[i] = resultSequence.get(i).getReceiverAddress().getLocation();

        switch (courierToPick) {
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
            errorMessage = "StaticMapsError";
            return resultSequence;  //early out
        }
        if (directionsResult.geocodedWaypoints[0].geocoderStatus != GeocodedWaypointStatus.OK) {
            System.err.println("DirectionsApi returned ZERO_RESULTS");
        } else {
            path = directionsResult.routes[0].overviewPolyline.decodePath();
            staticMap = GoogleApiRequest.StaticMap()
                    .center(new StaticMap.GeoPoint(center.lat, center.lng))
                    .path(new StaticMap.Path(StaticMap.Path.Style.builder().color(0xff4136).build(),
                            path.toArray(new LatLng[]{})));

            //duration & distance
            for (DirectionsLeg l : directionsResult.routes[0].legs) {
                duration += l.duration.inSeconds;
                distance += l.distance.inMeters;
            }
        }

        //warehouse marker
        staticMap.marker(new StaticMap.Marker.Style.Builder().label('#').color(0x0ff0000).build(),
                new StaticMap.GeoPoint(office.getAddress().getLocation().lat, office.getAddress().getLocation().lng));

        List<Order> return_value = new ArrayList<>(waypoints.length);
        for (int i : directionsResult.routes[0].waypointOrder)
            return_value.add(resultSequence.get(i));

        resultSequence = return_value;
        //client markers
        {
            int index = 0;
            for (Order o : resultSequence) {
                int color = 0xffffff;
                switch (decide(o)) {
                    case WALKING:
                        color = 0x33ff33;
                        break;
                    case DRIVING:
                        color = 0x3333ff;
                        break;
                }
                if (isVip(o)) {
                    color = color & 0x00ffff + 0xff0000;
                }
                staticMap.marker(
                        new StaticMap.Marker.Style.Builder().label((char) ((index++) % 10 + '0')).color(color).build(),
                        new StaticMap.GeoPoint(
                                o.getReceiverAddress().getLocation().lat, o.getReceiverAddress().getLocation().lng)
                );
            }
            //TODO: set via interface
            staticMap.size(640, 640)
                    .scale(2);
        }

        return resultSequence;
    }

    @Override
    public List<Order> confirmCourier() {
        try {
            for (Order o : resultSequence) {
                CourierData cd = courierDataDao.findOne(courier).get();
                while (cd == null) {
                    if (getCouriers().size() == 0)
                        throw new IllegalStateException("No couriers data's in the base");
                    courier = nextCourier();
                    cd = courierDataDao.findOne(courier).get();
                }
                o.setCourier(courier);
                cd.setCourierStatus(CourierStatus.ON_WAY);

            }
        } catch (IllegalStateException e) {
            System.err.println("IllegalStateException + " + e.getMessage());
        }
        //TODO: update database
        return resultSequence;
    }

    @Override
    public List<Order> getOrdersSequence() {
        return resultSequence;
    }

    @Override
    public List<LatLng> getPath() {
        if (path == null) {
            calculatePath();
        }
        return path;
    }

    @Override
    public StaticMap getStaticMap() {
        if (staticMap == null) {
            calculatePath();
        }
        return staticMap;
    }

    @Override
    public long getDistance() {
        if (distance == 0) {
            calculatePath();
        }
        return distance;
    }

    @Override
    public long getDuration() {
        if (duration == 0) {
            calculatePath();
        }
        return duration * resultSequence.size() * clientWaitingTime;
    }

    @Override
    public boolean process() {
        for (int i = 0; i < MAX_RADIUS_STEPS && candidates == null; i++) {
            searchRadius *= radiusModifier;
            newPath();
        }

        if (candidates == null) {
            errorMessage = "Candidats queue wasn`t created.";
            return false;
        }

        for (int i = 0; i < MAX_RADIUS_STEPS && candidates.size() == 0; i++) {
            searchRadius *= radiusModifier;
            newPath();
        }

        if (candidates.size() == 0 && resultSequence.size() == 0) {
            errorMessage = "There are no candidates.";
            return false;
        }

        courier = nextCourier();
        if (courier == null) {
            errorMessage = "There are no couriers.";
            return false;
        }

        for (int i = 0; i < MAX_RADIUS_STEPS; i++) {
            searchRadius *= radiusModifier;
            while (null != pickNextOrder()) ;
        }

        resultSequence = calculatePath();
        confirmCourier();

        if (candidates != null) {
            if (candidates.size() > 0)
                this.add(candidates);
            candidates = null;
        }
        return true;
    }
}
