package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.OrderDao;
import edu.netcracker.project.logistic.dao.OrderDraftDao;
import edu.netcracker.project.logistic.dao.OrderStatusDao;
import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.OrderService;
import edu.netcracker.project.logistic.service.SecurityService;
import edu.netcracker.project.logistic.service.UserService;
import edu.netcracker.project.logistic.validation.CurrentPasswordValidator;
import edu.netcracker.project.logistic.validation.NewOrderValidator;
import edu.netcracker.project.logistic.validation.UpdateUserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private SmartValidator fieldValidator;
    private UpdateUserValidator updateUserValidator;
    private CurrentPasswordValidator currentPasswordValidator;
    private NewOrderValidator newOrderValidator;
    private UserService userService;
    private OrderService orderService;
    private SecurityService securityService;
    private OrderTypeDao orderTypeDao;
    private PasswordEncoder passwordEncoder;
    private OrderDao orderDao;
    private OrderStatusDao orderStatusDao;
    private OrderDraftDao orderDraftDao;

    @Autowired
    public UserController(SmartValidator fieldValidator, UpdateUserValidator updateUserValidator,
                          CurrentPasswordValidator currentPasswordValidator, NewOrderValidator newOrderValidator,
                          UserService userService, SecurityService securityService, OrderTypeDao orderTypeDao,
                          PasswordEncoder passwordEncoder, OrderService orderService, OrderDao orderDao,
                          OrderStatusDao orderStatusDao, OrderDraftDao orderDraftDao) {
        this.fieldValidator = fieldValidator;
        this.updateUserValidator = updateUserValidator;
        this.currentPasswordValidator = currentPasswordValidator;
        this.newOrderValidator = newOrderValidator;
        this.userService = userService;
        this.securityService = securityService;
        this.orderTypeDao = orderTypeDao;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
        this.orderDao = orderDao;
        this.orderStatusDao = orderStatusDao;
        this.orderDraftDao = orderDraftDao;
    }

    @GetMapping("/personal")
    public String viewPersonalArea(Model model, Principal principal) {

        String username = principal.getName();
        Optional<Person> optionalPerson = userService.findOne(username);

        if (!optionalPerson.isPresent()) {
            return "/error/403";
        }

        Person person = optionalPerson.get();
        UserForm userForm = getUserFormFilledWithPersonData(person);
        model.addAttribute("user", userForm);

        return "/user/user_personal_area";
    }

    private UserForm getUserFormFilledWithPersonData(Person person) {
        Contact contact = person.getContact();
        UserForm userForm = getUserFormFilledWithContactData(contact);
        userForm.setId(person.getId());
        userForm.setUserName(person.getUserName());
        return userForm;
    }

    private UserForm getUserFormFilledWithContactData(Contact contact) {
        UserForm userForm = new UserForm();
        Contact contactForUserForm = new Contact();
        contactForUserForm.setFirstName(contact.getFirstName());
        contactForUserForm.setLastName(contact.getLastName());
        contactForUserForm.setEmail(contact.getEmail());
        contactForUserForm.setPhoneNumber(contact.getPhoneNumber());
        userForm.setContact(contactForUserForm);
        return userForm;
    }

    @GetMapping(value = "/personal/{id}")
    public String changePersonalArea(@PathVariable Long id) {
        return "redirect:/user/personal";
    }

    @PostMapping(value = "/personal/{id}", params = "action=save")
    public String updatePersonalArea(@PathVariable Long id,
                                     @ModelAttribute("user") UserForm userForm,
                                     BindingResult bindingResult) {

        Optional<Person> optionalPerson = userService.findOne(id);

        if (!optionalPerson.isPresent()) {
            return "/error/403";
        }

        Person person = optionalPerson.get();
        String oldUsername = person.getUserName();
        person.setUserName(userForm.getUserName());

        Contact contact = person.getContact();
        Contact contactFromUserForm = userForm.getContact();
        contact.setFirstName(contactFromUserForm.getFirstName());
        contact.setLastName(contactFromUserForm.getLastName());
        contact.setEmail(contactFromUserForm.getEmail());
        contact.setPhoneNumber(contactFromUserForm.getPhoneNumber());

        fieldValidator.validate(userForm, bindingResult);
        updateUserValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/user/user_personal_area";
        }

        userService.update(person);

        if (!oldUsername.equals(person.getUserName())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            securityService.autoLogIn(person.getUserName(), (String) authentication.getCredentials());
        }

        return "redirect:/user/personal?save";
    }

    @PostMapping(value = "/personal/{id}", params = "action=delete")
    public String deletePersonalArea(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/login?delete";

    }

    @GetMapping(value = "/password/change")
    public String viewChangePassword(Model model) {
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        model.addAttribute("changePassword", changePasswordForm);
        return "user/user_change_password";
    }

    @PostMapping(value = "/password/change")
    public String saveNewPassword(@ModelAttribute("changePassword") ChangePasswordForm changePasswordForm,
                                  BindingResult bindingResult) {
        String currentPasswordFromForm = changePasswordForm.getOldPassword();
        currentPasswordValidator.validate(currentPasswordFromForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "user/user_change_password";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // change for single password
        Optional<Person> optionalPerson = userService.findOne(username);

        if (!optionalPerson.isPresent()) {
            return "/error/403";
        }

        Person person = optionalPerson.get();
        String newPassword = changePasswordForm.getNewPassword();
        String oldPassword = person.getPassword();

        if (!passwordEncoder.matches(newPassword, oldPassword)) {
            String newPasswordEncoded = passwordEncoder.encode(newPassword);
            person.setPassword(newPasswordEncoded);
            userService.update(person);
        } else {
            logger.error("Same old and new passwords");
            bindingResult.rejectValue("newPassword", "Password.Old.Equals.New");
            return "user/user_change_password";
        }

        securityService.autoLogIn(username, newPassword);

        return "redirect:/user/password/change?save";
    }

    @GetMapping(value = "/order", params = "!drafted")
    public String createOrder(Model model) {
        List<OrderType> orderTypes = orderTypeDao.findAll();
        if (orderTypes.size() < 1) {
            return "error/500";
        }
        Order order = new Order();
        order.setOrderType(orderTypes.get(0));
        model.addAttribute("orderTypes", orderTypes);
        model.addAttribute("order", order);
        return "user/user_order";
    }

    @GetMapping(value = "/order", params = {"drafted", "id"})
    public String createOrderFromDraft(Model model, @RequestParam String id) {
        List<OrderType> orderTypes = orderTypeDao.findAll();
        if (orderTypes.size() < 1) {
            return "error/500";
        }
        Long orderId;
        try {
            orderId = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            return "error/400";
        }
        Optional<OrderDraft> opt = orderDraftDao.findOne(orderId);
        if (!opt.isPresent()) {
            return "error/500";
        }
        OrderDraft draftedOrder = opt.get();
        model.addAttribute("orderTypes", orderTypes);
        Order order = draftedOrder.getDraft();
        order.setId(draftedOrder.getId());
        model.addAttribute("order", order);
        return "user/user_order";
    }

    @PostMapping(value = "/order", params = "!drafted")
    public String doCreateOrder(Model model, @ModelAttribute("order") Order order,
                                BindingResult result, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person user = opt.get();
        order.setSenderContact(user.getContact());

        newOrderValidator.validate(order, result);
        if (result.hasErrors()) {
            model.addAttribute("orderTypes", orderTypeDao.findAll());
            return "user/user_order";
        }

        orderService.createOrder(order);
        return "redirect:/main";
    }


    @PostMapping(value = "/order", params = {"drafted", "id"})
    public String doCreateOrderFromDraft(Model model, @ModelAttribute("order") Order draftedOrder,
                                         @RequestParam String id,
                                         BindingResult result, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person user = opt.get();
        newOrderValidator.validate(draftedOrder, result);
        if (result.hasErrors()) {
            model.addAttribute("orderTypes", orderTypeDao.findAll());
            return "user_order";
        }
        OrderDraft draft;
        try {
            Long orderId = Long.parseLong(id);
            draft = orderDraftDao.findOne(orderId)
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException ex) {
            return "error/400";
        }
        boolean isUserOrder = user.getId().equals(draft.getPersonId());
        if (!isUserOrder) {
            return "error/400";
        }
        draftedOrder.setSenderContact(user.getContact());
        orderService.createFromDraft(draft, draftedOrder);
        return "redirect:/main";
    }


    @PostMapping(value = "/order/delete", params = {"drafted", "id"})
    public String deleteDraft(Model model, @ModelAttribute("order") Order draftedOrder,
                                         @RequestParam String id,
                                         BindingResult result, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person user = opt.get();
        OrderDraft draft;
        try {
            Long orderId = Long.parseLong(id);
            draft = orderDraftDao.findOne(orderId)
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException ex) {
            return "error/400";
        }
        boolean isUserOrder = user.getId().equals(draft.getPersonId());
        if (!isUserOrder) {
            return "error/400";
        }
        orderDraftDao.delete(draft.getId());
        return "redirect:/main";
    }

    @GetMapping("/orders")
    public String getOrderByUser(Model model, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        Person user = opt.get();

        model.addAttribute("orders", orderDao.orderBySenderOrReceiver(user.getId()));
        model.addAttribute("destination_typeOrders", orderTypeDao.findAll());
        model.addAttribute("status_OrdersList", orderStatusDao.findAll());
        model.addAttribute("searchFormOrder", new SearchFormOrder());
        return "user/user_my_orders";
    }

    @PostMapping("/orders")
    public String searchOrdersByUser(@ModelAttribute("searchFormOrder") SearchFormOrder searchFormOrder, Model model, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person user = opt.get();
        List<Order> orders = orderDao.search(searchFormOrder, user.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("destination_typeOrders", orderTypeDao.findAll());
        model.addAttribute("status_OrdersList", orderStatusDao.findAll());
        model.addAttribute("searchFormOrder", searchFormOrder);
        return "user/user_my_orders";
    }

    @GetMapping("/orders/draft")
    public String draftOrders(Model model, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Long userId = opt.get().getId();
        List<OrderDraft> drafts = orderDraftDao.findByPersonId(userId);
        List<Order> orders = drafts.stream()
                .map(d -> {
                    d.getDraft().setId(d.getId());
                    return d.getDraft();
                })
                .collect(Collectors.toList());

        model.addAttribute("orders", orders);
        return "user/user_draft_orders";
    }

    @PostMapping("/order/draft")
    public String draftOrder(@ModelAttribute("order") Order order, Principal principal) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person user = opt.get();
        order.setSenderContact(user.getContact());

        OrderDraft orderDraft = new OrderDraft();
        orderDraft.setPersonId(user.getId());
        orderDraft.setDraft(order);

        orderService.draft(orderDraft);
        return "redirect:/main";
    }

    @PostMapping(value = "/order/draft", params = {"drafted", "id"})
    public String updateDraftOrder(@ModelAttribute("order") Order order, Principal principal,
                                   @RequestParam String id) {
        Optional<Person> opt = userService.findOne(principal.getName());
        if (!opt.isPresent()) {
            return "error/500";
        }
        Person user = opt.get();
        order.setSenderContact(user.getContact());

        OrderDraft orderDraft = new OrderDraft();
        try {
            orderDraft.setId(Long.parseLong(id));
        } catch (NumberFormatException ex) {
            return "error/400";
        }
        orderDraft.setPersonId(user.getId());
        orderDraft.setDraft(order);
        orderService.draft(orderDraft);
        return "redirect:/main";
    }

    @GetMapping("/order/update/{id}")
    public String updateOrder(@PathVariable long id, Model model) {
        model.addAttribute("order", orderDao.findOne(id));
        return "/user/order";
    }

    @GetMapping("/order/delete/{id}")
    public String deleteOffice(@PathVariable Long id) {
        orderDao.delete(id);
        return "redirect:/user/orders";
    }

    @GetMapping(value = "")
    public String viewSentOrders() {
        return "user/user_order_history";
    }
}
