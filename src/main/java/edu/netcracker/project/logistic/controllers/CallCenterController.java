package edu.netcracker.project.logistic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/call-center")
public class CallCenterController {

    @GetMapping("/orders/actual")
    public String viewTasksList(){
        return "call_center/call_center_orders_actual";
    }

    @GetMapping("/orders/history")
    public String viewTasksHistory(){
        return "call_center/call_center_orders_history";
    }

}
