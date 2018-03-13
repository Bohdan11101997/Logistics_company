package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.*;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.processing.TaskProcessorCourier;
import edu.netcracker.project.logistic.service.OrderService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class OrderServiceImpl implements OrderService {
    private AddressDao addressDao;
    private ContactDao contactDao;
    private OrderDao orderDao;
    private OrderStatusDao orderStatusDao;
    private TaskDao taskDao;
    private TaskProcessor taskProcessor;
    private TaskProcessorCourier taskProcessorCourier;

    public OrderServiceImpl(AddressDao addressDao, ContactDao contactDao, OrderDao orderDao,
                            OrderStatusDao orderStatusDao, TaskDao taskDao, TaskProcessor taskProcessor,
                            TaskProcessorCourier taskProcessorCourier) {
        this.addressDao = addressDao;
        this.contactDao = contactDao;
        this.orderDao = orderDao;
        this.orderStatusDao = orderStatusDao;
        this.taskDao = taskDao;
        this.taskProcessor = taskProcessor;
        this.taskProcessorCourier = taskProcessorCourier;
    }

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
    }

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
        order.setOrderStatus(orderStatusDao.findByName("DRAFT")
                .orElseThrow(
                        () -> new IllegalStateException("Can't find order status 'CONFIRMED'")
                ));
        orderDao.save(order);
        task.setCompleted(true);
        taskDao.save(task);
        taskProcessorCourier.createTask(order);
    }

    @Override
    public List<Order> HistoryCompleteOrderReceiver(Long aLong) {
        return orderDao.HistoryCompleteOrderReceiver(aLong);
    }

    @Override
    public List<Order> HistoryCompleteOrderSender(Long aLong) {
        return orderDao.HistoryCompleteOrderSender(aLong);
    }

    @Override
    public void draft(Order order) {
        LocalDateTime now = LocalDateTime.now();
        order.setCreationTime(now);
        order.setOrderStatusTime(now);
        OrderStatus draft = orderStatusDao.findByName("DRAFT")
                .orElseThrow(() -> new IllegalStateException("Can't find order status 'PROCESSING'"));
        order.setOrderStatus(draft);
        orderDao.save(order);
    }
}
