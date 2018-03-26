package edu.netcracker.project.logistic.controllers;

import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import edu.netcracker.project.logistic.dao.impl.ManagerStatisticsDaoImpl;
import edu.netcracker.project.logistic.dao.impl.PersonCrudDaoImpl;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.SearchFormOrderStatistic;
import edu.netcracker.project.logistic.service.OfficeService;
import edu.netcracker.project.logistic.service.UserService;
import edu.netcracker.project.logistic.service.impl.GeneratePdfReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;


@Controller
public class MyController {

    @Autowired
    ManagerStatisticsDaoImpl managerStatisticsDao;

    @Autowired
    PersonCrudDaoImpl personService;

    @Autowired
    OfficeService officeService;

    @Autowired
    UserService userService;


    @RequestMapping(value = "/pdfreport", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> citiesReport(Principal principal, @ModelAttribute("searchFormOrderStatistic") SearchFormOrderStatistic searchFormOrderStatistic) throws IOException {

        String username = principal.getName();
        Optional<Person> optionalPerson = userService.findOne(username);

        Person person = optionalPerson.get();


        ArrayList<Person> employee = (ArrayList<Person>) managerStatisticsDao.EmployeesByCourierOrCall_Center();
        ArrayList<Office> offices = (ArrayList<Office>) officeService.allOffices();

        List listEmployees = new com.itextpdf.text.List(List.ORDERED);
        List listOffices = new com.itextpdf.text.List(List.ORDERED);
        List listOrders = new com.itextpdf.text.List(List.ORDERED);

        listOffices.setAutoindent(false);
        listOffices.setSymbolIndent(42);

        listEmployees.setAutoindent(false);
        listEmployees.setSymbolIndent(42);


        listEmployees.add(new ListItem("Number employees:   " + managerStatisticsDao.countEmployees(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listEmployees.add(new ListItem("Number admins:  " + managerStatisticsDao.countEmployeesAdmins(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listEmployees.add(new ListItem("Number couriers: " + managerStatisticsDao.countEmployeesCouriers(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listEmployees.add(new ListItem("On foot:  " + managerStatisticsDao.countEmployeesCouriersWalking(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listEmployees.add(new ListItem("On car:  " + managerStatisticsDao.countEmployeesCouriersDriving(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listEmployees.add(new ListItem("Call center agents:  " + managerStatisticsDao.countEmployeesAgentCallCenter(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listEmployees.add(new ListItem("Managers:  " + managerStatisticsDao.countEmployeesManagers(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));

        listOffices.add(new ListItem("Number offices:   " + managerStatisticsDao.countOffices()));

        listOrders.add(new ListItem("Number orders:   " + managerStatisticsDao.countOrdersBetweenDate(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listOrders.add(new ListItem("Hand to hand:   " + managerStatisticsDao.countOrdersHandToHand(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));
        listOrders.add(new ListItem("From office:   " + managerStatisticsDao.countOrdersFromOffice(searchFormOrderStatistic.getFrom(), searchFormOrderStatistic.getTo())));


        ByteArrayInputStream bis = GeneratePdfReport.citiesReport(employee, offices, listEmployees, listOffices, listOrders, person.getContact().getFirstName(), person.getContact().getLastName());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ManagerStatistic.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}


