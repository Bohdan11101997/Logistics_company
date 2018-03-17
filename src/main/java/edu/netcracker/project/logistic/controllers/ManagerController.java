package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.impl.ManagerStatisticsDaoImpl;
import edu.netcracker.project.logistic.dao.impl.PersonCrudDaoImpl;
import edu.netcracker.project.logistic.dao.impl.RoleCrudDaoImpl;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.Role;
import edu.netcracker.project.logistic.model.SearchFormStatisticEmployee;
import edu.netcracker.project.logistic.service.EmployeeService;
import edu.netcracker.project.logistic.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    PersonCrudDaoImpl personCrudDao;

    @Autowired
    RoleService roleService;

    @Autowired
    EmployeeService employeeService;


    @Autowired
    ManagerStatisticsDaoImpl managerStatisticsDao;
    @Autowired
    RoleCrudDaoImpl roleCrudDao;

    @GetMapping("/statistics/employees")
    public String viewEmployeesStatistics(Model model){
        List<Person> employees = employeeService.findAll();
    model.addAttribute("employees", employees);
    model.addAttribute("availableRoles", roleCrudDao.findEmployeeRolesForManager());
    model.addAttribute("searchFormStatisticEmployee", new SearchFormStatisticEmployee());

    return "/manager/manager_statistics_employees";
    }

    @PostMapping("/statistics/employees")
    public String postViewEmployeesStatistics(Model model, @ModelAttribute("searchFormStatisticEmployee") SearchFormStatisticEmployee searchFormStatisticEmployee){
        List<Person> employees = managerStatisticsDao.searchStatisticForManager(searchFormStatisticEmployee);
        model.addAttribute("employees", employees);
        model.addAttribute("availableRoles", roleCrudDao.findEmployeeRolesForManager());
        model.addAttribute("searchFormStatisticEmployee", searchFormStatisticEmployee);
        System.out.println(employees);
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
    public String viewCommonStatistics(Model model){
        model.addAttribute("countEmployees", managerStatisticsDao.countEmployees());
//        model.addAttribute("countEmployeesAdmins",managerStatisticsDao.countEmployeesAdmins() );
//        model.addAttribute("countEmployeesCouriers",managerStatisticsDao.countEmployeesCouriers() );
//        model.addAttribute("countEmployeesCouriersDriving",managerStatisticsDao.countEmployeesCouriersDriving() );
//        model.addAttribute("countEmployeesCouriersWalking",managerStatisticsDao.countEmployeesCouriersWalking() );
//        model.addAttribute("countUsers",managerStatisticsDao.countUsers() );
//        model.addAttribute("countUsersNormal",managerStatisticsDao.countUsersNormal() );
//        model.addAttribute("countUsersVip",managerStatisticsDao.countUsersVip() );
//        model.addAttribute("countEmployeesAgentCallCenter",managerStatisticsDao.countEmployeesAgentCallCenter() );
//        model.addAttribute("countUnregisteredContacts",managerStatisticsDao.countUnregisteredContacts() );
//        model.addAttribute("",managerStatisticsDao );
//        model.addAttribute("",managerStatisticsDao );
        return "/manager/manager_statistics_common";
    }

    @GetMapping("/statistics/employee/{id}")
    public String viewSimpleEmployeeStatistics(){
        return "/manager/manager_statistics_employee_single";
    }


}
