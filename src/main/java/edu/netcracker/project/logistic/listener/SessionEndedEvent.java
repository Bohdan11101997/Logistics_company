package edu.netcracker.project.logistic.listener;

import edu.netcracker.project.logistic.processing.InactiveUsersProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionEndedEvent implements ApplicationListener<SessionDestroyedEvent> {
    private InactiveUsersProcessor inactiveUsersProcessor;

    public SessionEndedEvent(InactiveUsersProcessor inactiveUsersProcessor) {
        this.inactiveUsersProcessor = inactiveUsersProcessor;
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        List<SecurityContext> contexts =  event.getSecurityContexts();
        for (SecurityContext context: contexts) {
            Authentication auth = context.getAuthentication();
            if (auth == null) continue;
            inactiveUsersProcessor.process(auth);
        }
    }
}
