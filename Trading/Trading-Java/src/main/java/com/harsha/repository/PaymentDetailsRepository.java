package com.harsha.repository;

import com.harsha.model.PaymentDetails;
import com.harsha.service.PaymentDetailsService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

    PaymentDetails findByUserId(Long userId);
}
