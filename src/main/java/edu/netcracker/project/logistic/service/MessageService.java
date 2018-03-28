package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.Message;

public interface MessageService {

    boolean sendMessage(Message message);

}
