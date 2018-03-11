package edu.netcracker.project.logistic.service;

import edu.netcracker.project.logistic.model.ResetPasswordForm;
import edu.netcracker.project.logistic.model.ResetPasswordToken;

import javax.mail.MessagingException;
import java.util.Optional;

public interface ResetPasswordService {

    void generateAndSendOnEmailResetToken(String email) throws MessagingException;

    Optional<ResetPasswordToken> findResetPasswordTokenByResetToken(String resetToken);

    void deleteResetPasswordTokenRowById(Long personId);
}
