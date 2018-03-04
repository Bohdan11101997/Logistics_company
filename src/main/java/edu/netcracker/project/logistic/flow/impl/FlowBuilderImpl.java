package edu.netcracker.project.logistic.flow.impl;

import edu.com.google.maps.model.LatLng;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import org.apache.tomcat.util.collections.SynchronizedQueue;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class FlowBuilderImpl implements FlowBuilder {

    private boolean optimize = false;

    private Queue<Order> walkOrders;
    private Queue<Order> driveOrders;
    private Queue<Person> walkCouriers;
    private Queue<Person> driveCouriers;

    public FlowBuilderImpl() {
        Comparator<Order> co = (Order o1, Order o2) -> {
            LatLng l1 = o1.getReceiver().getAddress().getLocation();
            LatLng l2 = o2.getReceiver().getAddress().getLocation();
            Double dist1 = (l1.lat * l1.lat + l1.lng + l1.lng);
            Double dist2 = (l2.lat * l2.lat + l2.lng + l2.lng);
            //TODO: add updating via order type (VIP|CASUAL)
            return Double.compare(dist1, dist2);
        };
        walkOrders = new PriorityBlockingQueue<>(11, co);
        driveOrders = new PriorityBlockingQueue<>(11, co);

        Comparator<Person> cp = (Person p1, Person p2) -> {
          /*  LatLng l1 = p1.getLocation();
            LatLng l2 = p1.getLocation();
            Double dist1 = (l1.lat*l1.lat+l1.lng+l1.lng);
            Double dist2 = (l2.lat*l2.lat+l2.lng+l2.lng);
            return Double.compare(dist1,dist2);*/
            return 0; //TODO: update after refactoring Person(Courier role) class
        };
        walkCouriers = new PriorityBlockingQueue<>(11, cp);
        driveCouriers = new PriorityBlockingQueue<>(11, cp);
    }

    private static OrderType decide(Order o) {
        //TODO: back after updating other classes
        return OrderType.Luggage;
    }

    @Override
    public boolean add(Person courier, CourierType type) {
        if (courier.getRoles().contains(new Role((long) 5, "ROLE_COURIER")))
            //checking if is not courier
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

    private Person removeCourierFromQueue(Queue<Person> queue, long courier_id) {
        Person p = getCourierFromQueue(queue, courier_id);
        queue.remove(p);
        return p;
    }

    private Person getCourierFromQueue(Queue<Person> queue, long courier_id) {
        for (Person p : queue) {
            if (p.getId() == courier_id)
                return p;
        }
        return null;
    }

    private Order removeOrderFromQueue(Queue<Order> queue, long courier_id) {
        Order o = getOrderFromQueue(queue, courier_id);
        queue.remove(o);
        return o;
    }

    private Order getOrderFromQueue(Queue<Order> queue, long courier_id) {
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

    //TODO: all functions
    // false if adding failed
    @Override
    public boolean add(Order order, OrderType type) {
        switch (type) {
            case VIPFreight:
                break;
            case Freight:
                break;
            case VIPLuggage:
                break;
            case Luggage:
                break;
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
            case VIPFreight:
                return driveOrders;
            case Luggage:
            case VIPLuggage:
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

    public void clear() {
        walkOrders.clear();
        driveOrders.clear();
        walkCouriers.clear();
        driveCouriers.clear();
        optimize = false;
    }
    //forget some functions
}
