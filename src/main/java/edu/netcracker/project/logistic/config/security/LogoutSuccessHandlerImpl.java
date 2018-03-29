package edu.netcracker.project.logistic.config.security;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.processing.InactiveUsersProcessor;
import edu.netcracker.project.logistic.processing.RouteProcessor;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class LogoutSuccessHandlerImpl extends SimpleUrlLogoutSuccessHandler implements LogoutHandler {
    private PersonService personService;
    private InactiveUsersProcessor inactiveUsersProcessor;

    public LogoutSuccessHandlerImpl(InactiveUsersProcessor inactiveUsersProcessor) {
        this.inactiveUsersProcessor = inactiveUsersProcessor;
    }

    public PersonService getPersonService() {
        return personService;
    }

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return;
        inactiveUsersProcessor.process(auth);
    }
}
