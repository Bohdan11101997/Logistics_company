package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.dao.impl.ManagerStatisticsDaoImpl;
import edu.netcracker.project.logistic.dao.impl.PersonCrudDaoImpl;
import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.service.PersonService;
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
import java.util.ArrayList;
import java.util.List;


@Controller
public class MyController {

    @Autowired
    ManagerStatisticsDaoImpl managerStatisticsDao;

    @Autowired
    PersonCrudDaoImpl personService;

    @RequestMapping(value = "/pdfreport", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> citiesReport() throws IOException {

         List<Person> employee = personService.findAll();

        GeneratePdfReport generatePdfReport = new GeneratePdfReport();
        ByteArrayInputStream bis = GeneratePdfReport.citiesReport(employee);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}