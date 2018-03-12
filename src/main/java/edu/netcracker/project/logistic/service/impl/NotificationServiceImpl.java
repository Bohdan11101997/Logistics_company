package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.model.Notification;
import edu.netcracker.project.logistic.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl implements NotificationService {
    private SimpMessagingTemplate simpMessagingTemplate;

    public NotificationServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void send(String userName, Notification notification) {
        simpMessagingTemplate.convertAndSendToUser(
                userName,
                "/topic/app",
                notification
        );
    }
}
