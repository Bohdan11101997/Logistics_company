package edu.netcracker.project.logistic.flow.impl;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.mapswrapper.StaticMap;
import edu.netcracker.project.logistic.mapswrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.processing.RouteProcessor;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class RadiusSelector extends FlowBuilderImpl {
    /*
     * //from super:
     * protected RoleService roleService;
     * protected Office office;
     * protected LatLng center;
     * */
    private TravelMode travelMode;
    private RouteProcessor.CourierEntry courier;

    private double searchRadius;
    private static final double radiusModifier = 1.5;
    private static final int maxOrderCount = 8;
    private int MAX_RADIUS_STEPS = 5;

    private int count_c2c_orders;
    private double currentAproxDistance = 0/*meters*/;
    private BigDecimal currentWeight = new BigDecimal(0.0)/*kg*/;

    private List<RouteProcessor.OrderEntry> candidates;

    private List<RouteProcessor.OrderEntry> returnOrders;
    private List<RouteProcessor.OrderEntry> resultSequence;
    private DirectionsResult directionsResult;
    private StaticMap staticMap;

    private List<LatLng> path;

    public RadiusSelector(Queue<RouteProcessor.OrderEntry> walkOrders,
                          Queue<RouteProcessor.OrderEntry> driveOrders,
                          Queue<RouteProcessor.CourierEntry> walkCouriers,
                          Queue<RouteProcessor.CourierEntry> driveCouriers,
                          @NotNull Office office) {
        super(walkOrders, driveOrders, walkCouriers, driveCouriers, office);
        this.resultSequence = new LinkedList<>();
        this.returnOrders = new LinkedList<>();
        this.travelMode = TravelMode.DRIVING;
    }

    public void newPath(@NotNull RouteProcessor.OrderEntry pivot) {
        if (resultSequence.size() == 0) {
            resultSequence.add(pivot);
        }
        center = pivot.getOrder().getReceiverAddress().getLocation();
        searchRadius = FlowBuilder.distance(office.getAddress().getLocation(), center, isUseMapRequests(), travelMode);
        candidates = new LinkedList<>();
        count_c2c_orders = 0;

        switch (travelMode) {
            case DRIVING:
                candidates.addAll(walkOrders);
                travelMode = TravelMode.WALKING;
                break;
            default:
                travelMode = TravelMode.DRIVING;
                candidates.addAll(driveOrders);
                candidates.addAll(walkOrders);
                break;
        }

        staticMap = null;
        searchRadius = FlowBuilder.distance(office.getAddress().getLocation(), center, isUseMapRequests(), travelMode);
        currentAproxDistance = searchRadius;
        currentWeight = pivot.getOrder().getWeight();
        duration = 0;
        distance = 0;
    }

    private void updateTimeAndDistance(RouteProcessor.OrderEntry next) {
        currentAproxDistance += FlowBuilder.distance(center, next.getOrder().getReceiverAddress().getLocation(), isUseMapRequests(), travelMode);
    }

    private static boolean nearlyEqual(double a, double b, double tollerance) {
        return Math.abs(a - b) < tollerance;
    }

    private RouteProcessor.OrderEntry pickNextOrder() {
        RouteProcessor.OrderEntry next;
        switch (travelMode) {
            default:
                next = driveOrders.poll();
                if (next != null)
                    break;
            case WALKING:
                next = walkOrders.poll();
                break;
        }
        /*if (nearlyEqual(next.getOrder().getSenderAddress().getLocation().lat,
                office.getAddress().getLocation().lat, 0.001) &&
                nearlyEqual(next.getOrder().getSenderAddress().getLocation().lng,
                        office.getAddress().getLocation().lng, 0.001)) {*/
        if (next == null) {
            return null;
        }
        if(next.getOrder().getOffice() == null) {
            //client_to_client
            if (resultSequence.size() < maxOrderCount-1/*is here 2 places*/ && count_c2c_orders < 1) {
                RouteProcessor.OrderEntry sender = new RouteProcessor.OrderEntry(next);
                sender.getOrder().setReceiverAddress(next.getOrder().getSenderAddress());
                sender.getOrder().setSenderAddress(null);
                sender.setOrderFromClient(true);
                if (canBePicked(sender.getOrder())) {
                    //success
                    resultSequence.add(next);
                    resultSequence.add(sender);
                    currentWeight.add(sender.getOrder().getWeight());
                    updateTimeAndDistance(sender);
                    updateTimeAndDistance(next);
                    /*currentAproxDistance += FlowBuilder.distance(
                            next.getOrder().getSenderAddress().getLocation(),
                            next.getOrder().getReceiverAddress().getLocation(),
                            false,
                            null);*/
                    count_c2c_orders++;
                } else //fail at canBePicked
                    returnOrders.add(next);
            } else //fail at order count
                returnOrders.add(next);
        } else {//usual case
            if (canBePicked(next.getOrder()) && (next.getOrder().getOffice() == office )) {
                resultSequence.add(next);
                currentWeight = currentWeight.add(next.getOrder().getWeight());
                updateTimeAndDistance(next);
            } else //fail at canBePicked or order is at other office
                returnOrders.add(next);
        }
        return next; //always return back
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
                    maxDistance = isUseMapRequests() ? getMaxWalkableDistance() : metersToEarthDegrees(getMaxWalkableDistance());
                    approxSpeed = 5/*km/hour*/;

                    //time check
                    if ((resultSequence.size() + 1) * clientWaitingTime + (currentAproxDistance + distance) / (approxSpeed * 1000.0 / 60.0 / 60.0) > 8 * 60 * 60/*working day*/)
                        return false;
                    break;

                case DRIVING:
                default:
                    maxDistance = isUseMapRequests() ? getMaxDrivableDistance() : metersToEarthDegrees(getMaxDrivableDistance());
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

    public RadiusSelector setMaxRadiusStep(int step) {
        this.MAX_RADIUS_STEPS = step;
        return this;
    }

    @Override
    public List<RouteProcessor.OrderEntry> getUnused() {
        return returnOrders;
    }

    @Override
    public List<RouteProcessor.OrderEntry> getOrders() {
        List<RouteProcessor.OrderEntry> return_value = super.getOrders();
        return_value.addAll(returnOrders);
        return return_value;
    }

    @Override
    public List<RouteProcessor.OrderEntry> calculatePath() {
        duration = 0;
        distance = 0;
        LatLng[] waypoints = new LatLng[resultSequence.size()];
        for (int i = 0; i < waypoints.length; i++)
            waypoints[i] = resultSequence.get(i).getOrder().getReceiverAddress().getLocation();

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

        //warehouse marker
//        staticMap.marker(new StaticMap.Marker.Style.Builder().label('#').color(0x0ff0000).build(),
//                new StaticMap.GeoPoint(office.getAddress().getLocation().lat, office.getAddress().getLocation().lng));

        if (directionsResult.geocodedWaypoints[0].geocoderStatus != GeocodedWaypointStatus.OK) {
            System.err.println("DirectionsApi returned ZERO_RESULTS");
            //sender will always be before receiver
        } else {
            boolean needToReverse = false;
            List<RouteProcessor.OrderEntry> return_value = new ArrayList<>(waypoints.length);
            for (int i : directionsResult.routes[0].waypointOrder) {
                return_value.add(resultSequence.get(i));
                if(count_c2c_orders == 1 && resultSequence.get(i).isOrderFromClient()){
                    //found office-sender
                    for(RouteProcessor.OrderEntry oe: resultSequence){
                        if(oe.getOrder().getOffice() == null){
                            //found sender-receiver Before! office-sender
                            needToReverse = true;
                            break;
                        }
                    }
                }
            }
            if(needToReverse){
                Collections.reverse(path);
                Collections.reverse(return_value);
            }


            path = directionsResult.routes[0].overviewPolyline.decodePath();
            staticMap = GoogleApiRequest.StaticMap()
                    .center(new StaticMap.GeoPoint(center.lat, center.lng))
                    .marker(new StaticMap.Marker.Style.Builder().label('#').color(0x0ff0000).build(),
                            new StaticMap.GeoPoint(office.getAddress().getLocation().lat, office.getAddress().getLocation().lng))
                    .path(new StaticMap.Path(StaticMap.Path.Style.builder().color(0xff4136).build(),
                            path.toArray(new LatLng[]{})));

            //duration & distance
            for (DirectionsLeg l : directionsResult.routes[0].legs) {
                duration += l.duration.inSeconds;
                distance += l.distance.inMeters;
            }

            resultSequence = return_value;
        }

        //client markers
        {
            int index = 0;
            for (RouteProcessor.OrderEntry o : resultSequence) {
                int color = 0xffffff;
                switch (o.getTravelMode()) {
                    case WALKING:
                        color = 0x33ff33;
                        break;
                    case DRIVING:
                        color = 0x3333ff;
                        break;
                }
                if (o.getPriority().equalsIgnoreCase("VIP")) {
                    color = color & 0x00ffff + 0xff0000;
                }
                staticMap.marker(
                        new StaticMap.Marker.Style.Builder().label((char) ((index++) % 10 + '1')).color(color).build(),
                        new StaticMap.GeoPoint(
                                o.getOrder().getReceiverAddress().getLocation().lat,
                                o.getOrder().getReceiverAddress().getLocation().lng)
                );
            }
            //default value
            staticMap.size(640, 640)
                    .scale(2);
        }
        return resultSequence;
    }

    @Override
    public List<RouteProcessor.OrderEntry> getOrdersSequence() {
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
    public DirectionsResult getDirectionsResult(){
        return  directionsResult;
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
    public boolean process(RouteProcessor.OrderEntry pivot, RouteProcessor.CourierEntry courierEntry) {
        newPath(pivot);

        for (int i = 0; i < MAX_RADIUS_STEPS && candidates.size() == 0; i++) {
            searchRadius *= radiusModifier;
            newPath(pivot);
        }

        if (candidates.size() == 0 && resultSequence.size() == 0) {
            errorMessage = "There are no candidates.";
            return false;
        }

        courier = courierEntry;
        if (courier == null) {
            errorMessage = "There are no couriers.";
            return false;
        }

        travelMode = courier.getCourierData().getTravelMode();

        for (int i = 0; i < MAX_RADIUS_STEPS; i++) {
            searchRadius *= radiusModifier;
            //main loop
            while (null != pickNextOrder());
        }

        resultSequence = calculatePath();

        if (candidates != null) {
            if (candidates.size() > 0)
                this.add(candidates);
            candidates = null;
        }

        this.add(returnOrders);
        //add back unpicked orders
        return true;
    }
}
