package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Notification;

public interface NotificationService {
    void send(String userName, Notification notification);
}
