package com.ms.paymentservice.service;

import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.entity.Payment;

public interface PaymentStrategy {

    Payment processPayment(OrderDTO order);
    
}
