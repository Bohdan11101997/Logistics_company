package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.model.Address;
import edu.netcracker.project.logistic.model.Contact;
import edu.netcracker.project.logistic.model.Order;
import edu.netcracker.project.logistic.model.OrderType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class NewOrderValidator implements Validator {
    private ContactValidator contactValidator;

    public NewOrderValidator(ContactValidator contactValidator) {
        this.contactValidator = contactValidator;
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
        } else if (!orderType.getName().equals("Documents")) {
            validateWeight(order, errors, orderType);
            validateWidth(order, errors, orderType);
            validateHeight(order, errors, orderType);
            validateLength(order, errors, orderType);
        } else {
            if (order.getWeight() != null || order.getWidth() != null ||
                    order.getHeight() != null || order.getLength() != null) {
                errors.rejectValue("orderType", "Invalid.Constraints.OrderType.Documents");
            }
        }

        validateAddress("senderAddress", order.getSenderAddress(), errors);
        validateAddress("receiverAddress", order.getReceiverAddress(), errors);

        Contact receiverContact = order.getReceiverContact();
        if (receiverContact == null) {
            errors.rejectValue("receiverContact", "Required.Contact");
            return;
        }
        contactValidator.validate(receiverContact, errors, "receiverContact");
    }

    private void validateWeight(Order order, Errors errors, OrderType orderType) {
        if (order.getWeight() == null) {
            errors.rejectValue("weight", "Required.Order.Weight");
        } else if (order.getWeight().compareTo(orderType.getMaxWeight()) > 0) {
            errors.rejectValue("weight", "Exceeded.Order.Weight", new Object[]{orderType.getMaxWeight()}, null);
        }
    }

    private void validateWidth(Order order, Errors errors, OrderType orderType) {
        if (order.getWidth() == null) {
            errors.rejectValue("width", "Required.Order.Width");
        } else if (order.getWidth().compareTo(orderType.getMaxWidth()) > 0) {
            errors.rejectValue("width", "Exceeded.Order.Width", new Object[]{orderType.getMaxWidth().toString()}, null);
        }
    }

    private void validateHeight(Order order, Errors errors, OrderType orderType) {
        if (order.getHeight() == null) {
            errors.rejectValue("height", "Required.Order.Height");
        } else if (order.getHeight().compareTo(orderType.getMaxHeight()) > 0) {
            errors.rejectValue("height", "Exceeded.Order.Height", new Object[]{orderType.getMaxHeight().toString()}, null);
        }
    }

    private void validateLength(Order order, Errors errors, OrderType orderType) {
        if (order.getLength() == null) {
            errors.rejectValue("length", "Required.Order.Length");
        } else if (order.getLength().compareTo(orderType.getMaxLength()) > 0) {
            errors.rejectValue("length", "Exceeded.Order.Length", new Object[]{orderType.getMaxLength().toString()}, null);
        }
    }

    private void validateAddress(String fieldName, Address address, Errors errors) {
        if (address.getLocation() == null) {
            errors.rejectValue(fieldName, "Address.Not.Exists");
        }
    }
}
