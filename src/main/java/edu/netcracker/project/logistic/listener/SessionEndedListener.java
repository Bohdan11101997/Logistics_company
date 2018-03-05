package edu.netcracker.project.logistic.listener;


import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SessionEndedListener implements ApplicationListener<SessionDestroyedEvent> {
    private final static Logger logger = LoggerFactory.getLogger(SessionEndedListener.class);

    private TaskProcessor taskProcessor;
    private PersonService personService;

    public SessionEndedListener(TaskProcessor taskProcessor, PersonService personService) {
        this.taskProcessor = taskProcessor;
        this.personService = personService;
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        boolean callCentreAgent = false;
        String username = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_CALL_CENTER")) {
                callCentreAgent = true;
                username = ((User) auth.getPrincipal()).getUsername();
            }
        }
        if (callCentreAgent) {
            Long employeeId = personService.findOne(username).map(Person::getId)
                    .orElse(null);
            taskProcessor.removeAgent(employeeId);
        }
    }
}
