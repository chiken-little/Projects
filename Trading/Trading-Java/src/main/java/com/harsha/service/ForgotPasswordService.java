package com.harsha.service;

import com.harsha.domain.VerificationType;
import com.harsha.model.ForgotPasswordToken;
import com.harsha.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user,
                                    String id, String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken (ForgotPasswordToken token);
}
