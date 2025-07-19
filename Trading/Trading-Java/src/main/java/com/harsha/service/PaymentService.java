package com.harsha.service;

import com.harsha.domain.PaymentMethod;
import com.harsha.model.PaymentOrder;
import com.harsha.model.User;
import com.harsha.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount,
                             PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException,StripeException;

    PaymentResponse createRazorPaymentLink(User user, Long amount, Long orderId) throws RazorpayException;

    PaymentResponse createStripePaymentLink(User user, Long amount,Long orderId) throws StripeException;


}
