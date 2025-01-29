package com.ms.paymentservice.business.service;

import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.dto.PaymentDTO;
import com.ms.paymentservice.domain.model.exception.PaymentProcessingException;

public interface PaymentService {

    public abstract PaymentDTO executePaymentProcessing(OrderDTO order) throws PaymentProcessingException;
    
}
