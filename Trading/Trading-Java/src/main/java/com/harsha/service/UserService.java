package com.harsha.service;

import com.harsha.domain.VerificationType;
import com.harsha.model.User;
import org.springframework.stereotype.Service;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long userId) throws Exception;
    public User enableTwoFactorAuthentication(VerificationType verificationType,
                                              String sendTo,
                                              User user);
    public User updatePassword(User user,String newPassword);

}
