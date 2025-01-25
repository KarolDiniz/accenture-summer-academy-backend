package com.ms.paymentservice.service;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.dto.OrderDTO;

public interface PaymentStrategy {
    Payment processPayment(OrderDTO order);
}
