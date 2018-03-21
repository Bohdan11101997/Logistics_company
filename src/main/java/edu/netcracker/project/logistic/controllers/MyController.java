package edu.netcracker.project.logistic.controllers;

import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.impl.ManagerStatisticsDaoImpl;
import edu.netcracker.project.logistic.dao.impl.OfficeDaoImpl;
import edu.netcracker.project.logistic.dao.impl.PersonCrudDaoImpl;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.OfficeService;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.UserService;
import edu.netcracker.project.logistic.service.impl.GeneratePdfReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
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


    @RequestMapping(value = "/pdfreport", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE )
    public ResponseEntity<InputStreamResource> citiesReport(Principal principal) throws IOException {

    String username = principal.getName();
        Optional<Person> optionalPerson = userService.findOne(username);

        Person person = optionalPerson.get();


        ArrayList<Person> employee = (ArrayList<Person>) managerStatisticsDao.EmployeesByOfficeOrCall_Center();
        ArrayList<Office> offices = (ArrayList<Office>) officeService.allOffices();

        List listEmployees = new com.itextpdf.text.List(List.ORDERED);
        List listOffices = new com.itextpdf.text.List(List.ORDERED);
        List listOrders = new com.itextpdf.text.List(List.ORDERED);

        listOffices.setAutoindent(false);
        listOffices.setSymbolIndent(42);

        listEmployees.setAutoindent(false);
        listEmployees.setSymbolIndent(42);


        listEmployees.add(new ListItem("Number employees:   "  +  managerStatisticsDao.countEmployees()));
        listEmployees.add(new ListItem("Number admins:  " + managerStatisticsDao.countEmployeesAdmins()));
        listEmployees.add(new ListItem("Number couriers: " + managerStatisticsDao.countEmployeesCouriers() ));
        listEmployees.add(new ListItem("On foot:  " + managerStatisticsDao.countEmployeesCouriersWalking()));
        listEmployees.add(new ListItem("On car:  " + managerStatisticsDao.countEmployeesCouriersDriving()));
        listEmployees.add(new ListItem("Call center agents:  " + managerStatisticsDao.countEmployeesAgentCallCenter()));
        listEmployees.add(new ListItem("Managers:  " + managerStatisticsDao.countEmployeesManagers()));

        listOffices.add(new ListItem("Number offices:   "  +  managerStatisticsDao.countOffices()));

 listOrders.add(new ListItem("Number orders:   "  + managerStatisticsDao.countOrders()));
 listOrders.add(new ListItem("Hand to hand:   "  +  managerStatisticsDao.countOrdersHandtoHand()));
 listOrders.add(new ListItem("From office:   "  +  managerStatisticsDao.countOrdersFromOffice()));

        GeneratePdfReport generatePdfReport = new GeneratePdfReport();
        ByteArrayInputStream bis = GeneratePdfReport.citiesReport(employee, offices, listEmployees,  listOffices, listOrders, person.getContact().getFirstName(), person.getContact().getLastName());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ManagerStatistic.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}