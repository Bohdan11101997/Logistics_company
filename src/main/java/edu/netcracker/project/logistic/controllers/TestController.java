package edu.netcracker.project.logistic.controllers;

import com.google.maps.model.GeocodingResult;
import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.dao.OfficeDao;
import edu.netcracker.project.logistic.dao.impl.AddressDaoImpl;
import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Pager;
import edu.netcracker.project.logistic.service.AddressService;
import edu.netcracker.project.logistic.service.AdvertisementService;
import edu.netcracker.project.logistic.service.PersonService;
import edu.netcracker.project.logistic.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
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

    Address address;


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
        Address address = new Address("Київ, Академіка Янгеля, 5");
        Address address1 = new Address((long) 3, "erer");
        addressService.save(address);
        addressService.save(address1);

        System.out.println(address.check("qwqw"));

        for(GeocodingResult gr : Address.getListOfAddresses(address.getName()))
            System.out.println(gr.formattedAddress);
        System.out.println("===========================================");
        for(GeocodingResult gr : Address.getListOfAddresses(address1.getLocation()))
            System.out.println(gr.formattedAddress);
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

    private static final int INITIAL_PAGE_SIZE = 20;
    private static final int INITIAL_PAGE = 0;
    private static final int[] PAGE_SIZES = {5, 10, 20};

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping("/pagination")
    public String viewPagination(@RequestParam("pageSize")Optional<Integer> pageSize,
                                 @RequestParam("page") Optional<Integer> page){
        int itemsOnPage = pageSize.orElse(INITIAL_PAGE_SIZE);
        int currentPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get()-1;

        int allAdvertisementsNumber = advertisementService.getNumberOfAllAdvertisements();

        List<Advertisement> advertisementsForCurrentPage = advertisementService.findAllOnPage(itemsOnPage, currentPage);

        return "test_advertisements";
    }

}
