package edu.netcracker.project.logistic.controllers;

import com.google.maps.model.GeocodingResult;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.*;

import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.model.AdvertisementType;
import edu.netcracker.project.logistic.model.Office;

import edu.netcracker.project.logistic.service.AdvertisementService;

import edu.netcracker.project.logistic.validation.AdvertisementValidator;
import edu.netcracker.project.logistic.validation.EmployeeValidator;
import edu.netcracker.project.logistic.validation.ImageValidator;
import edu.netcracker.project.logistic.validation.SearchFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.concurrent.ForkJoinPool.commonPool;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private static final int INITIAL_PAGE_SIZE = 20;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int[] PAGE_SIZES = {5, 10, 20, 50};

    private EmployeeService employeeService;
    private OfficeService officeService;
    private RoleService roleService;
    private AdvertisementService advertisementService;
    private AddressService addressService;
    private EmployeeValidator employeeValidator;
    private AdvertisementValidator advertisementValidator;
    private SearchFormValidator searchFormValidator;
    private UserDetailsService userDetailsService;
    private ImageValidator imageValidator;


    @Autowired
    public AdminController(OfficeService officeService, EmployeeService employeeService,
                           RoleService roleService, AdvertisementService advertisementService,
                           AddressService addressService, EmployeeValidator employeeValidator,
                           AdvertisementValidator advertisementValidator, SearchFormValidator searchFormValidator,
                           UserDetailsService userDetailsService, ImageValidator imageValidator) {
        this.officeService = officeService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.advertisementService = advertisementService;
        this.addressService = addressService;
        this.employeeValidator = employeeValidator;
        this.advertisementValidator = advertisementValidator;
        this.searchFormValidator = searchFormValidator;
        this.userDetailsService = userDetailsService;
        this.imageValidator = imageValidator;
    }

    @GetMapping("/crud/advertisement")
    public String crateAdvertisementForm(Model model) {
        AdvertisementForm advertisementForm = new AdvertisementForm();
        model.addAttribute("advertisement", advertisementForm);
        return "/admin/admin_crud_advertisement";
    }

    @PostMapping("/crud/advertisement")
    public String publishAdvertisement(@Valid @ModelAttribute(value = "advertisement") AdvertisementForm advertisementForm,
                                       @RequestParam("file")MultipartFile file,
                                       Model model,
                                       BindingResult bindingResult) {

        advertisementValidator.validate(advertisementForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/admin/admin_crud_advertisement";
        }

        if (!imageValidator.isImage(file)){
            model.addAttribute("notImage", "Please, upload only image there!");
            return "/admin/admin_crud_advertisement";
        }

        Advertisement advertisement = new Advertisement();
        advertisement.setCaption(advertisementForm.getCaption());
        advertisement.setDescription(advertisementForm.getDescription());
        advertisement.setShowFirstDate(advertisementForm.getShowFirstDate());
        advertisement.setShowEndDate(advertisementForm.getShowEndDate());
        AdvertisementType advertisementType = new AdvertisementType();
        advertisementType.setName(advertisementForm.getType());
        advertisement.setType(advertisementType);

        advertisementService.save(advertisement);
        return "redirect:/admin/crud/advertisement?success";
    }

    @GetMapping("/crud/advertisement/update/{id}")
    public String showAdvertisementData(@PathVariable long id, Model model) {
        AdvertisementForm advertisementForm = new AdvertisementForm();

        Optional<Advertisement> advertisementOptional = advertisementService.findOne(id);
        if (!advertisementOptional.isPresent()) {
            return "redirect:/error/404";
        }

        Advertisement advertisement = advertisementOptional.get();
        advertisementForm.setId(advertisement.getId());
        advertisementForm.setCaption(advertisement.getCaption());
        advertisementForm.setDescription(advertisement.getDescription());
        advertisementForm.setShowFirstDate(advertisement.getShowFirstDate());
        advertisementForm.setShowEndDate(advertisement.getShowEndDate());
        advertisementForm.setType(advertisement.getType().getName());

        model.addAttribute("advertisement", advertisementForm);
        model.addAttribute("update", true);
        return "/admin/admin_crud_advertisement";
    }

    @PostMapping("/crud/advertisement/update/{id}")
    public String updateAdvertisement(@PathVariable long id,
                                      @ModelAttribute(value = "advertisement") AdvertisementForm advertisementForm,
                                      BindingResult bindingResult) {

        advertisementValidator.validate(advertisementForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/admin/admin_crud_advertisement";
        }

        Optional<Advertisement> advertisementOptional = advertisementService.findOne(id);
        if (!advertisementOptional.isPresent()) {
            return "redirect:/error/404";
        }

        Advertisement advertisement = advertisementOptional.get();
        advertisement.setCaption(advertisementForm.getCaption());
        advertisement.setDescription(advertisementForm.getDescription());
        advertisement.setShowFirstDate(advertisementForm.getShowFirstDate());
        advertisement.setShowEndDate(advertisementForm.getShowEndDate());
        advertisement.getType().setName(advertisementForm.getType());
        advertisementService.update(advertisement);

        return "redirect:/admin/advertisements?update=success";
    }

    @PostMapping("/crud/advertisement/delete/{id}")
    public String deleteAdvertisement(@PathVariable long id) {
        advertisementService.delete(id);
        return "redirect:/admin/advertisements?delete=success";
    }

    @GetMapping("/advertisements")
    public String getAllAdvertisementsOnPage(@RequestParam("pageSize")Optional<Integer> pageSize,
                                             @RequestParam("page") Optional<Integer> page,
                                             Model model){

        int itemsOnPage = pageSize.orElse(INITIAL_PAGE_SIZE);
        int currentPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get()-1;
        int allAdvertisementsCount = advertisementService.getCountOfAllAdvertisements();
        List<Advertisement> advertisementsForCurrentPage = advertisementService.findAmountOfAdvertisementsForCurrentPage(itemsOnPage, currentPage);

        int totalPages = allAdvertisementsCount/itemsOnPage;
        Pager pager = new Pager(totalPages, currentPage, BUTTONS_TO_SHOW);

        model.addAttribute("advertisements", advertisementsForCurrentPage);
        model.addAttribute("selectedPageSize", itemsOnPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSizes", PAGE_SIZES);
        model.addAttribute("pager", pager);

        return "/admin/admin_advertisements";

    }


    @GetMapping("/offices")
    public String getAllOffice(Model model) {
        model.addAttribute("offices", officeService.allOffices());
        return "/admin/admin_offices";
    }

    @GetMapping("/employees")
    public String getAllEmployees(Model model) {
        List<Person> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("availableRoles", roleService.findEmployeeRoles());
        model.addAttribute("searchForm", new SearchForm());
        return "admin/admin_employees";
    }

    @PostMapping("/employees")
    public String searchEmployees(Model model, @ModelAttribute("searchForm") SearchForm searchForm, BindingResult result) {
        searchFormValidator.validate(searchForm, result);
        if (result.hasErrors()) {
            model.addAttribute("availableRoles", roleService.findEmployeeRoles());
            return "admin/admin_employees";
        }
        List<Person> employees = employeeService.search(searchForm);
        model.addAttribute("employees", employees);
        model.addAttribute("availableRoles", roleService.findEmployeeRoles());
        model.addAttribute("searchForm", searchForm);
        return "admin/admin_employees";
    }

    @GetMapping("/crud/employee/{id}")
    public String employeeProfile(@PathVariable long id, Model model) {
        Optional<Person> opt = employeeService.findOne(id);
        if (!opt.isPresent()) {
            return "redirect:/error/404";
        }
        Person emp = opt.get();
        List<Role> employeeRoles = roleService.findEmployeeRoles();

        model.addAttribute("newEmployee", false);
        model.addAttribute("employee", emp);
        model.addAttribute("availableRoles", employeeRoles);


        return "/admin/admin_crud_employee";
    }

    @PostMapping("/crud/employee/{id}")
    public String updateEmployee(@PathVariable long id, Model model,
                                 @ModelAttribute("employee") Person employee,
                                 BindingResult result, Principal principal) {
        Person person = employeeService.findOne(employee.getId())
                .orElseThrow(IllegalStateException::new);
        employee.getContact().setContactId(person.getContact().getContactId());
        employee.setRegistrationDate(person.getRegistrationDate());
        employee.setId(id);
        employeeValidator.validateUpdateData(employee, result);
        Optional<Person> opt = employeeService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person account = opt.get();
        boolean adminRoleLeft =
                employee.getRoles().stream().anyMatch(r -> r.getRoleName().equals("ROLE_ADMIN"));
        if (account.getId().equals(id) && !adminRoleLeft) {
            model.addAttribute("message", "Can't remove admin role from own account");
            return "error/400";
        }
        if (result.hasErrors()) {
            List<Role> employeeRoles = roleService.findEmployeeRoles();
            model.addAttribute("newEmployee", false);
            model.addAttribute("availableRoles", employeeRoles);
            return "/admin/admin_crud_employee";
        }
        employee.setId(id);
        employeeService.update(employee);
        return "redirect:/admin/employees";
    }

    @PostMapping("/crud/employee/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, Model model, Principal principal) {
        Optional<Person> opt = employeeService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        if (opt.get().getId().equals(id)) {
            model.addAttribute("message", "Can't remove own admin account");
            return "error/400";
        }
        employeeService.delete(id);
        return "redirect:/admin/employees";
    }

    @GetMapping("/crud/employee")
    public String createEmployee(Model model) {
        Person employee = new Person();
        employee.setContact(new Contact());
        employee.setRoles(new HashSet<>());

        List<Role> availableRoles = roleService.findEmployeeRoles();

        model.addAttribute("employee", employee);
        model.addAttribute("newEmployee", true);
        model.addAttribute("availableRoles", availableRoles);

        return "/admin/admin_crud_employee";
    }

    @PostMapping("/crud/employee")
    public String doCreateEmployee(Model model,
                                   @ModelAttribute("employee") Person employee,
                                   BindingResult bindingResult) {

        String temporaryPassword = generateRandomPasswordWithSpecifiedLength(12);
        employee.setPassword(temporaryPassword);

        employeeValidator.validateCreateData(employee, bindingResult);
        if (bindingResult.hasErrors()) {
            List<Role> availableRoles = roleService.findEmployeeRoles();
            model.addAttribute("newEmployee", true);
            model.addAttribute("availableRoles", availableRoles);
            return "/admin/admin_crud_employee";
        }

        try {
            employeeService.create(employee);
        } catch (MessagingException e) {
            logger.error("Exception caught when sending confirmation mail", e);
        }

        return "redirect:/admin/employees";
    }

    private String generateRandomPasswordWithSpecifiedLength(int passwordLength){

        final String allowedSymbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < passwordLength; i++ )
            sb.append( allowedSymbols.charAt( rnd.nextInt(allowedSymbols.length()) ) );
        return sb.toString();
    }

    @GetMapping("/crud/office")
    public String createOffice(Model model) {
        model.addAttribute("office", new Office());
        return "/admin/admin_crud_office";
    }


    @GetMapping("/office/update/{id}")
    public String updateOffice(@PathVariable long id, Model model) {
        model.addAttribute("office", officeService.findOne(id));
        return "/admin/admin_crud_office";
    }

    @GetMapping("/office/delete/{id}")
    public String deleteOffice(@PathVariable Long id) {

        officeService.delete(id);
        return "redirect:/admin/offices";
    }

    @PostMapping("/crud/office")
    public String saveOffice(@ModelAttribute("office") Office office) {
        Optional<Address> opt = addressService.findOne(office.getAddress().getName());
        if (opt.isPresent()) {
            return "error/500";
        }
        addressService.save(office.getAddress());
        addressService.findOne(office.getAddress().getName()).get();
        System.out.println(office);
        for(GeocodingResult gr : Address.getListOfAddresses(office.getAddress().getLocation()))
            System.out.println(gr.formattedAddress);
        officeService.save(office);
        return "redirect:/admin/offices";
    }


    @PostMapping("/FindOfficeByDepartment")
    public String findByDepartment(@RequestParam String department, Model model) {
        model.addAttribute("offices", officeService.findByDepartment(department));
        return "/admin/admin_offices";

    }
}
