package edu.netcracker.project.logistic.flow.impl;

import com.google.maps.model.*;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.processing.RouteProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
//import org.apache.tomcat.util.collections.SynchronizedQueue;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public abstract class FlowBuilderImpl implements FlowBuilder {

    protected double maxWalkableDistance = 15 * 1000.0/*m*/;
    protected double maxDrivableDistance = 300 * 1000.0/*m*/;

    public static long clientWaitingTime = 15 * 60/*seconds*/;

    protected boolean optimize = false;

    protected Queue<RouteProcessor.OrderEntry> walkOrders;
    protected Queue<RouteProcessor.OrderEntry> driveOrders;
    protected Queue<RouteProcessor.CourierEntry> walkCouriers;
    protected Queue<RouteProcessor.CourierEntry> driveCouriers;

    protected Office office;
    protected LatLng center;

    private boolean useMapRequests = true;

    protected long distance;
    protected long duration;

    protected String errorMessage = "No error";

    @Deprecated
    private FlowBuilderImpl() {
        this(null ,null ,null ,null ,null );
    }

    public FlowBuilderImpl(Queue<RouteProcessor.OrderEntry> walkOrders,
                           Queue<RouteProcessor.OrderEntry> driveOrders,
                           Queue<RouteProcessor.CourierEntry> walkCouriers,
                           Queue<RouteProcessor.CourierEntry> driveCouriers,
                           @NotNull Office office) {
        this.walkOrders = walkOrders;
        this.driveOrders = driveOrders;
        this.walkCouriers = walkCouriers;
        this.driveCouriers = driveCouriers;
        this.office = office;
        this.center = office.getAddress().getLocation();
    }

    protected void reset(Office office, boolean useMap) {
        walkOrders = new PriorityBlockingQueue<>(11, FlowBuilder.makeOrderComparator(office.getAddress().getLocation(), useMap, TravelMode.WALKING));
        driveOrders = new PriorityBlockingQueue<>(11, FlowBuilder.makeOrderComparator(office.getAddress().getLocation(), useMap, TravelMode.DRIVING));
        walkCouriers = new PriorityBlockingQueue<>(11, FlowBuilder.makeCourierComparator(office.getAddress().getLocation(), useMap, TravelMode.WALKING));
        driveCouriers = new PriorityBlockingQueue<>(11, FlowBuilder.makeCourierComparator(office.getAddress().getLocation(), useMap, TravelMode.DRIVING));
    }


    @Override
    public void setOffice(Office office){
        this.office = office;
        reset(office,isUseMapRequests());
    }

    @Override
    public Office getOffice(){
        return office;
    }

    @Override
    public double getMaxWalkableDistance() {
        return maxWalkableDistance;
    }

    @Override
    public void setMaxWalkableDistance(double maxWalkableDistance) {
        if (maxWalkableDistance > 0)
            this.maxWalkableDistance = maxWalkableDistance;
        else this.maxWalkableDistance = 0;
    }

    @Override
    public double getMaxDrivableDistance() {
        return maxDrivableDistance;
    }

    @Override
    public void setMaxDrivableDistance(double maxDrivableDistance) {
        this.maxDrivableDistance = maxDrivableDistance;
    }

    public static double metersToEarthDegrees(double meters) {
        return meters * 6.0 / 1000.0;
    }

    // false if adding failed
    @Override
    public boolean add(RouteProcessor.OrderEntry orderEntry) {
        if (orderEntry == null)
            return false;
        if (orderEntry.getOrder().getOrderType() == null)
            return false;
        if (orderEntry.getOrder().getOrderStatus() == null)
            return false;

        switch (orderEntry.getTravelMode()) {
            case WALKING:
                if (FlowBuilder.distance(orderEntry.getOrder().getReceiverAddress().getLocation(), office.getAddress().getLocation()) < metersToEarthDegrees(maxWalkableDistance) / 8) {
                    if (!(orderEntry.getOrder().getSenderAddress() == null ||
                            FlowBuilder.distance(orderEntry.getOrder().getSenderAddress().getLocation(),
                                    office.getAddress().getLocation()) > metersToEarthDegrees(maxWalkableDistance) / 8))
                        return walkOrders.add(orderEntry);
                }
                System.err.println("Order moves from Walk to Driver orders due to location.");
            default:
                return driveOrders.add(orderEntry);
        }
    }

    @Override
    public int add(RouteProcessor.OrderEntry[] orderEntries) {
        int counter = 0;
        for (RouteProcessor.OrderEntry o : orderEntries)
            if (!add(o))
                return counter;
            else counter++;
        return counter;
    }

    @Override
    public int add(Collection<RouteProcessor.OrderEntry> orderEntries) {
        int counter = 0;
        for (RouteProcessor.OrderEntry o : orderEntries)
            if (!add(o))
                return counter;
            else counter++;

        return counter;
    }

    @Override
    public FlowBuilder optimize(boolean value) {
        optimize = value;
        return this;
    }

    @Override
    public boolean isOptimized() {
        return optimize;
    }

    @Override
    public boolean isUseMapRequests() {
        return useMapRequests;
    }

    @Override
    @Transactional
    public FlowBuilder setUseMapRequests(boolean useMapRequests) {
        this.useMapRequests = useMapRequests;
        List<RouteProcessor.OrderEntry> orderDrive = new ArrayList<>(driveOrders);
        List<RouteProcessor.OrderEntry> orderWalk = new ArrayList<>(walkOrders);
        List<RouteProcessor.CourierEntry> courierDrive = new ArrayList<>(driveCouriers);
        List<RouteProcessor.CourierEntry> courierWalk = new ArrayList<>(walkCouriers);
        this.driveOrders = null;
        this.walkOrders = null;
        this.driveCouriers = null;
        this.walkCouriers = null;
        reset(office, useMapRequests);
        this.driveOrders.addAll(orderDrive);
        this.walkOrders.addAll(orderWalk);
        this.driveCouriers.addAll(courierDrive);
        this.walkCouriers.addAll(courierWalk);
        return this;
    }

    @Override
    public abstract List<RouteProcessor.OrderEntry> getUnused();

    @Override
    public List<RouteProcessor.OrderEntry> getOrders() {
        List<RouteProcessor.OrderEntry> return_value = new ArrayList<>();
        return_value.addAll(walkOrders);
        return_value.addAll(driveOrders);
        return return_value;
    }

    @Override
    public List<RouteProcessor.OrderEntry> getOrders(OrderType type) {
        List<RouteProcessor.OrderEntry> return_value = new ArrayList<>();

        for (RouteProcessor.OrderEntry o : walkOrders)
            if (o.getOrder().getOrderType().equals(type))
                return_value.add(o);

        for (RouteProcessor.OrderEntry o : driveOrders)
            if (o.getOrder().getOrderType().equals(type))
                return_value.add(o);
        if (return_value.size() == 0)
            return null;
        else return return_value;
    }

    @Override
    public List<RouteProcessor.CourierEntry> getCouriers() {
        List<RouteProcessor.CourierEntry> return_value = new ArrayList<>();
        return_value.addAll(walkCouriers);
        return_value.addAll(driveCouriers);
        return return_value;
    }

    @Override
    public Queue<RouteProcessor.CourierEntry> getWalkingCouriers(){
        return walkCouriers;
    }

    @Override
    public Queue<RouteProcessor.CourierEntry> getDrivingCouriers(){
        return driveCouriers;
    }

    /*
    @Override
    public abstract List<RouteProcessor.OrderEntry> calculatePath();

    @Override
    public abstract List<RouteProcessor.OrderEntry> getOrdersSequence();

    @Override
    public abstract List<LatLng> getPath();

    @Override
    public abstract StaticMap getStaticMap();

    @Override
    public abstract DirectionsResult getDirectionsResult();

    @Override
    public abstract long getDistance();

    @Override
    public abstract long getDuration();

    @Override
    public abstract boolean process(RouteProcessor.OrderEntry pivot, RouteProcessor.CourierEntry courierEntry);
*/
    @Override
    public boolean isFinished() {
        if (walkOrders.size() == 0 && driveOrders.size() == 0)
            return true;
        return false;
    }

    @Override
    public String getError() {
        return errorMessage;
    }
}
