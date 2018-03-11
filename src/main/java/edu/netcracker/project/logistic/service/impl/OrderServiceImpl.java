package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.dao.AddressDao;
import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.OrderStatusDao;
import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.OrderStatus;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.service.OrderService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class OrderServiceImpl implements OrderService {
    private AddressDao addressDao;
    private ContactDao contactDao;
    private OrderDao orderDao;
    private OrderStatusDao orderStatusDao;
    private TaskProcessor taskProcessor;

    public OrderServiceImpl(AddressDao addressDao, ContactDao contactDao, OrderDao orderDao,
                            OrderStatusDao orderStatusDao, TaskProcessor taskProcessor) {
        this.addressDao = addressDao;
        this.contactDao = contactDao;
        this.orderDao = orderDao;
        this.orderStatusDao = orderStatusDao;
        this.taskProcessor = taskProcessor;
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
