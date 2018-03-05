package edu.netcracker.project.logistic.listener;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.stereotype.Component;

import java.security.Principal;


@Component
public class SessionCreatedEvent implements ApplicationListener<HttpSessionCreatedEvent> {
    private PersonService personService;
    private TaskProcessor taskProcessor;

    public SessionCreatedEvent(PersonService personService, TaskProcessor taskProcessor) {
        this.personService = personService;
        this.taskProcessor = taskProcessor;
    }

    @Override
    public void onApplicationEvent(HttpSessionCreatedEvent event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean callCenterAgent = false;
        String username = null;
        for (GrantedAuthority authority: auth.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_CALL_CENTER")) {
                callCenterAgent = true;
                username = ((User)auth.getPrincipal()).getUsername();
            }
        }
        if (callCenterAgent) {
            Long employeeId = personService.findOne(username).map(Person::getId)
                    .orElse(null);
            taskProcessor.addAgent(employeeId);
        }
    }
}
