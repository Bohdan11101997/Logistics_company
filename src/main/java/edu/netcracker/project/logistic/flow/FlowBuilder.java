package edu.netcracker.project.logistic.flow;

import edu.netcracker.project.logistic.maps_wrapper.StaticMap;
import com.google.maps.model.LatLng;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.OrderType;
import edu.netcracker.project.logistic.model.Person;

import java.util.Collection;
import java.util.List;
import java.util.Queue;

public interface FlowBuilder {

    double getMaxWalkableDistance();

    void setMaxWalkableDistance(double maxWalkableDistance);

    double getMaxDrivableDistance();

    void setMaxDrivableDistance(double maxDrivableDistance);

    //TODO: assert is second parameter needed
    boolean add(Person courier, CourierType type);

    Person removeCourier(long courier_id);

    Person getCourier(long courier_id);

    boolean add(Order order);

    int add(Order[] orders);

    int add(Collection<Order> orders);

    Order removeOrder(long order_id);

    Order getOrder(long order_id);

    FlowBuilder optimize(boolean value);

    boolean isOptimized();

    List<Order> getUnused();

    List<Order> getOrders();

    List<Order> getOrders(OrderType type);

    List<Person> getCouriers();

    Queue<Person> getCouriers(CourierType type);

    void clear();

    //calculatings
    List<Order> calculatePath();

    List<Order> confirmCourier();

    List<Order> getOrdersSequence();

    List<LatLng> getPath();

    StaticMap getStaticMap();

    //in meters
    long getDistance();

    //in seconds
    long getDuration();

    boolean process();

    String getError();

    boolean isFinished();

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
}
