package edu.netcracker.project.logistic.flow.impl;

import com.google.maps.DirectionsApi;
import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.maps_wrapper.StaticMap;
import com.google.maps.model.*;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.maps_wrapper.GoogleApiRequest;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.RoleService;
//import org.apache.tomcat.util.collections.SynchronizedQueue;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class FlowBuilderImpl implements FlowBuilder {

    protected double maxWalkableDistance = 15 * 1000.0/*m*/;
    protected double maxDrivableDistance = 300 * 1000.0/*m*/;

    public static long clientWaitingTime = 15 * 60/*seconds*/;

    protected boolean optimize = false;

    protected Queue<Order> walkOrders;
    protected Queue<Order> driveOrders;
    protected Queue<Person> walkCouriers;
    protected Queue<Person> driveCouriers;

    protected RoleService roleService;
    protected OrderTypeDao orderTypeDao;

    protected static Map<String, TravelMode> travelModeMap;

    protected Office office;
    protected LatLng center;

    private boolean useMapRequests = true;

    protected long distance;
    protected long duration;

    protected String errorMessage = "No error";

    public FlowBuilderImpl(RoleService roleService, OrderTypeDao orderTypeDao, Office office) {
        this.roleService = roleService;
        this.office = office;
        this.center = office.getAddress().getLocation();

        travelModeMap = new LinkedHashMap<>();
        List<OrderType> orderTypes;
        try {
            orderTypes = new ArrayList<>(orderTypeDao.findAll());
        } catch (NullPointerException e) {
            //TODO: remove this staff before release
            orderTypes = new ArrayList<>(3);
            orderTypes.add(new OrderType((long) 1, "Documents", new BigDecimal(1), (long) 35, (long) 25, (long) 2));
            orderTypes.add(new OrderType((long) 2, "Package", new BigDecimal(30), (long) 150, (long) 150, (long) 150));
            orderTypes.add(new OrderType((long) 3, "Cargo", new BigDecimal(1000), (long) 170, (long) 170, (long) 300));
        }
        for (OrderType ot : orderTypes)
            travelModeMap.put(ot.getName(), ot.getName().equalsIgnoreCase("Cargo") ? TravelMode.DRIVING : TravelMode.WALKING);


        init(this, this.office, useMapRequests);
    }

    @Override
    public Map<String,TravelMode> getTravelModeMap(){
        return travelModeMap;
    }

    protected static Comparator<Order> makeOrderComparator(FlowBuilderImpl impl, LatLng center, boolean useMap, TravelMode travelMode) {
        return (Order o1, Order o2) -> {
            LatLng l1 = o1.getReceiverAddress().getLocation();
            LatLng l2 = o2.getReceiverAddress().getLocation();
            Double dist1;
            Double dist2;
            if (useMap) {
                dist1 = mapDistance(l1, center, travelMode);
                dist2 = mapDistance(l2, center, travelMode);
            } else {
                dist1 = distance(l1, center);
                dist2 = distance(l2, center);
            }
            dist1 = updateOrderDistanceIfVip(impl, o1, dist1);
            dist2 = updateOrderDistanceIfVip(impl, o2, dist2);
            return Double.compare(dist1, dist2);
        };
    }

    protected static void init(FlowBuilderImpl impl, Office office, boolean useMap) {

        impl.walkOrders = new PriorityBlockingQueue<>(11, makeOrderComparator(impl, office.getAddress().getLocation(), useMap, TravelMode.WALKING));
        impl.driveOrders = new PriorityBlockingQueue<>(11, makeOrderComparator(impl, office.getAddress().getLocation(), useMap, TravelMode.DRIVING));

        Comparator<Person> cp = (Person p1, Person p2) -> {
          /*  LatLng l1 = p1.getLocation();
            LatLng l2 = p1.getLocation();
            Double dist1 = (l1.lat*l1.lat+l1.lng+l1.lng);
            Double dist2 = (l2.lat*l2.lat+l2.lng+l2.lng);
            return Double.compare(dist1,dist2);*/
            return 0; //TODO: update after refactoring Person(Courier role) class
        };
        impl.walkCouriers = new PriorityBlockingQueue<>(11, cp);
        impl.driveCouriers = new PriorityBlockingQueue<>(11, cp);

    }

    protected static double distance(LatLng a, LatLng b, boolean useMap, TravelMode travelMode) {
        if (useMap)
            return mapDistance(a, b, travelMode);
        else
            return distance(a, b);
    }

    protected static double distance(LatLng a, LatLng b) {
        double value =  Math.sqrt(
                Math.pow(a.lat - b.lat, 2) +
                Math.pow(a.lng - b.lng, 2));
        return value;
    }

    protected static double mapDistance(LatLng a, LatLng b, TravelMode travelMode) {
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

    protected static double updateOrderDistanceIfVip(FlowBuilderImpl impl, Order o, double distance) {
        boolean isVIP = false;
        for (Role role :
                impl.roleService.findRolesByPersonId(
                        o.getSenderContact().getContactId()
                )) {
            if (role.isEmployeeRole()) {
                isVIP = true;
                break;
            }
            if (role.getPriority().equals("VIP")) {
                isVIP = true;
                break;
            }
        }
        if (isVIP) {
            distance = (Double.MIN_VALUE + distance);
        }
        return distance;
    }

    protected static TravelMode decide(Order o) {
        return travelModeMap.getOrDefault(o.getOrderType(), TravelMode.WALKING);
    }

    @Override
    public boolean add(Person courier, CourierType type) {
        for (Role role : courier.getRoles())
            if (role.getRoleName().equals("ROLE_COURIER")) {
                switch (type) {
                    case Walker:
                        walkCouriers.add(courier);
                        return true;
                    case Driver:
                        driveCouriers.add(courier);
                        return true;
                }
            }
        return false;
    }

    protected Person removeCourierFromQueue(Queue<Person> queue, long courier_id) {
        Person p = getCourierFromQueue(queue, courier_id);
        queue.remove(p);
        return p;
    }

    protected Person getCourierFromQueue(Queue<Person> queue, long courier_id) {
        for (Person p : queue) {
            if (p.getId() == courier_id)
                return p;
        }
        return null;
    }

    protected Order removeOrderFromQueue(Queue<Order> queue, long courier_id) {
        Order o = getOrderFromQueue(queue, courier_id);
        queue.remove(o);
        return o;
    }

    protected Order getOrderFromQueue(Queue<Order> queue, long courier_id) {
        for (Order o : queue) {
            if (o.getId() == courier_id)
                return o;
        }
        return null;
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

    @Override
    public Person removeCourier(long courier_id) {
        Person p = removeCourierFromQueue(walkCouriers, courier_id);
        if (p != null)
            return p;
        p = removeCourierFromQueue(driveCouriers, courier_id);
        return p;
    }

    @Override
    public Person getCourier(long courier_id) {
        Person p = getCourierFromQueue(walkCouriers, courier_id);
        if (p != null)
            return p;
        p = getCourierFromQueue(driveCouriers, courier_id);
        return p;
    }

    public static double metersToEarthDegrees(double meters) {
        return meters * 6.0 / 1000.0;
    }

    // false if adding failed
    @Override
    public boolean add(Order order) {
        if (order == null)
            return false;
        if (order.getOrderType() == null)
            return false;
        if (order.getOrderStatus() == null)
            return false;
        //TODO: check if this can be moved out
        if (!order.getOrderStatus().getName().equalsIgnoreCase("CONFIRMED"))
            return false;

        switch (decide(order)) {
            case WALKING:
            case BICYCLING:
            case TRANSIT:
                if (distance(order.getReceiverAddress().getLocation(), office.getAddress().getLocation()) < metersToEarthDegrees(maxWalkableDistance) / 8) {
                    if (!(order.getSenderAddress() == null ||
                            distance(order.getSenderAddress().getLocation(),
                                    office.getAddress().getLocation()) > metersToEarthDegrees(maxWalkableDistance) / 8))
                        return walkOrders.add(order);
                }
                System.err.println("Order moves from Walk to Driver orders.");
            case DRIVING:
                return driveOrders.add(order);
        }
        return false;
    }

    @Override
    public int add(Order[] orders) {
        int counter = 0;
        for (Order o : orders)
            if (!add(o))
                return counter;
            else counter++;
        return counter;
    }

    @Override
    public int add(Collection<Order> orders) {
        int counter = 0;
        for (Order o : orders)
            if (!add(o))
                return counter;
            else counter++;

        return counter;
    }

    @Override
    public Order removeOrder(long order_id) {
        Order o = removeOrderFromQueue(walkOrders, order_id);
        if (o != null)
            return o;
        o = removeOrderFromQueue(driveOrders, order_id);
        return o;
    }

    @Override
    public Order getOrder(long order_id) {
        Order o = getOrderFromQueue(walkOrders, order_id);
        if (o != null)
            return o;
        o = getOrderFromQueue(driveOrders, order_id);
        return o;
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
    public FlowBuilder setUseMapRequests(boolean useMapRequests) {
        this.useMapRequests = useMapRequests;
        List<Order> value = getOrders();
        Queue<Person> courierDrive = getCouriers(CourierType.Driver);
        Queue<Person> courierWalk = getCouriers(CourierType.Walker);
        init(this, this.office, useMapRequests);
        add(value);
        for (Person p : courierDrive)
            add(p, CourierType.Driver);
        for (Person p : courierWalk)
            add(p, CourierType.Walker);
        return this;
    }

    @Override
    public abstract List<Order> getUnused();

    @Override
    public List<Order> getOrders() {
        List<Order> return_value = new ArrayList<>();
        return_value.addAll(walkOrders);
        return_value.addAll(driveOrders);
        return return_value;
    }

    @Override
    public List<Order> getOrders(OrderType type) {
        List<Order> return_value = new ArrayList<>();

        for (Order o : walkOrders)
            if (o.getOrderType().equals(type))
                return_value.add(o);

        for (Order o : driveOrders)
            if (o.getOrderType().equals(type))
                return_value.add(o);
        if (return_value.size() == 0)
            return null;
        else return return_value;
    }

    @Override
    public List<Person> getCouriers() {
        List<Person> return_value = new ArrayList<>();
        return_value.addAll(walkCouriers);
        return_value.addAll(driveCouriers);
        return return_value;
    }

    @Override
    public Queue<Person> getCouriers(CourierType type) {
        switch (type) {
            case Driver:
                return driveCouriers;
            case Walker:
                return walkCouriers;
        }
        return null;
    }

    @Override
    public void clear() {
        walkOrders.clear();
        driveOrders.clear();
        walkCouriers.clear();
        driveCouriers.clear();
        optimize = false;
        center = office.getAddress().getLocation();
    }

    @Override
    public abstract List<Order> calculatePath();

    @Override
    public abstract List<Order> confirmCourier();

    @Override
    public abstract List<Order> getOrdersSequence();

    @Override
    public abstract List<LatLng> getPath();

    @Override
    public abstract StaticMap getStaticMap();

    @Override
    public abstract long getDistance();

    @Override
    public abstract long getDuration();

    @Override
    public abstract boolean process();

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
