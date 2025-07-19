package com.harsha.service;

import com.harsha.model.PaymentDetails;
import com.harsha.model.User;

public interface PaymentDetailsService {

    public PaymentDetails addPaymentDetails(String accountNumber,
                                            String accountHolderName,
                                            String ifsc,
                                            String bankName,
                                            User user);

    public  PaymentDetails getUsersPaymentDetails(User user);

}
