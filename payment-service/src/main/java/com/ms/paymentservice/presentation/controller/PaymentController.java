package com.ms.paymentservice.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.ms.paymentservice.business.service.PaymentService;


public class PaymentController {


    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
 
}
