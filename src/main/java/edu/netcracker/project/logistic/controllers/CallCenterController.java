package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.TaskDao;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/call-center")
public class CallCenterController {
    private PersonService personService;
    private OrderDao orderDao;
    private TaskDao taskDao;

    public CallCenterController(PersonService personService, OrderDao orderDao, TaskDao taskDao) {
        this.personService = personService;
        this.orderDao = orderDao;
        this.taskDao = taskDao;
    }


    @GetMapping("/orders/actual")
    public String viewTasksList(Model model, Principal principal){
        Optional<Person> opt = personService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        List<Order> orders = orderDao.findNotProcessedByEmployeeId(opt.get().getId());
        model.addAttribute("orders", orders);
        return "call_center/call_center_orders_actual";
    }

    @GetMapping("/orders/history")
    public String viewTasksHistory(){
        return "call_center/call_center_orders_history";
    }

    @GetMapping("/orders/{id}/reject")
    public String rejectOrder(@PathVariable Long id) {
        return "call_center/call_center_orders_actual";
    }

    @GetMapping("/orders/{id}/confirm")
    public String confirmOrder(@PathVariable Long id) {
        return "call_center/call_center_orders_actual";
    }
}
