package edu.netcracker.project.logistic.controllers;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.flow.FlowBuilder;
import edu.netcracker.project.logistic.flow.impl.RadiusSelector;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
    private OrderDao orderDao;
    private CourierDataDao courierDataDao;

    private OrderGenerator orderGenWalks = null;
    private OrderGenerator orderGenDrives = null;


    @Autowired
    public FlowController(PersonService personService,
                          OfficeService officeService,
                          RoleService roleService,
                          EmployeeService employeeService,
                          OrderTypeDao orderTypeDao,
                          OrderDao orderDao,
                          CourierDataDao courierDataDao) {
        this.personService = personService;
        this.officeService = officeService;
        this.roleService = roleService;
        this.employeeService = employeeService;
        this.orderTypeDao = orderTypeDao;
        this.orderDao = orderDao;
        this.courierDataDao = courierDataDao;
    }

    @RequestMapping(value = "/route", method = RequestMethod.GET)
    public String generateRoutes(){
        for(Office office : officeService.allOffices()) {
            List<Order>  global = orderDao.findNotProcessed();
            List<Person> couriers = employeeService.findCouriers();
            List<Order> local = new ArrayList<>();
            for(Order o : global){
                if(o.getOffice() == office)
                    local.add(o);
                else if(o.getOffice() == null && FlowBuilder.distance(
                        office.getAddress().getLocation(),
                        o.getReceiverAddress().getLocation(),
                        true, TravelMode.DRIVING) < 9999.9999);//TODO: add finding the closest one
                    local.add(o);
            }

            FlowBuilder fb = new RadiusSelector(roleService, orderTypeDao, courierDataDao, office)
                    .optimize(true)
                    .setUseMapRequests(true);
            fb.add(local);
            for(Person p : couriers) {
                CourierData cd = courierDataDao.findOne(p).get();
                if (cd != null)
                    if(cd.getCourierStatus().name() == "FREE")
                    fb.add(p, cd.getTravelMode() == TravelMode.DRIVING ? FlowBuilder.CourierType.Driver : FlowBuilder.CourierType.Walker);
            }

            logger.info("Begin flow building");
            while (!fb.isFinished()) {
                if (!fb.process()) {
                    logger.error(fb.getError());
                    //global.add(fb.getUnused());//return unpicked orders back
                } else {
                    try {
                        System.out.print(" \b");
                        logger.info("New route finished;");
                        logger.info("Total distance: " + fb.getDistance() + "m");
                        logger.info("Total duration: " + fb.getDuration() * 1.0 / 60 / 60 + "h");
                        logger.info("Map url: " + fb.getStaticMap().toURL().toString());
                        // return "redirect:" + fb.getStaticMap().toURL().toString();
                    } catch (MalformedURLException e) {
                        logger.error(e.getMessage());
                        //return "redirect:error/300";
                    }
                }
            }
            logger.info("End of flow building.");
        }
        return "/flow";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        Office office = new Office();
        office.setOfficeId((long) (404));
        office.setName("FlowTestOffice");
        office.setAddress(new Address((long) (404),
                new LatLng(50.420506, 30.529369)));//our NetCrackerOffice
        if (orderGenWalks == null)
            orderGenWalks = new OrderGenerator(office).setTravelMode(TravelMode.WALKING);
        if (orderGenDrives == null)
            orderGenDrives = new OrderGenerator(office).setTravelMode(TravelMode.DRIVING);

        FlowBuilder fb = new RadiusSelector(roleService, orderTypeDao, courierDataDao, office)
                .optimize(true)
                .setUseMapRequests(false);
        fb.add(orderGenWalks.generate(10));
        fb.add(orderGenDrives.generate(10));

        Set<Role> couriers = new HashSet<>();
        couriers.add(new Role((long) (7), "ROLE_COURIER", null));

        fb.add(new Person("Courier#1", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#2", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);
        fb.add(new Person("Courier#3", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#4", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);

        logger.info("Begin flow building");
        while (!fb.isFinished()) {
            if (!fb.process()) {
                logger.error(fb.getError());

            //fb.add(fb.getUnused());//return unpicked orders back
            }
            else {
                try {
                    System.out.print("");
                    logger.info("New route finished;");
                    logger.info("Total distance: " + fb.getDistance() + "m");
                    URI uri = null;
                    try {
                        uri = fb.getStaticMap().toURL().toURI();
                    } catch (URISyntaxException e) {
                        logger.warn(e.getMessage());
                    }
                    logger.info("Total duration: " + fb.getDuration() * 1.0 / 60 / 60 + "h");
                    logger.info("Map url: " + uri.toASCIIString() + "\t", fb.getStaticMap().toURL());
                    // return "redirect:" + fb.getStaticMap().toURL().toString();
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage());
                    //return "redirect:error/300";
                }
            }
        }
        logger.info("End of flow building.");
        return "/flow";
    }

    @RequestMapping(value = "/testOld", method = RequestMethod.GET)
    public String testOld() {
        Office office = new Office();
        office.setOfficeId((long) (404));
        office.setName("FlowTestOffice");
        office.setAddress(new Address((long) (404),
                new LatLng(50.420506, 30.529369)));//our NetCrackerOffice

        if (orderGenWalks == null)
            orderGenWalks = new OrderGenerator(office).setTravelMode(TravelMode.WALKING);
        if (orderGenDrives == null)
            orderGenDrives = new OrderGenerator(office).setTravelMode(TravelMode.DRIVING);

        logger.info("Begin flow building");
        FlowBuilder fb = new RadiusSelector(roleService, orderTypeDao, courierDataDao, office)
                .optimize(true)
                .setUseMapRequests(false);
        fb.add(orderGenWalks.generate(10));
        fb.add(orderGenDrives.generate(10));

        Set<Role> couriers = new HashSet<>();
        couriers.add(new Role((long) (7), "ROLE_COURIER", null));

        fb.add(new Person("Courier#1", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#2", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);
        fb.add(new Person("Courier#3", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Walker);
        fb.add(new Person("Courier#4", "loggedin", LocalDateTime.now(), new Contact(), couriers), FlowBuilder.CourierType.Driver);

        if (!fb.process()) {
            logger.error(fb.getError());
            logger.info(Arrays.toString(fb.getOrders().toArray()));
            return "redirect:error/400";
        }

        try {
            logger.info("End of flow building.");
            logger.info("Total distance: " + fb.getDistance() + "m");
            logger.info("Total duration: " + fb.getDuration() * 1.0 / 60 / 60 + "h");
            return "redirect:" + fb.getStaticMap().toURL().toString();
        } catch (MalformedURLException e) {
            return "redirect:error/300";
        }
    }


    @RequestMapping(value = "/testNewSequence", method = RequestMethod.GET)
    public String testNewSequence(){
        orderGenWalks = null;
        orderGenDrives = null;
        return "redirect:/flow";
    }

    class OrderGenerator {
        private static final double radius = 0.125;//in map values - degrees
        private TravelMode travelMode = TravelMode.WALKING;
        public static final int TRY_ITERATIONS = 2;

        private Office office;
        private List<Order> sequence;

        private List<OrderType> orderTypes = orderTypeDao.findAll();

        public OrderGenerator(Office office) {
            this.office = office;
        }

        public OrderGenerator setTravelMode(TravelMode travelMode) {
            this.travelMode = travelMode;
            return this;
        }

        public List<Order> generate(int count) {
            return generate(count, false);
        }

        public List<Order> generate(int count, boolean ignoreOld) {
            if (sequence != null && !ignoreOld) {
                if (sequence.size() == count)
                    return sequence;
                else {
                    if (sequence.size() > count)
                        return sequence.subList(0, count);
                    else
                        sequence.addAll(generate(count - sequence.size(), true));
                }
            }

            sequence = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                Order o = new Order();
                o.setId((long) (404 + i));
                o.setCreationTime(LocalDateTime.now());
                o.setCourier(null);
                {
                    LatLng coord = new LatLng(
                            (Math.random() * 2 - 1) * radius + office.getAddress().getLocation().lat,
                            (Math.random() * 2 - 1) * radius + office.getAddress().getLocation().lng);
                    Address address = new Address((long) (604 + i), coord);
                    for (int iter = 0; iter < TRY_ITERATIONS && !address.check(office.getAddress(), travelMode); iter++) {
                        logger.warn("Address wasn't resolved");
                        coord = new LatLng(
                                (Math.random() * 2 - 1) * radius + office.getAddress().getLocation().lat,
                                (Math.random() * 2 - 1) * radius + office.getAddress().getLocation().lng);
                        address = new Address((long) (604 + i), coord);
                    }
                    //finally
                    o.setReceiverAddress(address);
                }
                o.setSenderAddress(office.getAddress());
                o.setOffice(office);
                o.setOrderStatus(new OrderStatus((long) (-1), "CONFIRMED"));
                o.setReceiverContact(new Contact());
                o.setSenderContact(new Contact());
                o.setOrderType(orderTypes.get(travelMode == TravelMode.DRIVING ? 2 : 1));
                //
                o.setWeight(o.getOrderType().getId() == 1 ? BigDecimal.valueOf(Math.random() * 0.5) :
                        (o.getOrderType().getId() == 2 ? BigDecimal.valueOf(Math.random() * 8) : BigDecimal.valueOf(Math.random() * 50)));
                //TODO: change to debug
                logger.info("Generated Order #" + i + ": " + o);
                sequence.add(o);
            }

            return sequence;
        }
    }
}
