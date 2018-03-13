package edu.netcracker.project.logistic.controllers;


import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.OrderService;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/courier")
public class CourierController {
    private PersonService personService;
    private OrderService orderService;
    private OrderDao orderDao;

    public CourierController(PersonService personService, OrderService orderService, OrderDao orderDao) {
        this.personService = personService;
        this.orderService = orderService;
        this.orderDao = orderDao;
    }

    @GetMapping("/orders/actual")
    public String viewTasksList(Model model, Principal principal) {
        Optional<Person> courier = personService.findOne(principal.getName());
        if (!courier.isPresent()) {
            return "error/500";
        }
        model.addAttribute("orders",
                orderDao.findNotProcessedByEmployeeId(courier.get().getId()));
        return "courier/courier_orders_actual";
    }

    @PostMapping("orders/actual")
    public String saveCompletedTasks() {
        return "redirect:/courier/orders/actual";
    }

    @GetMapping("/orders/history")
    public String viewTaskHistory() {
        return "courier/courier_orders_history";
    }

    @GetMapping("/orders/map")
    public String viewTasksMap() {
        return "courier/courier_orders_map";
    }

    @PostMapping("/orders/{id}/delivered")
    public String confirmDeliveredOrder(@PathVariable Long id, Principal principal) {
        Optional<Person> employee = personService.findOne(principal.getName());
        if (!employee.isPresent()) {
            return "error/500";
        }
        try {
            orderService.confirmDelivered(employee.get().getId(), id);
        } catch (IllegalArgumentException ex) {
            return "error/400";
        }
        return "call_center/call_center_orders_actual";
    }

    @PostMapping("/orders/{id}/failed")
    public String confirmFailedOrder(@PathVariable Long id, Principal principal) {
        Optional<Person> employee = personService.findOne(principal.getName());
        if (!employee.isPresent()) {
            return "error/500";
        }
        try {
            orderService.confirmFailed(employee.get().getId(), id);
        } catch (IllegalArgumentException ex) {
            return "error/400";
        }
        return "call_center/call_center_orders_actual";
    }
}
