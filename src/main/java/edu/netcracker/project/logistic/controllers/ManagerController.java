package edu.netcracker.project.logistic.controllers;


import edu.netcracker.project.logistic.dao.*;

import edu.netcracker.project.logistic.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {



    private ManagerStatisticsDao managerStatisticsDao;
    private RoleCrudDao roleCrudDao;
    private OrderStatusDao orderStatusDao;
    private  OrderTypeDao orderTypeDao;
    private OfficeDao officeDao;


    @Autowired
    public ManagerController(ManagerStatisticsDao managerStatisticsDao, RoleCrudDao roleCrudDao, OrderStatusDao orderStatusDao, OrderTypeDao orderTypeDao, OfficeDao officeDao) {
        this.managerStatisticsDao = managerStatisticsDao;
        this.roleCrudDao = roleCrudDao;
        this.orderStatusDao = orderStatusDao;
        this.orderTypeDao = orderTypeDao;
        this.officeDao = officeDao;
    }

    @GetMapping("/statistics/employees")
    public String viewEmployeesStatistics(Model model) {
        List<Person> employees = managerStatisticsDao.employeesByCourierOrCall_Center();
        model.addAttribute("employees", employees);
        model.addAttribute("availableRoles", roleCrudDao.findEmployeeRolesForManager());
        model.addAttribute("searchFormStatisticEmployee", new SearchFormStatisticEmployee());

        return "/manager/manager_statistics_employees";
    }

    @PostMapping("/statistics/employees")
    public String postViewEmployeesStatistics(Model model, @ModelAttribute("searchFormStatisticEmployee") SearchFormStatisticEmployee searchFormStatisticEmployee) {
        List<Person> employees = managerStatisticsDao.searchStatisticForManager(searchFormStatisticEmployee);
        model.addAttribute("employees", employees);
        model.addAttribute("availableRoles", roleCrudDao.findEmployeeRolesForManager());
        model.addAttribute("searchFormStatisticEmployee", searchFormStatisticEmployee);
        return "/manager/manager_statistics_employees";
    }




    @GetMapping("/statistics/orders")
    public String getStatisticOrderByManager(Model model) {

        model.addAttribute("destination_typeOrders", orderTypeDao.findAll());
        model.addAttribute("status_OrdersList", orderStatusDao.findAll());
        model.addAttribute("searchFormOrderStatistic", new SearchFormOrderStatistic());
        return "manager/manager_statistics_orders";
    }


    @PostMapping("/statistics/orders")
    public String SearchOrdersByManager(@ModelAttribute("searchFormOrderStatistic") SearchFormOrderStatistic searchFormOrderStatistic, Model model) {
        List<StatisticTask> personList = managerStatisticsDao.searchStatisticOrders(searchFormOrderStatistic);
        model.addAttribute("countOrder", managerStatisticsDao.countOrders());
        model.addAttribute("destination_typeOrders", orderTypeDao.findAll());
        model.addAttribute("status_OrdersList", orderStatusDao.findAll());
        model.addAttribute("searchFormOrderStatistic", searchFormOrderStatistic);
        model.addAttribute("fromDate", searchFormOrderStatistic.getFrom());
        model.addAttribute("toDate", searchFormOrderStatistic.getTo());
        model.addAttribute("personList", personList);
        model.addAttribute("countOrdersBetweenDate", managerStatisticsDao.countOrdersBetweenDate(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countOrder", managerStatisticsDao.countOrders());
        return "manager/manager_statistics_orders";
    }


    @GetMapping("/statistics/common")
    public String getCommonStatistics(@ModelAttribute("searchFormOrderStatistic") SearchFormOrderStatistic searchFormOrderStatistic) {
        return "/manager/manager_statistics_common";
    }

    @PostMapping("/statistics/common")
    public String viewCommonStatistics(Model model, @ModelAttribute("searchFormOrderStatistic") SearchFormOrderStatistic searchFormOrderStatistic) {
        model.addAttribute("countOrdersHandToHand", managerStatisticsDao.countOrdersHandToHand(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countOrdersFromOffice", managerStatisticsDao.countOrdersFromOffice(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countEmployees", managerStatisticsDao.countEmployees(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countEmployeesAdmins", managerStatisticsDao.countEmployeesAdmins(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countEmployeesCouriers", managerStatisticsDao.countEmployeesCouriers(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countEmployeesCouriersDriving", managerStatisticsDao.countEmployeesCouriersDriving(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countEmployeesCouriersWalking", managerStatisticsDao.countEmployeesCouriersWalking(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countEmployeesAgentCallCenter", managerStatisticsDao.countEmployeesAgentCallCenter(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countEmployeesManagers", managerStatisticsDao.countEmployeesManagers(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countUsers", managerStatisticsDao.countUsers(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countUsersNormal", managerStatisticsDao.countUsersNormal(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countUsersVip", managerStatisticsDao.countUsersVip(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countUnregisteredContacts", managerStatisticsDao.countUnregisteredContacts(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        model.addAttribute("countOffice", managerStatisticsDao.countOffices());
        model.addAttribute("countOrder", managerStatisticsDao.countOrdersBetweenDate(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo()));
        return "/manager/manager_statistics_common";
    }

    @GetMapping("/statistics/offices")
    public String getAllOffice(Model model) {
        model.addAttribute("offices", officeDao.allOffices());
        model.addAttribute("officeSearchForm", new OfficeSearchForm());
        return "/manager/manager_statistics_offices";
    }

    @PostMapping("/statistics/offices")
    public String findByDepartmentOrAddress( @ModelAttribute("officeSearchForm") OfficeSearchForm officeSearchForm,  Model model) {
        model.addAttribute("offices", officeDao.findByDepartmentOrAddress(officeSearchForm));
        return "/manager/manager_statistics_offices";

    }
}
