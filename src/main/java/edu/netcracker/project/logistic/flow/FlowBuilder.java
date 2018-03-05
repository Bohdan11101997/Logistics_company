package edu.netcracker.project.logistic.flow;

import edu.com.google.maps.StaticMap;
import edu.com.google.maps.model.LatLng;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.Person;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Queue;

public interface FlowBuilder {

    //TODO: assert is second parameter needed
    boolean add(Person courier, CourierType type);

    Person removeCourier(long courier_id);

    Person getCourier(long courier_id);

    boolean add(Order order, OrderType type);

    int add(Order[] orders, OrderType type);

    int add(Collection<Order> orders, OrderType type);

    Order removeOrder(long order_id);

    Order getOrder(long order_id);

    FlowBuilder optimize(boolean value);

    boolean isOptimized();

    List<Order> getOrders();

    Queue<Order> getOrders(OrderType type);

    List<Person> getCouriers();

    Queue<Person> getCouriers(CourierType type);

    void clear();

    //calculatings
    List<Order> calculatePath();

    List<Order> confirmCourier();

    List<Order> getOrdersSequence();

    List<LatLng> getPath();

    StaticMap getStaticMap();

    boolean process();

    String getError();

    /*
    * //TODO: give a better name
    *enum FlowResultType {
    *   PathOnly,
    *    DestinationsOnly,
    *    PathAndDestinations
    *}
    */

    enum CourierType {
        Walker,
        Driver
    }

    enum OrderType {
        Luggage,
        Freight
    }
}
