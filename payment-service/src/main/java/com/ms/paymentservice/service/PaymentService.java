package com.ms.paymentservice.service;

import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.dto.PaymentDTO;
import com.ms.paymentservice.model.exception.PaymentProcessingException;

public interface PaymentService {

    public abstract PaymentDTO executePaymentProcessing(OrderDTO order) throws PaymentProcessingException;
    
}
