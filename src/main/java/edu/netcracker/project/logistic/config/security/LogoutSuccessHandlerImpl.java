package edu.netcracker.project.logistic.config.security;

import edu.netcracker.project.logistic.model.Person;
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
    private TaskProcessor taskProcessor;
    private RouteProcessor routeProcessor;

    public LogoutSuccessHandlerImpl(TaskProcessor taskProcessor, RouteProcessor routeProcessor) {
        this.taskProcessor = taskProcessor;
        this.routeProcessor = routeProcessor;
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
        try {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_CALL_CENTER")) {
                    String username = ((User) auth.getPrincipal()).getUsername();
                    Long employeeId = personService.findOne(username).map(Person::getId)
                            .orElse(null);
                    taskProcessor.removeAgent(employeeId);
                }
                if (authority.getAuthority().equals("ROLE_COURIER")) {
                    String username = ((User) auth.getPrincipal()).getUsername();
                    Long employeeId = personService.findOne(username).map(Person::getId)
                            .orElse(null);
                    routeProcessor.removeCourier(employeeId);
                }
            }
        } catch (Exception ex) {
            logger.error("Error during removal of employee from processing services", ex);
        }
    }
}
