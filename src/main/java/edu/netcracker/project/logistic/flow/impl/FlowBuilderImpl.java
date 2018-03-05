package edu.netcracker.project.logistic.flow.impl;

import edu.com.google.maps.StaticMap;
import edu.com.google.maps.model.LatLng;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.apache.tomcat.util.collections.SynchronizedQueue;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class FlowBuilderImpl implements FlowBuilder {

    protected boolean optimize = false;

    protected Queue<Order> walkOrders;
    protected Queue<Order> driveOrders;
    protected Queue<Person> walkCouriers;
    protected Queue<Person> driveCouriers;

    protected RoleCrudDao roleCrudDao;

    protected Office office;
    protected LatLng center;

    public FlowBuilderImpl(RoleCrudDao roleCrudDao, Office office) {
        this.roleCrudDao = roleCrudDao;
        this.office = office;
        center = office.getAddress().getLocation();

        init(this, this.office);
    }

    private static void init(FlowBuilderImpl impl, Office office) {
        Comparator<Order> co = (Order o1, Order o2) -> {
            LatLng l1 = o1.getReceiver().getAddress().getLocation();
            LatLng l2 = o2.getReceiver().getAddress().getLocation();
            Double dist1 = distance(l1, office.getAddress().getLocation());
            Double dist2 = distance(l2, office.getAddress().getLocation());
            dist1 = updateOrderDistanceIfVip(impl, o1, dist1);
            dist2 = updateOrderDistanceIfVip(impl, o2, dist2);
            return Double.compare(dist1, dist2);
        };
        impl.walkOrders = new PriorityBlockingQueue<>(11, co);
        impl.driveOrders = new PriorityBlockingQueue<>(11, co);

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

    protected static double distance(LatLng a, LatLng b){
        return Math.sqrt(Math.pow(a.lat-b.lat,2)
                + Math.pow(a.lng-b.lng,2));
    }

    protected static double updateOrderDistanceIfVip(FlowBuilderImpl impl,Order o, double distance){
        boolean isVIP = false;
        for(Role role :
                impl.roleCrudDao.getByPersonId(
                        o.getSender().getContact().getContactId()
                )){
            if(role.isEmployeeRole()){
                isVIP = true; break; }
            if(role.getRoleName().equalsIgnoreCase("ROLE_VIP_USER")){
                isVIP = true; break; }
        }
        if(isVIP){ distance = (Double.MIN_VALUE+distance); }
        return distance;
    }

    protected static OrderType decide(Order o) {
        //TODO: back after updating other classes
        return OrderType.Luggage;
    }

    @Override
    public boolean add(Person courier, CourierType type) {
        //checking if is not a courier
        if (courier.getRoles().contains(roleCrudDao.getByName("ROLE_COURIER")))
            switch (type) {
                case Walker:
                    walkCouriers.add(courier);
                    break;
                case Driver:
                    driveCouriers.add(courier);
                    break;
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

    // false if adding failed
    @Override
    public boolean add(Order order, OrderType type) {
        switch (type) {
            case Freight:
                return driveOrders.add(order);
            case Luggage:
                return walkOrders.add(order);
        }
        return false;
    }

    @Override
    public int add(Order[] orders, OrderType type) {
        int counter = 0;
        for (Order o : orders)
            if (!add(o, type))
                return counter;
            else counter++;

        return counter;
    }

    //TODO: remove duplicating code (without using toArray())
    @Override
    public int add(Collection<Order> orders, OrderType type) {
        int counter = 0;
        for (Order o : orders)
            if (!add(o, type))
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
    public List<Order> getOrders() {
        List<Order> return_value = new ArrayList<>();
        return_value.addAll(walkOrders);
        return_value.addAll(driveOrders);
        return return_value;
    }

    @Override
    public Queue<Order> getOrders(OrderType type) {
        switch (type) {
            case Freight:
                return driveOrders;
            case Luggage:
                return walkOrders;
        }
        return null;
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
    public abstract boolean process();
}
