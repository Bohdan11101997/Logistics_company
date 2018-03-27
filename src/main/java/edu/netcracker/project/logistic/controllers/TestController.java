package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.OfficeDao;
import edu.netcracker.project.logistic.dao.impl.AddressDaoImpl;
import edu.netcracker.project.logistic.dao.impl.ManagerStatisticsDaoImpl;
import edu.netcracker.project.logistic.dao.impl.OrderDaoImpl;
import edu.netcracker.project.logistic.dao.impl.PersonCrudDaoImpl;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.AddressService;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.RoleService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class TestController {

    @Autowired
    private OfficeDao officeDao;

    private PersonService personService;


    @Autowired
    ManagerStatisticsDaoImpl managerStatisticsDao;

    @Autowired
    AddressService addressService;

    @Autowired
    ContactDao contactDao;

    @Autowired
    PersonCrudDaoImpl personCrudDao;
    Address address;

  OrderDaoImpl orderDao;

    AddressDaoImpl addressDao;
    private RoleService roleService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public TestController(PersonService personService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.personService = personService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/test")
    public String test(Model model) {

//        SearchFormStatisticEmployee searchFormStatisticEmployee = new SearchFormStatisticEmployee();
//        searchFormStatisticEmployee.setFirstName("Tyrion");




    //        PdfView pdfView;

        Address address = new Address((long) 3,"Київ, Академіка Янгеля, 5");
        Address address1 = new Address((long) 3, "Академіка Янгеля, 5");
       addressService.save(address);
        addressService.save(address1);
////
////        System.out.println(address.check("qwqw"));
////
////        for(GeocodingResult gr : Address.getListOfAddresses(address.getName()))
////            System.out.println(gr.formattedAddress);
////        System.out.println("===========================================");
//        for(GeocodingResult gr : Address.getListOfAddresses(address1.getLocation()))
//            System.out.println(gr.formattedAddress);
//        Contact contact = new Contact(1L, "lol", "lol", "+2312312313");
//        contactDao.save(contact);
//        Person person1 = new Person("nick_name", "1121212", localDate, "sdfffsfsdf", contact);
//        personService.savePerson(person1);

        return "test";
    }


    @GetMapping("/error/403")
    public String error403() {
        return "/error/403";
    }

    @GetMapping("/main")
    public String user() {
        return "person_main";
    }


}
