package edu.netcracker.project.logistic.processing;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.WorkDayDao;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

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
            } catch(Exception e) {}
        }

        public OrderEntry(OrderEntry other){
            this.order = new Order(other.order);
            this.priority = other.priority;
            this.travelMode = other.travelMode;
            this.orderFromClient = other.orderFromClient;
        }

        public String getPriority(){
            return priority;
        }

        public Order getOrder(){
            return order;
        }

        public TravelMode getTravelMode(){
            return travelMode;
        }

        public boolean isOrderFromClient() {
            return orderFromClient;
        }

        public void setOrderFromClient(boolean value){
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

        public LatLng getLastLocation(){
            if(lastLocation == null){
                String location[] = courierData.getLastLocation().split("[,]");
                String latitude = location[0];
                String longitude = location[1];
                lastLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            }
            return lastLocation;
        }

        public Long getEmployeeId(){
            return employeeId;
        }

        public CourierData getCourierData(){
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

    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

    private BlockingQueue<RouteProcessor.CourierEntry> walkWorkerQueue;
    private BlockingQueue<RouteProcessor.CourierEntry> driveWorkerQueue;
    private BlockingQueue<RouteProcessor.OrderEntry> walkOrdersQueue;
    private BlockingQueue<RouteProcessor.OrderEntry> driveOrdersQueue;

    private OrderDao orderDao;
    private PersonCrudDao personDao;
    private WorkDayDao workDayDao;
    private CourierDataDao courierDataDao;
    private NotificationService notificationService;
    private FlowBuilder flowBuilder;

    public RouteProcessor(OrderDao orderDao,
                          PersonCrudDao personDao,
                          WorkDayDao workDayDao,
                          CourierDataDao courierDataDao,
                          NotificationService notificationService) {
        this.orderDao = orderDao;
        this.personDao = personDao;
        this.workDayDao = workDayDao;
        this.courierDataDao = courierDataDao;
        this.notificationService = notificationService;

        //TODO: set normal center
        LatLng center = new LatLng(50.420506, 30.529369);
        this.walkOrdersQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeOrderComparator(center,false, TravelMode.WALKING)
                );
        this.driveOrdersQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeOrderComparator(center,false, TravelMode.DRIVING)
        );

        this.walkWorkerQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeCourierComparator(center,false, TravelMode.WALKING)
        );
        this.driveWorkerQueue = new PriorityBlockingQueue<>(
                11,
                FlowBuilder.makeCourierComparator(center,false, TravelMode.DRIVING)
        );
    }

    private void prepareQueues() {
        List<Order> confirmedOrders = orderDao.findConfirmed();
        for (Order data : confirmedOrders) {
            createOrder(data);
        }
    }

    @Transactional
    public boolean assignOrders(List<RouteProcessor.OrderEntry> orderEntries, RouteProcessor.CourierEntry employeeEntry) throws InterruptedException {
        for(RouteProcessor.OrderEntry oe : orderEntries) {
           if(!assignOrder(oe, employeeEntry))
               return false; //early out
        }
        return true;
    }

    @Transactional
    public boolean assignOrder(RouteProcessor.OrderEntry orderEntry, RouteProcessor.CourierEntry employeeEntry) throws InterruptedException {
        Order order = orderEntry.order;
        Long employeeId = employeeEntry.employeeId;

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
        if (!todayDate.equals(employeeEntry.workDayDate)) {
            Optional<WorkDay> opt = workDayDao.findScheduleForDate(todayDate, employeeId);
            if (!opt.isPresent()) {
                logger.error("Employee is not working on this day ({})", todayDate);
                addOrder(orderEntry);
                return false;
            }
            employeeEntry.workDay = opt.get();
            employeeEntry.workDayDate = todayDate;
        }
        // Check if work day is nearing end
        if (now.toLocalTime().isBefore(employeeEntry.workDay.getStartTime()) ||
                now.toLocalTime().plus(WORK_DAY_END_NEARING_INTERVAL)
                        .isAfter(employeeEntry.workDay.getEndTime())) {
           addOrder(orderEntry);
            return false;
        }

        /*
        taskDao.save(task);
        notificationService.send(employee.getUserName(), new Notification("task", "Created new task"));
        */
        order.setCourier(employee);
        logger.info("Assigned order #{} to employee #{}", order.getId(), employeeId);
        //TODO: dao save order and save what?
        walkWorkerQueue.put(employeeEntry);
        return true;
    }

    @Async
    public void taskLoop() throws InterruptedException {
        Office office = new Office();
        office.setOfficeId((long) (404));
        office.setName("NetCrackerOffice");
        office.setAddress(new Address((long) (404),
                new LatLng(50.420506, 30.529369)));//our NetCrackerOffice
        flowBuilder = new RadiusSelector(walkOrdersQueue, driveOrdersQueue, walkWorkerQueue, driveWorkerQueue, office);
        try {
            prepareQueues();
            while (true) {
                RouteProcessor.CourierEntry worker = null;
                RouteProcessor.OrderEntry oe = null;
                if(driveOrdersQueue.isEmpty()){
                    worker = walkWorkerQueue.take();
                    oe = walkOrdersQueue.poll();
                }
                if(worker == null || oe == null) {
                    worker = driveWorkerQueue.take();
                    oe = driveOrdersQueue.take();
                }

                if(!flowBuilder.process(oe, worker)){
                    logger.error(flowBuilder.getError());
                }

                if(assignOrders(flowBuilder.getOrdersSequence(), worker)){
                    //TODO: any work here
                    notificationService.send(personDao.findOne(worker.employeeId).get().getUserName(),
                            new Notification("route","New orders added"));
                }
            }
        } catch (InterruptedException ex) {
            logger.error("Route processor terminated");
            throw ex;
        }
    }

    public void addOrder(Long orderId) throws InterruptedException {
        Optional<Order> order = orderDao.findOne(orderId);
        if(order.isPresent()){
            addOrder(order.get());
        }
    }

    public void addOrder(Order order) throws InterruptedException {
        boolean vip = false;

        Optional<Person> sender = personDao.findOne(order.getSenderContact().getContactId());
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
        switch (orderEntry.travelMode){ //put back
            case WALKING:
                walkOrdersQueue.put(orderEntry);
                break;
            default:
                driveOrdersQueue.put(orderEntry);
                break;
        }
    }

    public void addOrder(OrderEntry orderEntry) throws InterruptedException {
        switch (orderEntry.travelMode){ //put back
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
            logger.error("Employee is not working on this day ({})", workDayDate);
            return;
        }
        WorkDay workDay = opt.get();
        Optional<CourierData> optCd = courierDataDao.findOne(employeeId);
        if (!optCd.isPresent()) {
            logger.error("Employee is not courier on this day ({})", workDayDate);
            return;
        }
        CourierData courierData = optCd.get();
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
        switch(oe.travelMode){
            case WALKING:
                walkOrdersQueue.add(oe);
                break;
            case DRIVING:
                driveOrdersQueue.add(oe);
                break;
        }

    }

    public void removeCourier(Long employeeId){
        List<Order> uncompletedByEmployee = orderDao.findConfirmedByEmployeeId(employeeId);
        for (Order o : uncompletedByEmployee) {
            o.setCourier(null);
            orderDao.save(o);   //update db
            removeOrder(o.getId());
            createOrder(o);
        }
    }

    public void removeOrder(Long orderId){
        walkOrdersQueue.removeIf(orderEntry -> {
            orderEntry.order.setCourier(null);
            return orderEntry.order.getId() == orderId;
        });
        driveOrdersQueue.removeIf(orderEntry -> {
            orderEntry.order.setCourier(null);
            return orderEntry.order.getId() == orderId;
        });
    }
}
