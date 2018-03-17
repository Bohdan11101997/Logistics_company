package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.dao.ContactDao;
import edu.netcracker.project.logistic.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ContactValidator implements Validator {
    private ContactDao contactDao;
    private Pattern phoneNumberRegex = Pattern.compile("^((\\+380)|0)*(?<phone>\\d{9})$");

    @Autowired
    public ContactValidator(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Contact.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        validate(o, errors, null);
    }

    public void validate(Object o, Errors errors, String fieldName) {
        String prefix = fieldName == null ? "" : fieldName + ".";

        Contact contact = (Contact) o;

        checkCorrectPhoneNumber(contact, errors, prefix);
        checkDuplicatesForPhoneNumberOrEmail(contact, errors, prefix);
    }

    private void checkDuplicatesForPhoneNumberOrEmail(Contact contact, Errors errors, String prefix) {
        List<Contact> duplicates =
                contactDao.findByPhoneNumberOrEmail(contact.getPhoneNumber(), contact.getEmail());
        for (Contact d : duplicates) {
            if (!d.getContactId().equals(contact.getContactId()) && d.getEmail().equals(contact.getEmail())) {
                errors.rejectValue(prefix + "email", "Duplicate.mail");
            }
        }
    }

    private void checkCorrectPhoneNumber(Contact contact, Errors errors, String prefix) {
        String phoneNumber = contact.getPhoneNumber();
        phoneNumber = phoneNumber.replaceAll("[()-]|\\s+", "");
        Matcher matcher = phoneNumberRegex.matcher(phoneNumber);
        if (!matcher.matches()) {
            errors.rejectValue(prefix + "phoneNumber", "Invalid.PhoneNumber");
        } else {
            String phone = matcher.group("phone");
            contact.setPhoneNumber(phone);
        }
    }
}
