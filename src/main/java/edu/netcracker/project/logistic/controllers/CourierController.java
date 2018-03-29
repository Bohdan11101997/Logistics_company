package edu.netcracker.project.logistic.controllers;


import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/courier")
public class CourierController {
    private PersonService personService;
    private OrderDao orderDao;

    public CourierController(PersonService personService, OrderDao orderDao) {
        this.personService = personService;
        this.orderDao = orderDao;
    }

    @GetMapping("/route")
    public String deliveryRoute(Model model, Principal principal) {
        Optional<Person> courier = personService.findOne(principal.getName());
        if (!courier.isPresent()) {
            return "error/500";
        }
        model.addAttribute("orders",
                orderDao.findNotProcessedByEmployeeId(courier.get().getId()));
        return "courier/courier_route";
    }

    @GetMapping("/orders")
    public String viewOrderHistory(Principal principal, Model model) {
        Person user = personService.findOne(principal.getName())
                .orElseThrow(IllegalStateException::new);
        model.addAttribute("orders", orderDao.findByCourierId(user.getId()));
        return "courier/courier_orders_history";
    }
}
