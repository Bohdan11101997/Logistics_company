package edu.netcracker.project.logistic.processing;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.*;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.flow.impl.RadiusSelector;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

@Scope("singleton")
@Component
public class RouteProcessor {

    public static class OrderEntry {
        private String priority;
        private Order order;
        private TravelMode travelMode;
        private boolean orderFromClient;

        private OrderEntry(Order order, String priority, boolean orderFromClient) {
            this.order = order;
            this.priority = priority;
            this.travelMode = TravelMode.DRIVING;
            this.orderFromClient = orderFromClient;
            try {
                if (!order.getOrderType().getName().equalsIgnoreCase("Cargo")) {
                    this.travelMode = TravelMode.WALKING;
                }
            } catch (Exception e) {
            }
        }

        public OrderEntry(OrderEntry other) {
            this.order = new Order(other.order);
            this.priority = other.priority;
            this.travelMode = other.travelMode;
            this.orderFromClient = other.orderFromClient;
        }

        public String getPriority() {
            return priority;
        }

        public Order getOrder() {
            return order;
        }

        public TravelMode getTravelMode() {
            return travelMode;
        }

        public boolean isOrderFromClient() {
            return orderFromClient;
        }

        public void setOrderFromClient(boolean value) {
            orderFromClient = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RouteProcessor.OrderEntry orderEntry = (RouteProcessor.OrderEntry) o;
            return Objects.equals(order, orderEntry.order);
        }

        @Override
        public int hashCode() {
            return Objects.hash(order);
        }
    }

    public static class CourierEntry {
        private LocalDate workDayDate;
        private WorkDay workDay;
        private Long employeeId;
        private CourierData courierData;
        private LatLng lastLocation;

        private CourierEntry(LocalDate workDayDate, WorkDay workDay, CourierData courierData, Long employeeId) {
            this.workDayDate = workDayDate;
            this.workDay = workDay;
            this.employeeId = employeeId;
            this.courierData = courierData;
        }

        private CourierEntry(Long employeeId) {
            this.employeeId = employeeId;
        }

        public LatLng getLastLocation() {
            if (lastLocation == null) {
                String location[] = courierData.getLastLocation().split("[,]");
                String latitude = location[0];
                String longitude = location[1];
                lastLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            }
            return lastLocation;
        }

        public Long getEmployeeId() {
            return employeeId;
        }

        public CourierData getCourierData() {
            return courierData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RouteProcessor.CourierEntry that = (RouteProcessor.CourierEntry) o;
            return Objects.equals(employeeId, that.employeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId);
        }
    }

    private static final Duration WORK_DAY_END_NEARING_INTERVAL = Duration.ofMinutes(10);

    private static final Logger logger = LoggerFactory.getLogger(RouteProcessor.class);

    private BlockingQueue<RouteProcessor.CourierEntry> walkWorkerQueue;
    private BlockingQueue<RouteProcessor.CourierEntry> driveWorkerQueue;
    private BlockingQueue<RouteProcessor.OrderEntry> walkOrdersQueue;
    private BlockingQueue<RouteProcessor.OrderEntry> driveOrdersQueue;

    private OrderDao orderDao;
    private OrderStatusDao orderStatusDao;
    private OfficeDao officeDao;
    private PersonCrudDao personDao;
    private PersonRoleDao personRoleDao;
    private WorkDayDao workDayDao;
    private CourierDataDao courierDataDao;
    private NotificationService notificationService;
    private FlowBuilder flowBuilder;

    public RouteProcessor(OrderDao orderDao,
                          PersonCrudDao personDao,
                          OrderStatusDao orderStatusDao,
                          OfficeDao officeDao,
                          PersonRoleDao personRoleDao,
                          WorkDayDao workDayDao,
                          CourierDataDao courierDataDao,
                          NotificationService notificationService) {
        this.orderDao = orderDao;
        this.orderStatusDao = orderStatusDao;
        this.officeDao = officeDao;
        this.personDao = personDao;
        this.personRoleDao = personRoleDao;
        this.workDayDao = workDayDao;
        this.courierDataDao = courierDataDao;
        this.notificationService = notificationService;
    }

    private void prepareQueues(LatLng center) {
        this.walkOrdersQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeOrderComparator(center, false, TravelMode.WALKING)
        );
        this.driveOrdersQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeOrderComparator(center, false, TravelMode.DRIVING)
        );

        this.walkWorkerQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeCourierComparator(center, false, TravelMode.WALKING)
        );
        this.driveWorkerQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeCourierComparator(center, false, TravelMode.DRIVING)
        );

        List<Order> confirmedOrders = orderDao.findConfirmed();
        for (Order data : confirmedOrders) {
            createOrder(data);
        }
    }

    @Transactional
    public boolean assignOrders(List<RouteProcessor.OrderEntry> orderEntries, RouteProcessor.CourierEntry employeeEntry) throws InterruptedException {
        for (RouteProcessor.OrderEntry oe : orderEntries) {
            if (!assignOrder(oe, employeeEntry))
                return false; //early out
        }
        return true;
    }

    @Transactional
    public boolean assignOrder(RouteProcessor.OrderEntry orderEntry, RouteProcessor.CourierEntry courierEntry) throws InterruptedException {
        Order order = orderEntry.order;
        Long employeeId = courierEntry.employeeId;

        // Check if employee is still courier
        Optional<Person> employeeRecord = personDao.findOne(employeeId);
        if (!employeeRecord.isPresent()) {
            logger.warn("Employee instance not found.");
            addOrder(orderEntry);
            return false;
        }
        Person employee = employeeRecord.get();
        boolean isCourier = false;
        for (Role r : employee.getRoles()) {
            if (r.getRoleName().equals("ROLE_COURIER")) {
                isCourier = true;
                break;
            }
        }
        if (!isCourier) {
            logger.warn("Person instance is not courier");
            addOrder(orderEntry);
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate todayDate = now.toLocalDate();
        // Check if stored schedule is still actual
        if (!todayDate.equals(courierEntry.workDayDate)) {
            Optional<WorkDay> opt = workDayDao.findScheduleForDate(todayDate, employeeId);
            if (!opt.isPresent()) {
                logger.error("Employee is not working on this day ({})", todayDate);
                addOrder(orderEntry);
                return false;
            }
            courierEntry.workDay = opt.get();
            courierEntry.workDayDate = todayDate;
        }
        // Check if work day is nearing end
        if (now.toLocalTime().isBefore(courierEntry.workDay.getStartTime()) ||
                now.toLocalTime().plus(WORK_DAY_END_NEARING_INTERVAL)
                        .isAfter(courierEntry.workDay.getEndTime())) {
            addOrder(orderEntry);
            return false;
        }

        order.setCourier(employee);
        order.setOrderStatusTime(LocalDateTime.now());
        Optional<OrderStatus> optOrderStatus = orderStatusDao.findByName("DELIVERING");
        if (!optOrderStatus.isPresent()) {
            logger.error("Order status not found in DB ({})", "DELIVERING");
            order.setOrderStatus(new OrderStatus((long) -1, "DELIVERING"));
        } else {
            order.setOrderStatus(optOrderStatus.get());
        }
        logger.info("Assigned order #{} to courier #{}", order.getId(), employeeId);

        Optional<CourierData> opt = courierDataDao.findOne(employee.getUserName());
        if (!opt.isPresent()) {
            logger.error("Can't find data for courier #{}", employee.getId());
            addOrder(orderEntry);
            addCourier(employeeId);
            return false;
        }
        courierEntry.courierData = opt.get();
        Route route = courierEntry.courierData.getRoute();
        if (route == null) {
            courierEntry.courierData.setRoute(new Route(null, new ArrayList<>()));
        } else if (route.getWayPoints() == null) {
            route.setWayPoints(new ArrayList<>());
        }
        List<RoutePoint> routePoints = courierEntry.courierData.getRoute().getWayPoints();
        LatLng point = orderEntry.getOrder().getReceiverAddress().getLocation();
        routePoints.add(new RoutePoint(
                String.format("%.8f", point.lat),
                String.format("%.8f", point.lng),
                DeliveryStatus.DELIVERING,
                OrderDTO.valueOf(order)));
        courierDataDao.save(courierEntry.courierData);
        if (!orderEntry.isOrderFromClient()) {
            //there are no office-sender order in chain office-sender-client
            orderDao.save(order);
        }
        return true;
    }

    public Office getRandomOffice() {
        List<Office> offices = officeDao.allOffices();
        if (offices == null || offices.size() == 0) {
            logger.error("There are no offices created");
            return null;
        }
        return offices.get((int) ((Math.random() * (offices.size() - 1))));
    }

    @Async
    public void taskLoop() throws InterruptedException {
        Office office;
        try {
            office = getRandomOffice();
            if (office == null) {
                logger.error("There are no offices created");
                prepareQueues(new LatLng(0, 0));
            } else {//use first founded office
                prepareQueues(office.getAddress().getLocation());
            }

            flowBuilder = new RadiusSelector(walkOrdersQueue, driveOrdersQueue, walkWorkerQueue, driveWorkerQueue, office);
            flowBuilder.setUseMapRequests(true);
            while (true) {
                RouteProcessor.CourierEntry worker = null;
                RouteProcessor.OrderEntry order = null;
                boolean driverWorker = true;
                try {
                    while (worker == null) {
                        worker = driveWorkerQueue.poll(15, TimeUnit.SECONDS);
                        if (worker == null) {
                            driverWorker = false;
                            worker = walkWorkerQueue.poll(15, TimeUnit.SECONDS);
                        }
                    }
                } catch (InterruptedException ex) {
                    logger.error("Interrupted while waiting for courier");
                    continue;
                }

                if (worker.courierData.getCourierStatus().equals(CourierStatus.ON_WAY)) {
                    logger.error("Courier #{} is already delivering orders", worker.getEmployeeId());
                    continue;
                }

                try {
                    if (!driverWorker) {
                        order = walkOrdersQueue.poll(15, TimeUnit.SECONDS);
                        if(order == null)
                            continue;
                    } else {
                        while (order == null) {
                            order = driveOrdersQueue.poll(15, TimeUnit.SECONDS);
                            if (order == null) {
                                order = walkOrdersQueue.poll(15, TimeUnit.SECONDS);
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    logger.error("Interrupted while waiting for order");
                    walkWorkerQueue.put(worker);
                    continue;
                }

                logger.info("Begin - new route building for ({})", worker);

                office = order.getOrder().getOffice() == null ? getRandomOffice() : order.getOrder().getOffice();
                if (office == null) {
                    logger.error("There are no offices created");
                } else {
                    flowBuilder.setOffice(office);
                }



                if (!flowBuilder.process(order, worker)) {
                    logger.error(flowBuilder.getError());
                    //manual rollback successfully picked orders

                    for (OrderEntry orderEntry : flowBuilder.getOrdersSequence()) {
                        if (!orderEntry.isOrderFromClient()) {//do not multiply fake orders
                            addOrder(orderEntry);
                        }
                    }
                    if(driverWorker){
                        driveWorkerQueue.add(worker);
                    } else {
                        walkWorkerQueue.add(worker);
                    }
                } else if (assignOrders(flowBuilder.getOrdersSequence(), worker)) { //if process success
                    worker.courierData.getRoute().setMapUrl(flowBuilder.getStaticMap().toString());
                    worker.courierData.setCourierStatus(CourierStatus.ON_WAY);
                    courierDataDao.save(worker.courierData);
                    notificationService.send(personDao.findOne(worker.employeeId).get().getUserName(),
                            new Notification("route", "Delivery route assigned."));
                    logger.info("Courier #{} get #{} orders", worker.employeeId, flowBuilder.getOrdersSequence().size());
                }

                logger.info("End - route building for ({})", worker);
            }
        } catch (InterruptedException ex) {
            logger.error("Route processor terminated");
            throw ex;
        }
    }

    public void addOrder(Long orderId) throws InterruptedException {
        Optional<Order> order = orderDao.findOne(orderId);
        if (order.isPresent()) {
            addOrder(order.get());
        }
    }

    public void addOrder(Order order) {
        boolean vip = false;

        Optional<Person> sender = personDao.findByContactId(order.getSenderContact().getContactId());
        if (sender.isPresent()) {
            for (Role r : sender.get().getRoles()) {
                if (r.getPriority().equals("VIP")) {
                    vip = true;
                    break;
                }
            }
        }

        Optional<Person> reciever = personDao.findOne(order.getReceiverContact().getContactId());
        if (reciever.isPresent()) {
            for (Role r : reciever.get().getRoles()) {
                if (r.getPriority().equals("VIP")) {
                    vip = true;
                    break;
                }
            }
        }

        OrderEntry orderEntry = new RouteProcessor.OrderEntry(order, vip ? "VIP" : "NORMAL", false);

        try {
            switch (orderEntry.travelMode) { //put back
                case WALKING:
                    walkOrdersQueue.put(orderEntry);
                    break;
                default:
                    driveOrdersQueue.put(orderEntry);
                    break;
            }
        } catch (InterruptedException ex) {
            logger.error("Interrupted on adding order to queue");
        }
    }

    public void addOrder(OrderEntry orderEntry) throws InterruptedException {
        switch (orderEntry.travelMode) { //put back
            case WALKING:
                walkOrdersQueue.put(orderEntry);
                break;
            default:
                driveOrdersQueue.put(orderEntry);
                break;
        }
    }

    public void addCourier(Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate workDayDate = now.toLocalDate();

        Optional<WorkDay> opt = workDayDao.findScheduleForDate(workDayDate, employeeId);
        if (!opt.isPresent()) {
            logger.error("Employee #{} is not working on this day ({})", employeeId, workDayDate);
            return;
        }
        WorkDay workDay = opt.get();
        Optional<CourierData> optCd = courierDataDao.findOne(employeeId);
        if (!optCd.isPresent()) {
            logger.error("Employee is not courier on this day ({})", workDayDate);
            return;
        }
        CourierData courierData = optCd.get();
        if (!courierData.getCourierStatus().equals(CourierStatus.FREE)) {
            logger.warn("Courier is still busy ({})", courierData.getCourierStatus());
            return;
        }

        RouteProcessor.CourierEntry entry = new RouteProcessor.CourierEntry(workDayDate, workDay, courierData, employeeId);

        switch (courierData.getTravelMode()) {
            case WALKING:
                if (!walkWorkerQueue.contains(entry)) {
                    try {
                        walkWorkerQueue.put(entry);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException("Interrupted on adding courier", ex);
                    }
                }
                break;
            default:
                if (!driveWorkerQueue.contains(entry))
                    try {
                        driveWorkerQueue.put(entry);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException("Interrupted on adding courier", ex);
                    }
                break;
        }
    }

    public void createOrder(Order order) {
        Long contactId = order.getSenderContact().getContactId();
        Optional<Person> opt = personDao.findByContactId(contactId);
        if (!opt.isPresent()) {
            String errorMsg = String.format("Order receiver contact #%d is not registered user.", contactId);
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        String priority = "NORMAL";
        Person sender = opt.get();
        for (Role r : sender.getRoles()) {
            String rolePriority = r.getPriority();
            if (rolePriority != null && rolePriority.equals("VIP")) {
                priority = "VIP";
            }
        }

        OrderEntry oe = new RouteProcessor.OrderEntry(order, priority, false);
        switch (oe.travelMode) {
            case WALKING:
                walkOrdersQueue.add(oe);
                break;
            case DRIVING:
                driveOrdersQueue.add(oe);
                break;
        }

    }

    public void removeCourier(Long employeeId) {
        if(employeeId == null)
            return;

        List<Order> uncompletedByEmployee = orderDao.findConfirmedByEmployeeId(employeeId);
        for (Order o : uncompletedByEmployee) {
            o.setCourier(null);
            orderDao.save(o);   //update db
            removeOrder(o.getId());
            createOrder(o);
        }
        walkWorkerQueue.removeIf(c -> c.employeeId.equals(employeeId));
        driveWorkerQueue.removeIf(c -> c.employeeId.equals(employeeId));
    }

    public void removeOrder(Long orderId) {
        if(orderId == null)
            return;

        walkOrdersQueue.removeIf(orderEntry -> orderEntry.order.getId().equals(orderId));
        driveOrdersQueue.removeIf(orderEntry -> orderEntry.order.getId().equals(orderId));
    }
}
