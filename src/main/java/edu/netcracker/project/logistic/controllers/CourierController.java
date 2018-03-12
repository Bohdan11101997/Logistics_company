package edu.netcracker.project.logistic.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courier")
public class CourierController {

    @GetMapping("/orders/actual")
    public String viewTasksList(){
        return "courier/courier_orders_actual";
    }

    @PostMapping("orders/actual")
    public String saveCompletedTasks(){
        return "redirect:/courier/orders/actual";
    }

    @GetMapping("/orders/history")
    public String viewTaskHistory(){
        return "courier/courier_orders_history";
    }

    @GetMapping("/orders/map")
    public String viewTasksMap(){
        return "courier/courier_orders_map";
    }

}
