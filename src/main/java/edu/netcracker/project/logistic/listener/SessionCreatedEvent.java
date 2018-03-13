package edu.netcracker.project.logistic.listener;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.processing.TaskProcessor;
import edu.netcracker.project.logistic.processing.TaskProcessorCourier;
import edu.netcracker.project.logistic.service.PersonService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;


@Component
public class SessionCreatedEvent implements ApplicationListener<AuthenticationSuccessEvent> {
    private PersonService personService;
    private TaskProcessor taskProcessor;
    private TaskProcessorCourier taskProcessorCourier;

    public SessionCreatedEvent(PersonService personService, TaskProcessor taskProcessor,
                               TaskProcessorCourier taskProcessorCourier) {
        this.personService = personService;
        this.taskProcessor = taskProcessor;
        this.taskProcessorCourier = taskProcessorCourier;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_CALL_CENTER")) {
                String username = ((User) auth.getPrincipal()).getUsername();
                Long employeeId = personService.findOne(username).map(Person::getId)
                        .orElse(null);
                taskProcessor.addAgent(employeeId);
            }
            else if (authority.getAuthority().equals("ROLE_COURIER")) {
                String username = ((User) auth.getPrincipal()).getUsername();
                Long employeeId = personService.findOne(username).map(Person::getId)
                        .orElse(null);
                taskProcessorCourier.addCourier(employeeId);
            }
        }
    }
}
