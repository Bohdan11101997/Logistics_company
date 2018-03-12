package edu.netcracker.project.logistic.controllers;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.flow.impl.RadiusSelector;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.EmployeeService;
import edu.netcracker.project.logistic.service.OfficeService;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = "/flow")
public class FlowController {
    private final Logger logger = LoggerFactory.getLogger(FlowController.class);

    private PersonService personService;
    private OfficeService officeService;
    private RoleService roleService;
    private EmployeeService employeeService;
    private OrderTypeDao orderTypeDao;


    @Autowired
    public FlowController(PersonService personService, OfficeService officeService, RoleService roleService, EmployeeService employeeService, OrderTypeDao orderTypeDao) {
        this.personService = personService;
        this.officeService = officeService;
        this.roleService = roleService;
        this.employeeService = employeeService;
        this.orderTypeDao = orderTypeDao;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        Office office = new Office();
        office.setOfficeId((long)(404));
        office.setName("FlowTestOffice");
        office.setAddress(new Address((long)(404), new LatLng(50.420506, 30.529369)));//our NetCrackerOffice

        class OrderGenerator{
            private static final double radius = 0.125;//in map values - degrees
            private TravelMode travelMode = TravelMode.WALKING;
            public static final int TRY_ITERATIONS = 3;

            private List<OrderType> orderTypes = orderTypeDao.findAll();

            public OrderGenerator setTravelMode(TravelMode travelMode){
                this.travelMode = travelMode;
                return this;
            }

            public List<Order> generate(int count){
                List<Order> return_value = new ArrayList<>(count);
                for(int i = 0; i < count; i++) {
                    Order o = new Order();
                    o.setId((long) (404 + i));
                    o.setCreationTime(LocalDateTime.now());
                    o.setCourier(null);

                    Address address = new Address((long) (604 + i), new LatLng(
                            (Math.random() * 2 - 1) * radius + office.getAddress().getLocation().lat,
                            (Math.random() * 2 - 1) * radius + office.getAddress().getLocation().lng));
                    for (int iter = 0; iter < TRY_ITERATIONS && !address.check(office.getAddress(),travelMode); iter++){
                        logger.warn("Address wasn't resolved");
                        address = new Address((long) (604 + i), new LatLng(
                                address.getLocation().lat+(Math.random() * 2.0/(iter+1) - 1.0/(iter+1))
                                        * radius/(iter+1) + office.getAddress().getLocation().lat,
                                address.getLocation().lng+(Math.random() * 2.0/(iter+1) - 1.0/(iter+1))
                                        * radius/(iter+1) + office.getAddress().getLocation().lng));
                    }
                    o.setReceiverAddress(address);
                    o.setSenderAddress(office.getAddress());
                    o.setOffice(office);
                    o.setOrderStatus(new OrderStatus((long) (1), "testing"));
                    o.setReceiverContact(new Contact());
                    o.setSenderContact(new Contact());
                    o.setOrderType(orderTypes.get((int) Math.random()*orderTypes.size()));
                    return_value.add(o);
                }

                return return_value;
            }
        }

        FlowBuilder fb = new RadiusSelector(roleService, orderTypeDao, office);
        fb.add(new OrderGenerator().setTravelMode(TravelMode.WALKING).generate(10));
        fb.add(new OrderGenerator().setTravelMode(TravelMode.DRIVING).generate(10));

        Set<Role> couriers = new HashSet<>();
        //TODO: priority???
        couriers.add(new Role((long)(5), "ROLE_COURIER", "NULL"));
        fb.add(new Person("Courier#1", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#2", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);
        fb.add(new Person("Courier#3", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#4", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);
        fb.optimize(true);

        logger.info("Begin flow building");
        while(!fb.isFinished()) {
            if (!fb.process()) {
                logger.error(fb.getError());
                //TODO:change
                fb.add(fb.getOrders());//return orders back
            }

            try {
                logger.info("New route finished;");
                logger.info("Total distance: " + fb.getDistance() + "m");
                logger.info("Total duration: " + fb.getDuration() * 1.0 / 60 / 60 + "h");
                logger.info("Map url: " + fb.getStaticMap().toURL().toString(), fb.getStaticMap().toURL());
               // return "redirect:" + fb.getStaticMap().toURL().toString();
            } catch (MalformedURLException e) {
                logger.error(e.getMessage());
                //return "redirect:error/300";
            }
        }
        logger.info("End of flow building.");
        return "";
    }
}
