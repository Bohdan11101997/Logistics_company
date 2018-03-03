package edu.netcracker.project.logistic.processing;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.dao.TaskDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import edu.netcracker.project.logistic.model.Task;
import edu.netcracker.project.logistic.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        private int activeTasks;
        private Person employee;

        private EmployeeEntry(int activeTasks, Person employee) {
            this.activeTasks = activeTasks;
            this.employee = employee;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeEntry that = (EmployeeEntry) o;
            return Objects.equals(employee, that.employee);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employee);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

    private BlockingQueue<EmployeeEntry> workerQueue;
    private BlockingQueue<TaskEntry> taskQueue;

    private RoleCrudDao roleDao;
    private TaskDao taskDao;
    private PersonCrudDao personDao;
    private EmployeeService employeeService;
    private SessionRegistry sessionRegistry;

    public TaskProcessor(RoleCrudDao roleDao, TaskDao taskDao, PersonCrudDao personDao,
                         EmployeeService employeeService, SessionRegistry sessionRegistry) {
        this.roleDao = roleDao;
        this.taskDao = taskDao;
        this.personDao = personDao;
        this.employeeService = employeeService;
        this.sessionRegistry = sessionRegistry;
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
                Comparator.comparingInt(entry -> entry.activeTasks)
        );
    }

    private void prepareQueues() {
        List<Task> uncompletedTasks = taskDao.findUncompleted();
        for (Task t : uncompletedTasks) {
            addTask(t);
        }
    }

    @Transactional // CANT USE ON PRIVATE METHODS
    public void assignTask(TaskEntry taskEntry, EmployeeEntry employeeEntry) throws InterruptedException {
        // TODO think about how to reduce number of database calls
        Task task = taskEntry.task;
        Person employee = employeeEntry.employee;

        // Check if employee is still call agent
        Optional<Person> employeeRecord = personDao.findOne(employee.getId());
        if (!employeeRecord.isPresent()) {
            logger.info("Employee instance not found.");
            taskQueue.put(taskEntry);
            return;
        }
        boolean callCentreAgent = false;
        for (Role r : employeeRecord.get().getRoles()) {
            if (r.getRoleName().equals("ROLE_CALL_CENTER")) {
                callCentreAgent = true;
            }
        }
        if (!callCentreAgent) {
            logger.info("Person instance is not call centre agent");
            taskQueue.put(taskEntry);
            return;
        }
        task.setEmployee(employee);
        taskDao.save(task);
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

    public void addTask(Task task) {
        Long contactId = task.getOrder().getSenderContact().getContactId();
        Optional<Person> opt = personDao.findByContactId(contactId);
        if (!opt.isPresent()) {
            logger.error("Order receiver contact #{} is not registered user.", contactId);
            return;
        }
        Person sender = opt.get();
        String priority = "NORMAL";
        for (Role r : sender.getRoles()) {
            if (r.getPriority().equals("VIP")) {
                priority = "VIP";
            }
        }
        taskQueue.add(new TaskEntry(task, priority));
    }
}
