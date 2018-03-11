package edu.netcracker.project.logistic.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courier")
public class CourierController {

    @GetMapping("/tasks/list")
    public String viewTasksList(){

        return "courier/courier_tasks_list";
    }

    @PostMapping("tasks/list")
    public String saveCompletedTasks(){

        return "redirect:/courier/tasks/list";
    }

    @GetMapping("/tasks/map")
    public String viewTasksMap(){

        return "courier/courier_tasks_map";
    }

}
