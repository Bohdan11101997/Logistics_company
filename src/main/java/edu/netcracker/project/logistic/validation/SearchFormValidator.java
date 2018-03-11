package edu.netcracker.project.logistic.validation;

import edu.netcracker.project.logistic.model.SearchForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SearchFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SearchForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SearchForm searchForm = (SearchForm) target;
        if (searchForm.getFrom() == null || searchForm.getTo() == null) {
            return;
        }
        boolean correctDateInterval = searchForm.getFrom().isBefore(searchForm.getTo());
        if (!correctDateInterval) {
            errors.rejectValue("from", "Not.valid.date_interval");
        }
    }
}
