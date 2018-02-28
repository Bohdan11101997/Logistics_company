package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.*;

import edu.netcracker.project.logistic.model.Advertisement;
import edu.netcracker.project.logistic.model.AdvertisementType;
import edu.netcracker.project.logistic.model.Office;

import edu.netcracker.project.logistic.service.AdvertisementService;

import edu.netcracker.project.logistic.validation.EmployeeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private EmployeeService employeeService;
    private OfficeService officeService;
    private RoleService roleService;
    private AdvertisementService advertisementService;
    private AddressService addressService;
    private EmployeeValidator employeeValidator;
    private UserDetailsService userDetailsService;


    @Autowired
    public AdminController(OfficeService officeService, EmployeeService employeeService,
                           RoleService roleService, AdvertisementService advertisementService,
                           AddressService addressService,
                           EmployeeValidator employeeValidator, UserDetailsService userDetailsService) {
        this.officeService = officeService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.advertisementService = advertisementService;
        this.addressService = addressService;
        this.employeeValidator = employeeValidator;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/crud/advertisement")
    public String crateAdvertisementForm(Model model) {
        AdvertisementForm advertisementForm = new AdvertisementForm();
        model.addAttribute("advertisement", advertisementForm);
        return "/admin/admin_crud_advertisement";
    }

    @PostMapping("/crud/advertisement")
    public String publishAdvertisement(@ModelAttribute(value = "advertisement") AdvertisementForm advertisementForm) {

        Advertisement advertisement = new Advertisement();
        advertisement.setCaption(advertisementForm.getCaption());
        advertisement.setDescription(advertisementForm.getDescription());
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
        advertisementForm.setType(advertisement.getType().getName());

        model.addAttribute("advertisement", advertisementForm);
        model.addAttribute("update", true);
        return "/admin/admin_crud_advertisement";
    }

    @PostMapping("/crud/advertisement/update/{id}")
    public String updateAdvertisement(@PathVariable long id,
                                      @ModelAttribute(value = "advertisement") AdvertisementForm advertisementForm) {

        Optional<Advertisement> advertisementOptional = advertisementService.findOne(id);
        if (!advertisementOptional.isPresent()) {
            return "redirect:/error/404";
        }

        Advertisement advertisement = advertisementOptional.get();
        advertisement.setCaption(advertisementForm.getCaption());
        advertisement.setDescription(advertisementForm.getDescription());
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
    public String getAllAdvertisements(Model model) {
        model.addAttribute("advertisements", advertisementService.findAll());
        return "/admin/admin_advertisements";
    }


    @GetMapping("/offices")
    public String getAllOffice(Model model) {
        model.addAttribute("offices", officeService.allOffices());
        return "/admin/admin_offices";
    }

    @GetMapping("/employees")
    public String getAllEmployees(Model model, @ModelAttribute("searchForm") SearchForm searchForm) {
        List<Person> employees = employeeService.search(searchForm);
        model.addAttribute("employees", employees);
        model.addAttribute("availableRoles", roleService.findEmployeeRoles());
        return "/admin/admin_employees";
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
                                 BindingResult result) {
        employee.setId(id);
        employeeValidator.validateUpdateData(employee, result);
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
        employeeValidator.validateCreateData(employee, bindingResult);
        if (bindingResult.hasErrors()) {
            List<Role> availableRoles = roleService.findEmployeeRoles();
            model.addAttribute("newEmployee", true);
            model.addAttribute("availableRoles", availableRoles);

            return "/admin/admin_crud_employee";
        }
        employeeService.create(employee);
        return "redirect:/admin/employees";
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
    public String saveOffice(@ModelAttribute("office") OfficeForm officeForm) {

        Office office = new Office(
                officeForm.getOfficeId(),
                officeForm.getName(),
                addressService.findOne(officeForm.getAddress()).get()
        );
        System.out.println(office);
        officeService.save(office);
        return "redirect:/admin/offices";
    }


    @PostMapping("/FindOfficeByDepartment")
    public String findByDepartment(@RequestParam String department, Model model) {
        model.addAttribute("offices", officeService.findByDepartment(department));
        return "/admin/admin_offices";

    }
}
