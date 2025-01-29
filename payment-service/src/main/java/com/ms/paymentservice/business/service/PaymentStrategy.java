package com.ms.paymentservice.business.service;

import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.entity.Payment;

public interface PaymentStrategy {

    Payment processPayment(OrderDTO order);
    
}
