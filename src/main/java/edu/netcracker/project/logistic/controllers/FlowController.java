package edu.netcracker.project.logistic.controllers;

import edu.com.google.maps.model.LatLng;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.flow.impl.RadiusSelector;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.EmployeeService;
import edu.netcracker.project.logistic.service.OfficeService;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = "/flow")
public class FlowController {
    private PersonService personService;
    private OfficeService officeService;
    private RoleService roleService;
    private EmployeeService employeeService;

    @Autowired
    public FlowController(PersonService personService, OfficeService officeService, RoleService roleService, EmployeeService employeeService) {
        this.personService = personService;
        this.officeService = officeService;
        this.roleService = roleService;
        this.employeeService = employeeService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        Office office = new Office();
        office.setOfficeId((long)(404));
        office.setName("FlowTestOffice");
        office.setAddress(new Address((long)(404), new LatLng(50.420506, 30.529369)));//our NetCrackerOffice

        class OrderGenerator{
            private static final double radius = 0.400;
            public List<Order> generate(int count){
                List<Order> return_value = new ArrayList<>(count);
                for(int i = 0; i < count; i++){
                    Order o = new Order();
                    o.setId((long)(404+i));
                    o.setCreationDay(LocalDate.now());
                    o.setCourier(null);
                    o.setReceiverAddress(new Address((long)(604+i),new LatLng(
                            Math.random()*radius+office.getAddress().getLocation().lat,
                            Math.random()*radius+office.getAddress().getLocation().lng
                    )));
                    o.setSenderAddress(office.getAddress());
                    o.setOffice(office);
                    o.setOrderStatus(new OrderStatus((long)(1),"testing"));
                    o.setReceiverContact(new Contact());
                    o.setSenderContact(new Contact());

                    return_value.add(o);
                }

                return return_value;
            }
        }

        System.out.println("Begin flow building");
        FlowBuilder fb = new RadiusSelector(roleService, office);
        fb.add(new OrderGenerator().generate(10), FlowBuilder.OrderType.Luggage);
        fb.add(new OrderGenerator().generate(10), FlowBuilder.OrderType.Freight);

        Set<Role> couriers = new HashSet<>();
        //TODO: priority???
        couriers.add(new Role((long)(5), "ROLE_COURIER", "NULL"));
        fb.add(new Person("Courier#1", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#2", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);
        fb.add(new Person("Courier#3", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#4", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);
        fb.optimize(true);
        if(!fb.process()) {
            System.out.println(fb.getError());
            return "redirect:error/400";
        }

        try {
            System.out.println("End of flow building.");
            return "redirect:"+fb.getStaticMap().toURL().toString();
        } catch (MalformedURLException e) {
            return "redirect:error/300";
        }
    }
}
