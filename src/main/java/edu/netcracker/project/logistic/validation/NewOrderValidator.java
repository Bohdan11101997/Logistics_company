package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.OrderType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class NewOrderValidator implements Validator {
    private ContactDao contactDao;

    public NewOrderValidator(ContactDao contactDao) {
        this.contactDao = contactDao;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Order.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Order order = (Order) target;
        OrderType orderType = order.getOrderType();
        if (orderType == null) {
            errors.rejectValue("orderType", "Required.OrderType");
        }
        if (orderType != null && !orderType.getName().equals("Documents")) {
            if (order.getWeight() == null) {
                errors.rejectValue("weight", "Not.Empty");
            }
            if (order.getWidth() == null) {
                errors.rejectValue("width", "Not.Empty");
            }
            if (order.getHeight() == null) {
                errors.rejectValue("height", "Not.Empty");
            }
            if (order.getLength() == null) {
                errors.rejectValue("length", "Not.Empty");
            }
        }
        validateAddress("senderAddress", order.getSenderAddress(), errors);
        validateAddress("receiverAddress", order.getReceiverAddress(), errors);
        validateReceiverContact(order.getReceiverContact(), errors);
    }

    private void validateReceiverContact(Contact contact, Errors errors) {
        if (contact == null) {
            errors.rejectValue("receiverContact", "Required.Contact");
            return;
        }
        if (contact.getFirstName() == null || contact.getFirstName().isEmpty()) {
            errors.rejectValue("receiverContact.firstName", "Not.Empty");
        }
        if (contact.getLastName() == null || contact.getLastName().isEmpty()) {
            errors.rejectValue("receiverContact.lastName", "Not.Empty");
        }
        if (contact.getPhoneNumber() == null || contact.getPhoneNumber().isEmpty()) {
            errors.rejectValue("receiverContact.phoneNumber", "Not.Empty");
        }
        if (!contact.getPhoneNumber().matches("(\\+380)?\\d{9}")) {
            errors.rejectValue("receiverContact.phoneNumber", "Invalid.PhoneNumber");
        }
        List<Contact> duplicates =
                contactDao.findByPhoneNumberOrEmail(contact.getPhoneNumber(), contact.getEmail());
        for (Contact d : duplicates) {
            if (!d.getContactId().equals(contact.getContactId()) && d.getEmail().equals(contact.getEmail())) {
                errors.rejectValue("receiverContact.email", "Duplicate.mail");
            }
        }
    }

    private void validateAddress(String fieldName, Address address, Errors errors) {
        if (address.getLocation() == null) {
            errors.rejectValue(fieldName, "Address.Not.Exists");
        }
    }
}
