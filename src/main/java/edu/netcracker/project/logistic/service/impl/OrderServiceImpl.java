package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.*;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.processing.RouteProcessor;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OrderServiceImpl implements OrderService {
    private final static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private AddressDao addressDao;
    private ContactDao contactDao;
    private OrderDao orderDao;
    private OrderStatusDao orderStatusDao;
    private OrderDraftDao orderDraftDao;
    private TaskDao taskDao;
    private TaskProcessor taskProcessor;
    private RouteProcessor routeProcessor;
    private CourierDataDao courierDataDao;
    private PersonCrudDao personDao;

    public OrderServiceImpl(AddressDao addressDao, ContactDao contactDao, OrderDao orderDao,
                            OrderStatusDao orderStatusDao, OrderDraftDao orderDraftDao,
                            TaskDao taskDao, TaskProcessor taskProcessor,
                            RouteProcessor routeProcessor, CourierDataDao courierDataDao, PersonCrudDao personDao) {
        this.addressDao = addressDao;
        this.contactDao = contactDao;
        this.orderDao = orderDao;
        this.orderStatusDao = orderStatusDao;
        this.orderDraftDao = orderDraftDao;
        this.taskDao = taskDao;
        this.taskProcessor = taskProcessor;
        this.routeProcessor = routeProcessor;
        this.courierDataDao = courierDataDao;
        this.personDao = personDao;
    }

    @Transactional
    @Override
    public void createOrder(Order order) {
        Address senderAddress = order.getSenderAddress();
        Address receiverAddress = order.getReceiverAddress();
        addressDao.save(senderAddress);
        addressDao.save(receiverAddress);
        contactDao.save(order.getReceiverContact());
        if (order.getWeight() == null) {
            order.setWeight(new BigDecimal(1));
        }
        LocalDateTime now = LocalDateTime.now();
        order.setCreationTime(now);
        order.setOrderStatusTime(now);
        OrderStatus processing = orderStatusDao.findByName("PROCESSING")
                .orElseThrow(() -> new IllegalStateException("Can't find order status 'PROCESSING'"));
        order.setOrderStatus(processing);
        orderDao.save(order);
        taskProcessor.createTask(order);
    }

    @Transactional
    @Override
    public void createFromDraft(OrderDraft orderDraft, Order order) {
        Order oldDraft = orderDraft.getDraft();
        order.setOrderStatusTime(oldDraft.getOrderStatusTime());
        order.setCreationTime(oldDraft.getCreationTime());
        Address senderAddress = order.getSenderAddress();
        Address receiverAddress = order.getReceiverAddress();
        addressDao.save(senderAddress);
        addressDao.save(receiverAddress);
        contactDao.save(order.getReceiverContact());
        if (order.getWeight() == null) {
            order.setWeight(new BigDecimal(1));
        }
        LocalDateTime now = LocalDateTime.now();
        order.setOrderStatusTime(now);
        OrderStatus processing = orderStatusDao.findByName("PROCESSING")
                .orElseThrow(() -> new IllegalStateException("Can't find order status 'PROCESSING'"));
        order.setOrderStatus(processing);
        order.setId(null);
        orderDao.save(order);
        orderDraftDao.delete(orderDraft.getId());
        taskProcessor.createTask(order);
    }

    @Transactional
    @Override
    public void confirmOrder(Long employeeId, Long orderId) {
        Optional<Order> opt = orderDao.findOne(orderId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Employee not found");
        }
        Order order = opt.get();
        Task task = taskDao.findByOrderId(order.getId()).orElseThrow(
                () -> new IllegalStateException(
                        String.format("Call centre agent processed order #%d but task not exists", order.getId()))
        );
        if (!employeeId.equals(task.getEmployeeId())) {
            throw new IllegalArgumentException("Can't confirm not assigned order");
        }
        order.setOrderStatus(orderStatusDao.findByName("CONFIRMED")
                .orElseThrow(
                        () -> new IllegalStateException("Can't find order status 'CONFIRMED'")
                ));
        orderDao.save(order);
        task.setCompleted(true);
        taskDao.save(task);
        routeProcessor.addOrder(order);
    }

    @Transactional
    @Override
    public void rejectOrder(Long employeeId, Long orderId) {
        Optional<Order> opt = orderDao.findOne(orderId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Employee not found");
        }
        Order order = opt.get();
        Task task = taskDao.findByOrderId(order.getId()).orElseThrow(
                () -> new IllegalStateException(
                        String.format("Call centre agent processed order #%d but task not exists", order.getId()))
        );
        if (!employeeId.equals(task.getEmployeeId())) {
            throw new IllegalArgumentException("Can't reject not assigned order");
        }
        orderDao.delete(order.getId());

        Person person = personDao.findByContactId(order.getSenderContact().getContactId())
                .orElseThrow(() -> new IllegalStateException("Sender contact doesn't have account"));
        OrderDraft orderDraft = new OrderDraft();
        orderDraft.setDraft(order);
        orderDraft.setPersonId(person.getId());

        orderDraftDao.save(orderDraft);
        taskDao.delete(task.getId());
    }

    private void updateRoutePoint(CourierData data, Long orderId, DeliveryStatus status) {
        Long employeeId = data.getCourier().getId();
        Route route = data.getRoute();
        List<RoutePoint> points = route.getWayPoints();
        RoutePoint point = points
                .stream()
                .filter(p -> p.getOrder().getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error(
                            "Attempt to change delivery status for order #{} which is not in current route for courier #{}",
                            orderId,
                            employeeId
                    );
                    return new IllegalArgumentException("Route point for order not exists");
                });
        if (!point.getStatus().equals(DeliveryStatus.DELIVERING)) {
            throw new IllegalArgumentException("Can't change state for order which is not in DELIVERING state");
        }
        point.setStatus(status);
        if (points.stream().allMatch(p -> p.getStatus() != DeliveryStatus.DELIVERING)) {
            data.setCourierStatus(CourierStatus.FREE);
            Route emptyRoute = new Route();
            emptyRoute.setWayPoints(Collections.emptyList());
            data.setRoute(emptyRoute);
        } else {
            data.setRoute(route);
        }
        data.setLastLocation(String.format("%s,%s", point.getLatitude(), point.getLongitude()));
        courierDataDao.save(data);
    }

    @Transactional
    @Override
    public void confirmDelivered(CourierData data, Long orderId) {
        Long employeeId = data.getCourier().getId();
        Optional<Order> opt = orderDao.findOne(orderId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Employee not found");
        }
        Order order = opt.get();
        if (!employeeId.equals(order.getCourier().getId())) {
            throw new IllegalArgumentException("Can't fail not assigned order");
        }
        updateRoutePoint(data, orderId, DeliveryStatus.DELIVERED);
        order.setOrderStatus(orderStatusDao.findByName("DELIVERED")
                .orElseThrow(
                        () -> new IllegalStateException("Can't find order status 'DELIVERED'")
                ));
        orderDao.save(order);
        if(data.getCourierStatus().equals(CourierStatus.FREE)) {
            routeProcessor.addCourier(employeeId);
        }
    }

    @Transactional
    @Override
    public void confirmFailed(CourierData data, Long orderId) {
        Long employeeId = data.getCourier().getId();
        Optional<Order> opt = orderDao.findOne(orderId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Employee not found");
        }
        Order order = opt.get();
        if (!employeeId.equals(order.getCourier().getId())) {
            throw new IllegalArgumentException("Can't fail not assigned order");
        }
        updateRoutePoint(data, orderId, DeliveryStatus.CANCELLED);
        order.setOrderStatus(orderStatusDao.findByName("PROCESSING")
                .orElseThrow(
                        () -> new IllegalStateException("Can't find order status 'PROCESSING'")
                ));
        orderDao.save(order);
        taskProcessor.createTask(order);
        if(data.getCourierStatus().equals(CourierStatus.FREE)) {
            routeProcessor.addCourier(employeeId);
        }
    }

    @Override
    public void draft(OrderDraft orderDraft) {
        Optional<OrderDraft> opt = orderDraftDao.findOne(orderDraft.getId());
        if (opt.isPresent()) {
            OrderDraft record = opt.get();
            orderDraft.getDraft().setCreationTime(record.getDraft().getCreationTime());
            record.setDraft(orderDraft.getDraft());
            record.getDraft().setOrderStatusTime(LocalDateTime.now());
            orderDraftDao.save(record);
        } else {
            LocalDateTime now = LocalDateTime.now();
            Order order = orderDraft.getDraft();
            order.setCreationTime(now);
            order.setOrderStatusTime(now);
            orderDraftDao.save(orderDraft);
        }
    }
}