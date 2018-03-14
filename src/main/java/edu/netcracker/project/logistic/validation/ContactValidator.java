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
        Contact contact = (Contact) o;

        String phoneNumber = contact.getPhoneNumber();
        phoneNumber = phoneNumber.replaceAll("[()-]", "").replaceAll("\\s+", "");
        contact.setPhoneNumber(phoneNumber);

        checkDuplicatesForPhoneNumberOrEmail(contact, errors);
        checkCorrectPhoneNumber(contact, errors);
    }

    private void checkDuplicatesForPhoneNumberOrEmail(Contact contact, Errors errors) {
        List<Contact> duplicates =
                contactDao.findByPhoneNumberOrEmail(contact.getPhoneNumber(), contact.getEmail());
        for (Contact d : duplicates) {
            if (!d.getContactId().equals(contact.getContactId()) && d.getEmail().equals(contact.getEmail())) {
                errors.rejectValue("email", "Duplicate.mail");
            } else if (!d.getContactId().equals(contact.getContactId()) && d.getPhoneNumber().equals(contact.getPhoneNumber())) {
                errors.rejectValue("phoneNumber", "Duplicate.phone");
            }
        }
    }

    private void checkCorrectPhoneNumber(Contact contact, Errors errors){
        String regex = "^\\+[1-9]{1}[0-9]{3,14}$";
        Matcher matcher = Pattern.compile(regex).matcher(contact.getPhoneNumber());
        if (!matcher.matches()){
            errors.rejectValue("phoneNumber", "Invalid.PhoneNumber");
        }
    }
}
