package edu.netcracker.project.logistic.processing;

import edu.netcracker.project.logistic.dao.PersonCrudDao;
import edu.netcracker.project.logistic.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class InactiveUsersProcessor {
    private final Logger logger = LoggerFactory.getLogger(InactiveUsersProcessor.class);

    private PersonCrudDao personDao;
    private TaskProcessor taskProcessor;
    private RouteProcessor routeProcessor;

    public InactiveUsersProcessor(PersonCrudDao personDao, TaskProcessor taskProcessor, RouteProcessor routeProcessor) {
        this.personDao = personDao;
        this.taskProcessor = taskProcessor;
        this.routeProcessor = routeProcessor;
    }

    public void process(Authentication auth) {
        try {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_CALL_CENTER")) {
                    String username = ((User) auth.getPrincipal()).getUsername();
                    Long employeeId = personDao.findOne(username).map(Person::getId)
                            .orElse(null);
                    taskProcessor.removeAgent(employeeId);
                }
                if (authority.getAuthority().equals("ROLE_COURIER")) {
                    String username = ((User) auth.getPrincipal()).getUsername();
                    Long employeeId = personDao.findOne(username).map(Person::getId)
                            .orElse(null);
                    routeProcessor.removeCourier(employeeId);
                }
            }
        } catch (Exception ex) {
            logger.error("Error during removal of employee from processing services", ex);
        }
    }
}
