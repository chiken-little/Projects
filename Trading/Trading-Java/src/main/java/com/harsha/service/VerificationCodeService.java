package com.harsha.service;

import com.harsha.domain.VerificationType;
import com.harsha.model.User;
import com.harsha.model.VerificationCode;

public interface VerificationCodeService {

    VerificationCode sendVerificationCodeOtp(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByUser(Long userId);


    void deleteVerificationCodeById(VerificationCode verificationCode);
}
