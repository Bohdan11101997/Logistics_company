package edu.netcracker.project.logistic.config.security;

import edu.netcracker.project.logistic.controllers.RegistrationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            logger.error("User is not authenticated! Access denied!");
        }

        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/error/403");

    }
}
