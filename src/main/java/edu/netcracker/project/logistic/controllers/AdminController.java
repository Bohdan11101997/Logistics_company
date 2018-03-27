package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.OfficeDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.*;

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
import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private EmployeeService employeeService;
    private OfficeService officeService;
    private RoleService roleService;
    private AdvertisementService advertisementService;
    private AddressService addressService;
    private EmployeeValidator employeeValidator;
    private AdvertisementValidator advertisementValidator;
    private SearchFormValidator searchFormValidator;
    private ImageValidator imageValidator;
    private OfficeDao officeDao;


    @Autowired
    public AdminController(OfficeService officeService, EmployeeService employeeService,
                           RoleService roleService, AdvertisementService advertisementService,
                           AddressService addressService, EmployeeValidator employeeValidator,
                           AdvertisementValidator advertisementValidator, SearchFormValidator searchFormValidator,
                           ImageValidator imageValidator, OfficeDao officeDao) {
        this.officeService = officeService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.advertisementService = advertisementService;
        this.addressService = addressService;
        this.employeeValidator = employeeValidator;
        this.advertisementValidator = advertisementValidator;
        this.searchFormValidator = searchFormValidator;
        this.imageValidator = imageValidator;
        this.officeDao = officeDao;
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
                                       BindingResult bindingResult) throws IOException {

        advertisementValidator.validate(advertisementForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/admin/admin_crud_advertisement";
        }

        if (!imageValidator.isImage(file)){
            model.addAttribute("notImage", "Please, upload image there!");
            return "/admin/admin_crud_advertisement";
        }

        Advertisement advertisement = new Advertisement();
        advertisement.setCaption(advertisementForm.getCaption());
        advertisement.setDescription(advertisementForm.getDescription());
        advertisement.setShowFirstDate(advertisementForm.getShowFirstDate());
        advertisement.setShowEndDate(advertisementForm.getShowEndDate());
        byte[] image = file.getBytes();
        advertisement.setImage(image);
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
                                      @RequestParam("file")MultipartFile file,
                                      Model model,
                                      BindingResult bindingResult) throws IOException {

        advertisementValidator.validate(advertisementForm, bindingResult);

        model.addAttribute("update", true);
        if (bindingResult.hasErrors()) {
            return "/admin/admin_crud_advertisement";
        }

        Optional<Advertisement> advertisementOptional = advertisementService.findOne(id);
        if (!advertisementOptional.isPresent()) {
            return "redirect:/error/404";
        }

        if (!file.getOriginalFilename().equals("") && !imageValidator.isImage(file)){
            System.out.println(file.getOriginalFilename());
            model.addAttribute("notImage", "Please, upload image there!");
            return "/admin/admin_crud_advertisement";
        }

        Advertisement advertisement = advertisementOptional.get();
        advertisement.setCaption(advertisementForm.getCaption());
        advertisement.setDescription(advertisementForm.getDescription());
        advertisement.setShowFirstDate(advertisementForm.getShowFirstDate());
        advertisement.setShowEndDate(advertisementForm.getShowEndDate());
        if (!file.getOriginalFilename().equals("")){
            byte[] image = file.getBytes();
            advertisement.setImage(image);
        }
        advertisement.getType().setName(advertisementForm.getType());
        advertisementService.save(advertisement);

        return "redirect:/admin/advertisements?update=success";
    }

    @PostMapping("/crud/advertisement/delete/{id}")
    public String deleteAdvertisement(@PathVariable long id) {
        advertisementService.delete(id);
        return "redirect:/admin/advertisements?delete=success";
    }

    @GetMapping("/advertisements")
    public String getAllAdvertisementsOnPage(@RequestParam("itemsOnPage")Optional<Integer> itemsOnPage,
                                             @RequestParam("currentPage") Optional<Integer> currentPage,
                                             @RequestParam("buttonsToShow") Optional<Integer> buttonsToShow,
                                             Model model){

        int allAdvertisementsCount = advertisementService.getCountOfAllAdvertisements();
        Pager pager = new Pager(allAdvertisementsCount, itemsOnPage, currentPage, buttonsToShow);

        List<Advertisement> advertisementsForCurrentPage = advertisementService.findAmountOfAdvertisementsForCurrentPage(pager.getItemsOnPage(), pager.getCurrentPage());

        model.addAttribute("advertisements", advertisementsForCurrentPage);
        model.addAttribute("pager", pager);

        return "/admin/admin_advertisements";

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
    public String getUpdateOffice(@PathVariable long id, Model model) {
        model.addAttribute("office", officeService.findOne(id));
        return "/admin/admin_update_office";
    }

    @PostMapping("/office/update/{id}")
    public String updateOffice(@PathVariable long id, Model model, @ModelAttribute("office") Office office ) {
        model.addAttribute("office", officeService.findOne(id));
        System.out.println(id);
        office.setOfficeId(id);
        System.out.println(office.getOfficeId());
        addressService.save(office.getAddress());
        officeService.save(office);
        return "redirect:/admin/offices ";
    }

    @GetMapping("/office/delete/{id}")
    public String deleteOffice(@PathVariable Long id) {

        officeService.delete(id);
        return "redirect:/admin/offices";
    }

    @PostMapping("/crud/office")
    public String saveOffice(@ModelAttribute("office") Office office) {
        //        Optional<Address> opt = addressService.findOne(office.getAddress().getName());
        addressService.save(office.getAddress());
        officeService.save(office);
        return "redirect:/admin/offices";
    }

    @GetMapping("/offices")
    public String getAllOffice(Model model) {
        model.addAttribute("offices", officeService.allOffices());
        model.addAttribute("officeSearchForm", new OfficeSearchForm());
        return "/admin/admin_offices";
    }

    @PostMapping("/offices")
    public String findByDepartmentOrAddress( @ModelAttribute("officeSearchForm") OfficeSearchForm officeSearchForm,  Model model) {
        model.addAttribute("offices", officeDao.findByDepartmentOrAddress(officeSearchForm));
        return "/admin/admin_offices";

    }
}
