package edu.netcracker.project.logistic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/statistics/employees")
    public String viewEmployeesStatistics(){
        return "/manager/manager_statistics_employees";
    }

    @GetMapping("/statistics/offices")
    public String viewOfficesStatistics(){
        return "/manager/manager_statistics_offices";
    }

    @GetMapping("/statistics/orders")
    public String viewOrdersStatistics(){
        return "/manager/manager_statistics_orders";
    }

    @GetMapping("/statistics/common")
    public String viewCommonStatistics(){
        return "/manager/manager_statistics_common";
    }

    @GetMapping("/statistics/employee/{id}")
    public String viewSimpleEmployeeStatistics(){
        return "/manager/manager_statistics_employee_single";
    }


}
