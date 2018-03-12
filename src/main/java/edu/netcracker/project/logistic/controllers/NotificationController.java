package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.model.Notification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    @MessageMapping("/username")
    @SendTo("/topic/username")
    public Notification sendUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("Can't get user auth data");
        }
        Notification usernameMsg = new Notification(
                "username",
                auth.getName()
        );
        return usernameMsg;
    }

    @MessageMapping("/echo")
    @SendToUser("/topic/echo")
    public Notification sendEchoNotification() {
        Notification notification = new Notification("echo", "");
        return notification;
    }
}