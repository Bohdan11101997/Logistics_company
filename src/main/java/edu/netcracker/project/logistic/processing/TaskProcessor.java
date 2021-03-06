package edu.netcracker.project.logistic.processing;

import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.TaskDao;
import edu.netcracker.project.logistic.dao.WorkDayDao;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Scope("singleton")
@Component
public class TaskProcessor {
    private static class TaskEntry {
        private String priority;
        private Task task;

        private TaskEntry(Task task, String priority) {
            this.task = task;
            this.priority = priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TaskEntry taskEntry = (TaskEntry) o;
            return Objects.equals(task, taskEntry.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task);
        }
    }

    private static class EmployeeEntry {
        private int tasksAssigned;
        private LocalDate workDayDate;
        private WorkDay workDay;
        private Long employeeId;

        private EmployeeEntry(LocalDate workDayDate, WorkDay workDay, Long employeeId) {
            this.workDayDate = workDayDate;
            this.workDay = workDay;
            this.employeeId = employeeId;
            this.tasksAssigned = 0;
        }

        private EmployeeEntry(Long employeeId) {
            this.employeeId = employeeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeEntry that = (EmployeeEntry) o;
            return Objects.equals(employeeId, that.employeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId);
        }
    }

    private static final Duration WORK_DAY_END_NEARING_INTERVAL = Duration.ofMinutes(10);

    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

    private BlockingQueue<EmployeeEntry> workerQueue;
    private BlockingQueue<TaskEntry> taskQueue;

    private OrderDao orderDao;
    private TaskDao taskDao;
    private PersonCrudDao personDao;
    private WorkDayDao workDayDao;
    private NotificationService notificationService;

    public TaskProcessor(OrderDao orderDao, TaskDao taskDao,
                         PersonCrudDao personDao, WorkDayDao workDayDao,
                         NotificationService notificationService) {
        this.orderDao = orderDao;
        this.taskDao = taskDao;
        this.personDao = personDao;
        this.workDayDao = workDayDao;
        this.notificationService = notificationService;
        this.taskQueue = new PriorityBlockingQueue<>(
                11,
                (t1, t2) -> {
                    int t1Priority = t1.priority.equals("VIP") ? 1 : 0;
                    int t2Priority = t2.priority.equals("VIP") ? 1 : 0;
                    return t1Priority - t2Priority;
                }
        );
        this.workerQueue = new PriorityBlockingQueue<>(
                11,
                Comparator.comparingInt(entry -> entry.tasksAssigned)
        );
    }

    private void prepareQueues() {
        List<Order> notProcessedOrders = orderDao.findNotProcessed();
        for (Order data : notProcessedOrders) {
            createTask(data);
        }
    }

    @Transactional
    public void assignTask(TaskEntry taskEntry, EmployeeEntry employeeEntry) throws InterruptedException {
        Task task = taskEntry.task;
        Long employeeId = employeeEntry.employeeId;

        // Check if employee is still call center agent
        Optional<Person> employeeRecord = personDao.findOne(employeeId);
        if (!employeeRecord.isPresent()) {
            logger.info("Employee instance not found.");
            taskQueue.put(taskEntry);
            return;
        }
        Person employee = employeeRecord.get();
        boolean callCentreAgent = false;
        for (Role r : employee.getRoles()) {
            if (r.getRoleName().equals("ROLE_CALL_CENTER")) {
                callCentreAgent = true;
                break;
            }
        }
        if (!callCentreAgent) {
            logger.info("Person instance is not call centre agent");
            taskQueue.put(taskEntry);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate todayDate = now.toLocalDate();
        // Check if stored schedule is still actual
        if (!todayDate.equals(employeeEntry.workDayDate)) {
            Optional<WorkDay> opt = workDayDao.findScheduleForDate(todayDate, employeeId);
            if (!opt.isPresent()) {
                logger.error("Employee is not working on this day ({})", todayDate);
                taskQueue.put(taskEntry);
                return;
            }
            employeeEntry.workDay = opt.get();
            employeeEntry.workDayDate = todayDate;
        }
        // Check if work day is nearing end
        if (now.toLocalTime().isBefore(employeeEntry.workDay.getStartTime()) ||
                now.toLocalTime().plus(WORK_DAY_END_NEARING_INTERVAL)
                        .isAfter(employeeEntry.workDay.getEndTime())) {
            taskQueue.put(taskEntry);
            return;
        }


        task.setEmployeeId(employeeId);
        taskDao.save(task);
        logger.info("Assigned task #{} to employee #{}", task.getId(), employeeId);
        employeeEntry.tasksAssigned += 1;
        notificationService.send(employee.getUserName(), new Notification("task", "Created new task"));
        workerQueue.put(employeeEntry);
    }

    @Async
    public void taskLoop() throws InterruptedException {
        try {
            prepareQueues();
            while (true) {
                TaskEntry t = taskQueue.take();
                EmployeeEntry worker = workerQueue.take();
                assignTask(t, worker);
            }
        } catch (InterruptedException ex) {
            logger.error("Task processor terminated");
            throw ex;
        }
    }

    public void addAgent(Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate workDayDate = now.toLocalDate();

        Optional<WorkDay> opt = workDayDao.findScheduleForDate(workDayDate, employeeId);
        if (!opt.isPresent()) {
            logger.error("Employee is not working on this day ({})", workDayDate);
            return;
        }
        WorkDay workDay = opt.get();
        EmployeeEntry entry = new EmployeeEntry(workDayDate, workDay, employeeId);
        if (!workerQueue.contains(entry))
            try {
                workerQueue.put(entry);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted on adding call center agent", ex);
            }
    }

    @Transactional
    public void removeAgent(Long employeeId) {
        List<Task> uncompletedByEmployee = taskDao.findUncompletedByEmployeeId(employeeId);
        for (Task t : uncompletedByEmployee) {
            t.setEmployeeId(null);
            taskDao.delete(t.getId());
            t.setId(null);
        }
        workerQueue.remove(new EmployeeEntry(employeeId));
        for (Task t: uncompletedByEmployee) {
            Order order = orderDao.findOne(t.getOrderId())
                    .orElseThrow(() -> new IllegalStateException("Order for task don't exists"));
            createTask(order);
        }
    }

    public void createTask(Order order) {
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
        Task task = new Task();
        task.setOrderId(order.getId());
        task.setCompleted(false);
        taskQueue.add(new TaskEntry(task, priority));
    }
}
