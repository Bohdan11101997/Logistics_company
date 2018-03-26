package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.impl.*;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.*;
import edu.netcracker.project.logistic.validation.CurrentPasswordValidator;
import edu.netcracker.project.logistic.validation.NewOrderValidator;
import edu.netcracker.project.logistic.validation.UpdateUserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

    private SmartValidator fieldValidator;
    private UpdateUserValidator updateUserValidator;
    private CurrentPasswordValidator currentPasswordValidator;
    private NewOrderValidator newOrderValidator;
    private UserService userService;
    private OrderService orderService;
    private SecurityService securityService;
    private OrderTypeDao orderTypeDao;
    private PasswordEncoder passwordEncoder;


    @Autowired
    OrderStatusDaoImpl orderStatusDao;

    @Autowired
    OfficeDaoImpl officeDao;

    @Autowired
    public ManagerController(SmartValidator fieldValidator, UpdateUserValidator updateUserValidator,
                          CurrentPasswordValidator currentPasswordValidator, NewOrderValidator newOrderValidator,
                          UserService userService, SecurityService securityService, OrderTypeDao orderTypeDao,
                          PasswordEncoder passwordEncoder, OrderService orderService) {
        this.fieldValidator = fieldValidator;
        this.updateUserValidator = updateUserValidator;
        this.currentPasswordValidator = currentPasswordValidator;
        this.newOrderValidator = newOrderValidator;
        this.userService = userService;
        this.securityService = securityService;
        this.orderTypeDao = orderTypeDao;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
    }

    @GetMapping("/statistics/employees")
    public String viewEmployeesStatistics(Model model){
        List<Person> employees = managerStatisticsDao.EmployeesByCourierOrCall_Center();
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
        return "/manager/manager_statistics_employees";
    }


    @GetMapping("/statistics/offices")
    public String viewOfficesStatistics(){
        return "/manager/manager_statistics_offices";
    }


    @GetMapping("/statistics/orders")
    public  String getStatisticOrderByManager(Model model)
    {

        model.addAttribute("destination_typeOrders", orderTypeDao.findAll());
        model.addAttribute("status_OrdersList", orderStatusDao.findAll());
        model.addAttribute("searchFormOrderStatistic", new SearchFormOrderStatistic());
        return "manager/manager_statistics_orders";
    }


    @PostMapping("/statistics/orders")
    public  String SearchOrdersByManager(@ModelAttribute("searchFormOrderStatistic") SearchFormOrderStatistic searchFormOrderStatistic, Model model)
    {
        List<Statistic_task> personList = managerStatisticsDao.searchStatisiticOrders(searchFormOrderStatistic);
        model.addAttribute("countOrder",managerStatisticsDao.countOrders());
        model.addAttribute("destination_typeOrders", orderTypeDao.findAll());
        model.addAttribute("status_OrdersList", orderStatusDao.findAll());
        model.addAttribute("searchFormOrderStatistic",searchFormOrderStatistic);
        model.addAttribute("fromDate", searchFormOrderStatistic.getFrom());
        model.addAttribute("toDate", searchFormOrderStatistic.getTo());
        model.addAttribute("personList", personList);
        model.addAttribute("countOrdersBetweenDate",managerStatisticsDao.CountOrdersBetweenDate(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countOrder",managerStatisticsDao.countOrders());
        return "manager/manager_statistics_orders";
    }


    @GetMapping("/statistics/common")
    public String viewCommonStatistics(Model model){
        model.addAttribute("countOrdersHandtoHand", managerStatisticsDao.countOrdersHandtoHand());
        model.addAttribute("countOrdersFromOffice", managerStatisticsDao.countOrdersFromOffice());
        model.addAttribute("countEmployees", managerStatisticsDao.countEmployees());
        model.addAttribute("countEmployeesAdmins",managerStatisticsDao.countEmployeesAdmins() );
        model.addAttribute("countEmployeesCouriers",managerStatisticsDao.countEmployeesCouriers() );
        model.addAttribute("countEmployeesCouriersDriving",managerStatisticsDao.countEmployeesCouriersDriving() );
        model.addAttribute("countEmployeesCouriersWalking",managerStatisticsDao.countEmployeesCouriersWalking() );
        model.addAttribute("countEmployeesAgentCallCenter",managerStatisticsDao.countEmployeesAgentCallCenter() );
        model.addAttribute("countEmployeesManagers", managerStatisticsDao.countEmployeesManagers());
        model.addAttribute("countUsers",managerStatisticsDao.countUsers() );
        model.addAttribute("countUsersNormal",managerStatisticsDao.countUsersNormal());
        model.addAttribute("countUsersVip",managerStatisticsDao.countUsersVip() );
        model.addAttribute("countUnregisteredContacts",managerStatisticsDao.countUnregisteredContacts() );
        model.addAttribute("countOffice",managerStatisticsDao.countOffices());
        model.addAttribute("countOrder",managerStatisticsDao.countOrders());
        return "/manager/manager_statistics_common";
    }

    @GetMapping("/statistics/employee/{id}")
    public String viewSimpleEmployeeStatistics(){
        return "/manager/manager_statistics_employee_single";
    }


    @PostMapping("/FindOfficeByDepartmentOrAddressStatistic")
    public String findByDepartment(@RequestParam String department, @RequestParam String address, Model model) {
        model.addAttribute("offices", officeDao.findByDepartmentOrAddress(department, address));
        return "/manager/manager_statistics_offices";

    }

}
