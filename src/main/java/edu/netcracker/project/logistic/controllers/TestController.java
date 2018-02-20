package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Person;

import edu.netcracker.project.logistic.dao.OfficeDao;
import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.impl.ContactDaoImpl;

import edu.netcracker.project.logistic.service.AddressService;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;


@Controller
public class TestController {

 @Autowired
 private OfficeDao officeDao;

   private PersonService personService;

   @Autowired
    AddressService addressService;

   @Autowired
    ContactDao contactDao;

  private   RoleService roleService;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public TestController(PersonService personService,RoleService roleService, BCryptPasswordEncoder passwordEncoder ) {
        this.personService=personService;
        this.roleService=roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/test")
    public String test(Model model) {
      
        Address address = new Address("м. Київ");
        Address address1 = new Address((long)3,"м. Житомир");
        addressService.save(address);
        addressService.save(address1);


        LocalDateTime localDate = LocalDateTime.now();

        Person person1 = new Person("nick_name", "1121212", localDate, "sdfffsfsdf");
        personService.savePerson(person1);
        Contact contact = new Contact( 1L ,"lol", "lol", "+2312312313", person1);
        contactDao.save(contact);
         Office office = new Office( "werewr", address1);
        System.out.println(office);
         officeDao.save(office);
         officeDao.findOne(1L);
         officeDao.contains(1L);
        contactDao.save(contact);
        contactDao.findOne(1L);
        System.out.println(contact);

        return "test";
    }


    @GetMapping("/error/403")
    public String error403() {
        return "/error/403";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/employee")
    public String admin() {
        return "employee";
    }

}
